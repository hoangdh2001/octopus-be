import {
  Body,
  Controller,
  Get,
  Post,
  Query,
  Headers,
  UseFilters,
  HttpException,
  HttpStatus,
  Param,
  Inject,
  OnModuleInit,
  OnModuleDestroy,
  HttpCode,
  UseInterceptors,
  UploadedFiles,
  BadRequestException,
  UploadedFile,
} from '@nestjs/common';
import { ChannelService } from '../services/channel.service';
import { JwtService } from '@nestjs/jwt';
import {
  ChannelDTO,
  ChannelPaginationParams,
  CreateChannelDTO,
} from 'src/dtos/channel.dto';
import { ChannelExceptionFilter } from 'src/exceptions/channel.exception';
import { v4 } from 'uuid';
import { Channel, ChannelMember } from 'src/models/channel.model';
import { Pagination } from 'src/dtos/pagination.dto';
import { convertChannelDTO } from 'src/utils/channel.utils';
import { MessageServive } from 'src/services/message.service';
import {
  AttachmentDTO,
  MessagePaginationParams,
  SendMessageParams,
} from 'src/dtos/message.dto';
import {
  convertMessageDTO,
  convertQuotedMessage,
} from 'src/utils/message.util';
import { Message } from 'src/models/message.model';
import { EventsGateway } from 'src/listeners/events.gateway';
import { KafkaService } from '@rob3000/nestjs-kafka';
import { HttpService } from '@nestjs/axios';
import { DeviceDTO, UserDTO } from 'src/dtos/user.dto';
import { FirebaseMessagingService } from '@aginix/nestjs-firebase-admin';
import {
  AnyFilesInterceptor,
  FileFieldsInterceptor,
  FileInterceptor,
} from '@nestjs/platform-express';
import FormData from 'form-data';
import { createReadStream } from 'streamifier';
import { EventDTO } from 'src/dtos/event.dto';

@Controller('/channels')
@UseFilters(new ChannelExceptionFilter())
@UseInterceptors(FileInterceptor('file'))
export class ChannelController implements OnModuleInit, OnModuleDestroy {
  constructor(
    private readonly channelService: ChannelService,
    private readonly jwtService: JwtService,
    private readonly messageService: MessageServive,
    private readonly eventsGateway: EventsGateway,
    private readonly httpService: HttpService,
    @Inject('MESSAGE_SERVICE') private readonly client: KafkaService,
    private readonly firebaseMessaging: FirebaseMessagingService,
  ) {}

  async onModuleDestroy() {
    await this.client.disconnect();
  }

  async onModuleInit() {
    await this.client.connect();
  }

  @Get('/search')
  @HttpCode(200)
  async findAllByUser(
    @Query() { userID, skip = 0, limit = 10 }: ChannelPaginationParams,
    @Headers('Authorization') token?: string,
  ): Promise<Pagination> {
    const totalItem = await this.channelService.countByUserID(userID);
    const totalPage = Math.floor(totalItem / limit) + 1;
    const channels = await this.channelService.findAllByUser(
      userID,
      skip,
      limit,
    );

    const data = await Promise.all(
      channels.map(async (channel) => {
        const messages = await this.messageService.findAllByChannel({
          channelID: channel._id,
        });
        return convertChannelDTO({
          channel,
          userID,
          messages,
          callUser: async (userID) => {
            const response = await this.httpService.axiosRef.get<UserDTO>(
              `http://auth-service/api/users/${userID}`,
              { headers: { Authorization: token } },
            );
            return response.data;
          },
          attachments: async (attachmentID) => {
            const response = await this.httpService.axiosRef.get<AttachmentDTO>(
              `http://storage-service/api/storage/attachments/${attachmentID}`,
              { headers: { Authorization: token } },
            );
            return response.data;
          },
          callQuotedMessage: async (messageID) => {
            const message = await this.messageService.findMessageById(
              messageID,
            );
            return message;
          },
        });
      }),
    );

    return {
      skip: Number.parseInt(skip.toString()),
      limit: Number.parseInt(limit.toString()),
      totalItem,
      totalPage,
      data,
    };
  }

  @Post()
  @HttpCode(201)
  async createChannel(
    @Body() createChannelDTO: CreateChannelDTO,
    @Headers('Authorization') token?: string,
  ) {
    let { newMembers, name, userID } = createChannelDTO;
    if (!userID || userID.trim().length === 0) {
      const { id }: { id: string } = this.jwtService.decode(
        token?.split(' ')[1] || '',
      ) as any;
      userID = id;
    }
    if (newMembers.length < 2) {
      throw new HttpException(
        { message: 'Members must is equal 2 or longer than' },
        HttpStatus.BAD_REQUEST,
      );
    }
    newMembers = [userID, ...newMembers];

    const channel: Channel = {
      _id: v4(),
      name: name,
      members: newMembers.map((member) => ({ userID: member })),
      createdBy: userID,
    };

    const newChannel = await this.channelService.createChannel(channel);

    return await convertChannelDTO({
      channel: newChannel,
      userID,
      messages: [],
      callUser: async (userID) => {
        const response = await this.httpService.axiosRef.get<UserDTO>(
          `http://auth-service/api/users/${userID}`,
          { headers: { Authorization: token } },
        );
        return response.data;
      },
      attachments: [],
      callQuotedMessage: async (messageID) => {
        const message = await this.messageService.findMessageById(messageID);
        return message;
      },
    });
  }

  @Post('/:channelID/query')
  async queryChannelByID(
    @Body()
    {
      messages: { skip, limit = 30, id_gt, id_gte, id_lt, id_lte, id_around },
    }: {
      messages: MessagePaginationParams;
    },
    @Query('userID') userID: string,
    @Param('channelID') channelID: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id }: { id: string } = this.jwtService.decode(
        token?.split(' ')[1] || '',
      ) as any;
      userID = id;
    }
    const channel = await this.channelService.findChannelByID(channelID);

    var messagesData: Message[];

    if (id_gt) {
      messagesData = await this.messageService.findAllByChannel({
        channelID: channelID,
        messageID: id_gt,
        limit: limit,
        condition: 'gt',
      });
    } else if (id_gte) {
      messagesData = await this.messageService.findAllByChannel({
        channelID: channelID,
        messageID: id_gte,
        limit: limit,
        condition: 'gte',
      });
    } else if (id_lt) {
      messagesData = await this.messageService.findAllByChannel({
        channelID: channelID,
        messageID: id_lt,
        limit: limit,
        condition: 'lt',
      });
    } else if (id_lte) {
      messagesData = await this.messageService.findAllByChannel({
        channelID: channelID,
        messageID: id_lte,
        limit: limit,
        condition: 'lte',
      });
    } else if (id_around) {
      messagesData = await this.messageService.queryAroundMessage({
        channelID: channelID,
        messageID: id_around,
        limit: limit,
      });
    } else {
      messagesData = await this.messageService.findAllByChannel({
        channelID: channelID,
        skip: skip,
        limit: limit,
      });
    }

    return await convertChannelDTO({
      channel: channel,
      userID,
      messages: messagesData,
      callUser: async (userID) => {
        const response = await this.httpService.axiosRef.get<UserDTO>(
          `http://auth-service/api/users/${userID}`,
          { headers: { Authorization: token } },
        );
        return response.data;
      },
      attachments: async (attachmentID) => {
        const response = await this.httpService.axiosRef.get<AttachmentDTO>(
          `http://storage-service/api/storage/attachments/${attachmentID}`,
          { headers: { Authorization: token } },
        );
        return response.data;
      },
      callQuotedMessage: async (messageID) => {
        const message = await this.messageService.findMessageById(messageID);
        return message;
      },
    });
  }

  @Post('/:channelID/messages')
  @HttpCode(201)
  async sendMessage(
    @Param('channelID') channelID: string,
    @Body()
    {
      senderID,
      _id,
      text,
      attachments = [],
      quotedMessageID,
    }: SendMessageParams,
    @Headers('Authorization') token?: string,
    @Query('userID') userID?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }

    const hasFile = attachments.length > 0;

    const message: Message = {
      _id: _id || v4(),
      senderID: senderID || userID,
      status: 'READY',
      type: 'NORMAL',
      channelID: channelID,
      text: text,
      attachments: attachments.map((attachments) => attachments._id),
      quotedMessageID: quotedMessageID,
    };

    const newMessage = await this.messageService.createMessage(
      channelID,
      message,
    );

    const messageDTO = await convertMessageDTO({
      message: newMessage,
      attachments: async (attachmentID) => {
        const response = await this.httpService.axiosRef.get<AttachmentDTO>(
          `http://storage-service/api/storage/attachments/${attachmentID}`,
          { headers: { Authorization: token } },
        );
        return response.data;
      },
      callUser: async (userID) => {
        const response = await this.httpService.axiosRef.get<UserDTO>(
          `http://auth-service/api/users/${userID}`,
          { headers: { Authorization: token } },
        );
        return response.data;
      },
      callQuotedMessage: async (messageID) => {
        const quotedMessage = await this.messageService.findMessageById(
          messageID,
        );
        return await convertQuotedMessage({
          message: quotedMessage,
          callUser: async (userID) => {
            const response = await this.httpService.axiosRef.get<UserDTO>(
              `http://auth-service/api/users/${userID}`,
              { headers: { Authorization: token } },
            );
            return response.data;
          },
          attachments: async (attachmentID) => {
            const response = await this.httpService.axiosRef.get<AttachmentDTO>(
              `http://storage-service/api/storage/attachments/${attachmentID}`,
              { headers: { Authorization: token } },
            );
            return response.data;
          },
        });
      },
    });

    const channel: Channel = await this.channelService.findChannelByID(
      channelID,
    );

    const channelDTO: ChannelDTO = await convertChannelDTO({
      channel: channel,
      userID,
      messages: [],
      callUser: async (userID) => {
        const response = await this.httpService.axiosRef.get<UserDTO>(
          `http://auth-service/api/users/${userID}`,
          { headers: { Authorization: token } },
        );
        return response.data;
      },
      callQuotedMessage: async (messageID) => {
        const message = await this.messageService.findMessageById(messageID);
        return message;
      },
    });

    const otherMembers = channelDTO.members.filter(
      (member) => member.userID != userID,
    );

    const deviceDTO: DeviceDTO[][] = await Promise.all(
      otherMembers.map(async (member): Promise<DeviceDTO[]> => {
        const response = await this.httpService.axiosRef.get<DeviceDTO[]>(
          `http://auth-service/api/users/${member.userID}/devices`,
          { headers: { Authorization: token } },
        );
        return response.data;
      }),
    );

    const devices = deviceDTO
      .reduce((previousValue, currentValue) => {
        return [...previousValue, ...currentValue];
      }, [])
      .map((device) => device.deviceID);

    if (devices.length != 0) {
      var channelName = '';
      if (channelDTO.channel.name.length) {
        channelName = channelDTO.channel.name;
      } else {
        if (otherMembers.length != 0) {
          if (otherMembers.length == 1) {
            const user = otherMembers[0].user;
            if (user != null) {
              channelName = `${user.firstName} ${user.lastName}`;
            }
          } else {
            channelName = `${otherMembers
              .map((e) => e.user?.lastName)
              .join(', ')}`;
          }
        }
      }

      const isGroup = channelDTO.members.length > 2;

      const sender = channelDTO.members.find(
        (member) => member.userID === userID,
      );
      const senderName = `${sender.user.firstName} ${sender.user.lastName}`;

      try {
        const messageResponse = await this.firebaseMessaging.sendMulticast({
          tokens: devices,
          data: {
            channelID: channelID,
          },
          notification: {
            title: channelName,
            body: hasFile
              ? `${isGroup ? `${senderName}: ` : ''}Đã gửi ${
                  attachments.length
                } ảnh`
              : messageDTO.text,
            imageUrl: hasFile ? attachments[0].url : null,
          },
          android: {
            priority: 'high',
            data: {
              channelID: channelID,
            },
            ttl: 60 * 60 * 24,
            notification: {
              title: channelName,
              body: hasFile
                ? `${isGroup ? `${senderName}: ` : ''}Đã gửi ${
                    attachments.length
                  } ảnh`
                : messageDTO.text,
              imageUrl: hasFile ? attachments[0].url : null,
              priority: 'high',
            },
          },
          apns: {
            payload: {
              aps: {
                aps: {
                  'mutable-content': 1,
                },
                alert: {
                  title: channelName,
                  body: hasFile
                    ? `${isGroup ? `${senderName}: ` : ''}Đã gửi ${
                        attachments.length
                      } ảnh`
                    : messageDTO.text,
                  launchImage: hasFile ? attachments[0].url : null,
                },
              },
            },
            fcmOptions: {
              imageUrl: hasFile ? attachments[0].url : null,
            },
          },
        });
        console.log(messageResponse);
        if (messageResponse.failureCount > 0) {
          const failedTokens = [];
          messageResponse.responses.forEach((resp, idx) => {
            if (!resp.success) {
              failedTokens.push(devices[idx]);
            }
          });
          console.log('List of tokens that caused failures: ' + failedTokens);
        }
      } catch (error) {
        console.log(error);
      }
    }

    this.eventsGateway.sendMessage({
      type: 'message.new',
      message: messageDTO,
      channelID: messageDTO.channelID,
    });

    return messageDTO;
  }

  @Post('/:channelID/file')
  @HttpCode(201)
  async uploadFile(
    @Body() { attachmentID }: { attachmentID: string },
    @UploadedFile() file: Express.Multer.File,
    @Headers('Authorization') token?: string,
  ) {
    if (file) {
      const formData = new FormData();
      formData.append('file', createReadStream(file.buffer), {
        filename: file.originalname,
        contentType: file.mimetype,
        filepath: file.path,
      });
      formData.append('attachmentID', attachmentID);
      const response = await this.httpService.axiosRef.post<AttachmentDTO[]>(
        `http://storage-service/api/storage/upload`,
        formData,
        {
          headers: {
            Authorization: token,
            'Content-Type': 'multipart/form-data',
          },
        },
      );
      return response.data;
    }
    throw new BadRequestException('Invalid file type');
  }

  @Post('/:channelID/image')
  @HttpCode(201)
  async uploadImage(
    @Body() { attachmentID }: { attachmentID: string },
    @UploadedFile() file: Express.Multer.File,
    @Headers('Authorization') token?: string,
  ) {
    if (file) {
      const formData = new FormData();
      formData.append('file', createReadStream(file.buffer), {
        filename: file.originalname,
        contentType: file.mimetype,
        filepath: file.path,
      });
      formData.append('attachmentID', attachmentID);
      const response = await this.httpService.axiosRef.post<AttachmentDTO[]>(
        `http://storage-service/api/storage/upload`,
        formData,
        {
          headers: {
            Authorization: token,
            'Content-Type': 'multipart/form-data',
          },
        },
      );
      return response.data;
    }
    throw new BadRequestException('Invalid file type');
  }

  @Post('/:channelID/event')
  async sendEvent(
    @Param('channelID') channelID: string,
    @Body() event: EventDTO,
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }

    const response = await this.httpService.axiosRef.get<UserDTO>(
      `http://auth-service/api/users/${userID}`,
      { headers: { Authorization: token } },
    );
    const user = response.data;

    this.eventsGateway.sendMessage({
      type: event.type,
      channelID: channelID,
      user: user,
    });
  }

  @Post('/:channelID/read')
  async markRead(
    @Param('channelID') channelID: string,
    @Body() { messageID }: { messageID: string },
  ) {}
}
