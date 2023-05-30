import {
  Body,
  Controller,
  Get,
  Post,
  Query,
  Headers,
  HttpException,
  HttpStatus,
  Param,
  Inject,
  OnModuleInit,
  OnModuleDestroy,
  HttpCode,
  UseInterceptors,
  BadRequestException,
  UploadedFile,
  Delete,
  Put,
} from '@nestjs/common';
import { ChannelService } from '../services/channel.service';
import { JwtService } from '@nestjs/jwt';
import {
  ChannelDTO,
  ChannelPaginationParams,
  CreateChannelDTO,
  Payload,
  SortOption,
} from 'src/dtos/channel.dto';
import { ChannelExceptionFilter } from 'src/exceptions/channel.exception';
import { v4 } from 'uuid';
import { Channel, ChannelMember } from 'src/models/channel.model';
import { Pagination } from 'src/dtos/pagination.dto';
import {
  convertChannelDTO,
  convertChannelModel,
  convertMemberDTO,
  convertReadDTO,
} from 'src/utils/channel.utils';
import { MessageServive } from 'src/services/message.service';
import {
  AttachmentDTO,
  MessageDTO,
  MessagePaginationParams,
  Reaction,
  SendMessageParams,
} from 'src/dtos/message.dto';
import {
  convertMessageDTO,
  convertQuotedMessage,
  convertReaction,
} from 'src/utils/message.util';
import { Message, MessageReaction } from 'src/models/message.model';
import { EventsGateway } from 'src/listeners/events.gateway';
import { KafkaService } from '@rob3000/nestjs-kafka';
import { HttpService } from '@nestjs/axios';
import { DeviceDTO, OwnUserDTO, UserDTO } from 'src/dtos/user.dto';
import { FirebaseMessagingService } from '@aginix/nestjs-firebase-admin';
import { FileInterceptor } from '@nestjs/platform-express';
import FormData from 'form-data';
import { createReadStream } from 'streamifier';
import { EventDTO } from 'src/dtos/event.dto';
import { SortOrder, UpdateQuery } from 'mongoose';
import { PaginationParam } from 'src/dtos/pagination_param';

@Controller('/channels')
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

  @Get()
  @HttpCode(200)
  async findAllByUser(
    @Query('payload') payload: string,
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ): Promise<Pagination> {
    const { filter_conditions, sort, limit, offset }: Payload =
      JSON.parse(payload);
    const totalItem = await this.channelService.countByUserID(userID);
    const totalPage = Math.floor(totalItem / limit) + 1;
    const sortConvert: { [key: string]: SortOrder } = {};

    console.log(filter_conditions);

    sort?.forEach((x) => {
      sortConvert[x.field] = x.direction;
    });

    const channels = await this.channelService.search(filter_conditions, {
      sort: sortConvert,
      limit: limit,
      offset: offset,
    });

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
            const response = await this.httpService.axiosRef.get<{
              attachment: AttachmentDTO;
            }>(
              `http://storage-service/api/storage/attachments/${attachmentID}`,
              { headers: { Authorization: token } },
            );
            return response.data.attachment;
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
      skip: offset,
      limit: limit,
      totalItem,
      totalPage,
      data,
    };
  }

  @Get('/search')
  async search(
    @Query('payload') payload: string,
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id }: { id: string } = this.jwtService.decode(
        token?.split(' ')[1] || '',
      ) as any;
      userID = id;
    }
    const payloadConvert: Payload = JSON.parse(payload);

    const sort: { [key: string]: SortOrder } = {};

    payloadConvert.sort.forEach((x) => {
      sort[x.field] = x.direction;
    });

    const channels = await this.channelService.search(
      payloadConvert.filter_conditions,
      {},
    );

    let data: Message[] = [];

    await Promise.all(
      channels.map(async (channel) => {
        const messages = await this.messageService.seachMessage(
          channel._id,
          payloadConvert.message_filter_conditions,
          {
            sort: sort,
          },
        );
        data = [...data, ...messages];
      }),
    );

    const getMessagesResponse = await Promise.all(
      data.map(async (message) => {
        const messageDTO = await convertMessageDTO({
          userID,
          message: message,
          callUser: async (userID) => {
            const response = await this.httpService.axiosRef.get<UserDTO>(
              `http://auth-service/api/users/${userID}`,
              { headers: { Authorization: token } },
            );
            return response.data;
          },
          attachments: async (attachmentID) => {
            const response = await this.httpService.axiosRef.get<{
              attachment: AttachmentDTO;
            }>(
              `http://storage-service/api/storage/attachments/${attachmentID}?filter=${JSON.stringify(
                payloadConvert.attachment_filter_conditions,
              )}`,
              { headers: { Authorization: token } },
            );
            return response.data.attachment;
          },
          callQuotedMessage: async (messageID) => {
            const message = await this.messageService.findMessageById(
              messageID,
            );
            return message;
          },
        });

        const channelModel = await convertChannelModel({
          userID,
          channel: channels.find(
            (channel) => channel._id === message.channelID,
          ),
          callUser: async (userID) => {
            const response = await this.httpService.axiosRef.get<UserDTO>(
              `http://auth-service/api/users/${userID}`,
              { headers: { Authorization: token } },
            );
            return response.data;
          },
        });

        return {
          message: messageDTO,
          channel: channelModel,
        };
      }),
    );

    const messageNoEmptyAttachment = getMessagesResponse.filter(
      (message) =>
        message.message.attachments.length > 0 &&
        !message.message.attachments.includes(null),
    );

    return {
      results:
        payloadConvert.attachment_filter_conditions != null &&
        payloadConvert.attachment_filter_conditions != undefined
          ? messageNoEmptyAttachment
          : getMessagesResponse,
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
    if (newMembers.length < 1) {
      throw new HttpException(
        { message: 'Members must is equal 1 or longer than' },
        HttpStatus.BAD_REQUEST,
      );
    }
    newMembers = [userID, ...newMembers];
    newMembers = [...new Set(newMembers)];

    const channel: Channel = {
      _id: v4(),
      name: name,
      members: newMembers.map((member) => ({
        userID: member,
        lastRead: Date().toString(),
      })),
      createdBy: userID,
    };

    const newChannel = await this.channelService.saveChannel(channel);

    const channelDTO = await convertChannelDTO({
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

    const response = await this.httpService.axiosRef.get<OwnUserDTO[]>(
      `http://auth-service/api/users/ownUsers?users=${newMembers.join(',')}`,
      {
        headers: {
          Authorization: token,
        },
      },
    );

    await this.eventsGateway.subcribeChannel(response.data);

    this.eventsGateway.sendMessage({
      type: 'channel.truncated',
      channel: channelDTO,
      channelID: channelDTO.channel._id,
    });

    if (newMembers.length > 2) {
      const currentUser = channelDTO.members.find(
        (member) => member.userID == userID,
      ).user;

      const messageCreated: Message = {
        _id: v4(),
        senderID: userID,
        status: 'READY',
        type: 'SYSTEM_CREATED_CHANNEL',
        channelID: newChannel._id,
        text: `${currentUser.firstName} ${currentUser.lastName}`,
      };

      const messageAddMember: Message = {
        _id: v4(),
        senderID: userID,
        status: 'READY',
        type: 'SYSTEM_ADDED_MEMBER',
        channelID: newChannel._id,
        text: `${currentUser.firstName} ${currentUser.lastName}`,
      };

      const createdMessage = await this.messageService.createMessage(
        channel._id,
        messageCreated,
      );

      const addMembersMessage = await this.messageService.createMessage(
        channel._id,
        messageAddMember,
      );

      const createdMessageDTO = await convertMessageDTO({
        userID: userID,
        message: createdMessage,
        callUser: async (userID) => {
          const response = await this.httpService.axiosRef.get<UserDTO>(
            `http://auth-service/api/users/${userID}`,
            { headers: { Authorization: token } },
          );
          return response.data;
        },
      });

      const addMembersMessageDTO = await convertMessageDTO({
        userID: userID,
        message: addMembersMessage,
        callUser: async (userID) => {
          const response = await this.httpService.axiosRef.get<UserDTO>(
            `http://auth-service/api/users/${userID}`,
            { headers: { Authorization: token } },
          );
          return response.data;
        },
      });

      await this.notificationPushToDevice({
        channelID: channelDTO.channel._id,
        token,
        userID,
        type: 'message',
        message: createdMessageDTO,
      });

      this.eventsGateway.sendMessage({
        type: 'message.new',
        message: createdMessageDTO,
        channelID: channelDTO.channel._id,
      });

      await this.notificationPushToDevice({
        channelID: channelDTO.channel._id,
        token,
        userID,
        type: 'message',
        message: addMembersMessageDTO,
      });

      this.eventsGateway.sendMessage({
        type: 'message.new',
        message: addMembersMessageDTO,
        channelID: channelDTO.channel._id,
      });
    }

    return channelDTO;
  }

  @Put('/:channelID')
  async updateChannel(
    @Param('channelID') channelID: string,
    @Body() data: UpdateQuery<Channel>,
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id }: { id: string } = this.jwtService.decode(
        token?.split(' ')[1] || '',
      ) as any;
      userID = id;
    }
    const channel = await this.channelService.updateChannel(channelID, data);

    const isChangeName = data.name != null && data.name != undefined;

    const message: Message = {
      _id: v4(),
      senderID: userID,
      status: 'READY',
      type: isChangeName ? 'SYSTEM_CHANGED_NAME' : 'SYSTEM_CHANGED_AVATAR',
      channelID: channelID,
      text: isChangeName ? data.name : data.avatar,
    };

    const newMessage = await this.messageService.createMessage(
      channel._id,
      message,
    );

    const channelDTO = await convertChannelModel({
      userID,
      channel,
      callUser: async (userID) => {
        const response = await this.httpService.axiosRef.get<UserDTO>(
          `http://auth-service/api/users/${userID}`,
          { headers: { Authorization: token } },
        );
        return response.data;
      },
    });

    const messageDTO = await convertMessageDTO({
      userID: userID,
      message: newMessage,
      callUser: async (userID) => {
        const response = await this.httpService.axiosRef.get<UserDTO>(
          `http://auth-service/api/users/${userID}`,
          { headers: { Authorization: token } },
        );
        return response.data;
      },
    });

    this.eventsGateway.sendMessage({
      type: 'channel.updated',
      channelModel: channelDTO,
      channelID: channelDTO._id,
    });

    this.eventsGateway.sendMessage({
      type: 'message.new',
      message: messageDTO,
      channelID: channelDTO._id,
    });

    return channelDTO;
  }

  @Post('/:channelID/query')
  async queryChannelByID(
    @Body()
    {
      messages: {
        offset = 0,
        limit = 30,
        id_gt,
        id_gte,
        id_lt,
        id_lte,
        id_around,
      },
    }: {
      messages: PaginationParam;
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
        skip: offset,
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
        const response = await this.httpService.axiosRef.get<{
          attachment: AttachmentDTO;
        }>(`http://storage-service/api/storage/attachments/${attachmentID}`, {
          headers: { Authorization: token },
        });
        return response.data.attachment;
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
    { senderID, _id, text, attachments, quotedMessageID }: SendMessageParams,
    @Headers('Authorization') token?: string,
    @Query('userID') userID?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }

    const message: Message = {
      _id: _id || v4(),
      senderID: senderID || userID,
      status: 'READY',
      type: 'NORMAL',
      channelID: channelID,
      text: text,
      attachments: attachments?.map((attachments) => attachments._id),
      quotedMessageID: quotedMessageID,
    };

    const newMessage = await this.messageService.createMessage(
      channelID,
      message,
    );

    const channel = await this.channelService.findChannelByID(channelID);

    channel.members.forEach((member) => {
      if (member.userID !== userID) {
        member.lastRead = newMessage.createdAt;
        member.unreadMessage = member.unreadMessage + 1;
      }
    });

    await this.channelService.updateChannel(channelID, {
      lastMessageAt: newMessage.createdAt,
      members: channel.members,
    });

    const messageDTO = await convertMessageDTO({
      userID: userID,
      message: newMessage,
      attachments: async (attachmentID) => {
        const response = await this.httpService.axiosRef.get<{
          attachment: AttachmentDTO;
        }>(`http://storage-service/api/storage/attachments/${attachmentID}`, {
          headers: { Authorization: token },
        });
        return response.data.attachment;
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
        return quotedMessage;
      },
    });

    const hasFile = attachments != null && attachments.length > 0;

    await this.notificationPushToDevice({
      channelID,
      token,
      userID,
      type: hasFile ? 'attachments' : 'message',
      attachments,
      message: messageDTO,
    });

    this.eventsGateway.sendMessage({
      type: 'message.new',
      message: messageDTO,
      channelID: messageDTO.channelID,
    });

    return messageDTO;
  }

  @Delete('/:channelID/messages/:messageID')
  async deleteMessage(
    @Param('channelID') channelID: string,
    @Param('messageID') messageID: string,
    @Query('hard') hard: boolean | string,
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }
    let deletedMessage: Message;
    if (
      (typeof hard === 'boolean' && hard) ||
      (typeof hard === 'string' && hard === 'true')
    ) {
      deletedMessage = await this.messageService.updateMessage(messageID, {
        type: 'DELETED',
      });
    } else {
      const message = await this.messageService.findMessageById(messageID);
      const oldIgnoreUser = message.ignoreUser.filter(
        (user) => user !== userID,
      );
      const ignoreUser = [...oldIgnoreUser, userID];
      deletedMessage = await this.messageService.updateMessage(messageID, {
        ignoreUser: ignoreUser,
      });
    }

    const messageDTO = await convertMessageDTO({
      userID: userID,
      message: deletedMessage,
      attachments: async (attachmentID) => {
        const response = await this.httpService.axiosRef.get<{
          attachment: AttachmentDTO;
        }>(`http://storage-service/api/storage/attachments/${attachmentID}`, {
          headers: { Authorization: token },
        });
        return response.data.attachment;
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
        return quotedMessage;
      },
    });

    this.eventsGateway.sendMessage({
      type: 'message.deleted',
      channelID: channelID,
      message: messageDTO,
    });
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
    @Body() { attachmentID }: { attachmentID?: string },
    @UploadedFile() file: Express.Multer.File,
    @Headers('Authorization') token?: string,
  ) {
    console.log(file);

    if (file) {
      const formData = new FormData();
      formData.append('file', createReadStream(file.buffer), {
        filename: file.originalname,
        contentType: file.mimetype,
        filepath: file.path,
      });
      if (attachmentID) formData.append('attachmentID', attachmentID);
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

  @Post('/:channelID/messages/:messageID/reactions/:reactionType')
  async sendReaction(
    @Param('channelID') channelID: string,
    @Param('messageID') messageID: string,
    @Param('reactionType') reactionType: string,
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }

    const newReaction: MessageReaction = {
      reacter_id: userID,
      reaction: reactionType,
    };

    const message = await this.messageService.findMessageById(messageID);
    const oldReactions = message.reactions.filter(
      (reaction) => reaction.reacter_id !== newReaction.reacter_id,
    );
    const reactions = [...oldReactions, newReaction];
    const updatedMessage = await this.messageService.updateMessage(messageID, {
      reactions: reactions,
    });

    const messageDTO = await convertMessageDTO({
      userID: userID,
      message: updatedMessage,
      attachments: async (attachmentID) => {
        const response = await this.httpService.axiosRef.get<{
          attachment: AttachmentDTO;
        }>(`http://storage-service/api/storage/attachments/${attachmentID}`, {
          headers: { Authorization: token },
        });
        return response.data.attachment;
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
        return quotedMessage;
      },
    });

    const reactionDTO = await convertReaction({
      messageReaction: newReaction,
      callUser: async (userID) => {
        const response = await this.httpService.axiosRef.get<UserDTO>(
          `http://auth-service/api/users/${userID}`,
          { headers: { Authorization: token } },
        );
        return response.data;
      },
    });

    await this.notificationPushToDevice({
      channelID,
      token,
      userID,
      type: 'reaction',
      reaction: reactionDTO,
      message: messageDTO,
    });

    this.eventsGateway.sendMessage({
      type: 'reaction.new',
      message: messageDTO,
      channelID: channelID,
    });

    return {
      message: messageDTO,
      reaction: reactionDTO,
    };
  }

  @Delete('/:channelID/messages/:messageID/reactions/:reactionType')
  async deleteReaction(
    @Param('channelID') channelID: string,
    @Param('messageID') messageID: string,
    @Param('reactionType') reactionType: string,
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }

    const message = await this.messageService.findMessageById(messageID);
    const reactions = message.reactions.filter(
      (reaction) =>
        reaction.reacter_id !== userID && reaction.reaction !== reactionType,
    );
    const updatedMessage = await this.messageService.updateMessage(messageID, {
      reactions: reactions,
    });

    const messageDTO = await convertMessageDTO({
      userID: userID,
      message: updatedMessage,
      attachments: async (attachmentID) => {
        const response = await this.httpService.axiosRef.get<{
          attachment: AttachmentDTO;
        }>(`http://storage-service/api/storage/attachments/${attachmentID}`, {
          headers: { Authorization: token },
        });
        return response.data.attachment;
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
        return quotedMessage;
      },
    });

    this.eventsGateway.sendMessage({
      type: 'reaction.deleted',
      message: messageDTO,
      channelID: channelID,
    });
  }

  @Post('/:channelID/read')
  async markRead(
    @Param('channelID') channelID: string,
    @Body() { messageID }: { messageID: string },
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }

    let oldMessage = await this.messageService.findMessageLastRead(
      userID,
      channelID,
    );

    if (oldMessage) {
      const oldMessageIndex = oldMessage.viewers.indexOf(userID);
      if (oldMessageIndex > -1) {
        oldMessage.viewers.splice(oldMessageIndex, 1);
      }
      oldMessage = await this.messageService.updateMessage(oldMessage._id, {
        viewers: oldMessage.viewers,
      });
    }

    const message = await this.messageService.findMessageById(messageID);

    message.viewers.push(userID);

    const updatedMessage = await this.messageService.updateMessage(messageID, {
      viewers: message.viewers,
    });

    let updateChannel: Channel;
    if (oldMessage) {
      const remainMessage = await this.messageService.findMessageUnread(
        userID,
        new Date(oldMessage.createdAt),
      );

      const channel = await this.channelService.findChannelByID(channelID);
      channel.members.find((member) => member.userID == userID).unreadMessage =
        remainMessage.length;
      updateChannel = await this.channelService.updateChannel(channelID, {
        members: channel.members,
      });
    }

    const messageDTO = await convertMessageDTO({
      userID: userID,
      message: updatedMessage,
      attachments: async (attachmentID) => {
        const response = await this.httpService.axiosRef.get<{
          attachment: AttachmentDTO;
        }>(`http://storage-service/api/storage/attachments/${attachmentID}`, {
          headers: { Authorization: token },
        });
        return response.data.attachment;
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
        return quotedMessage;
      },
    });

    const oldMessageDTO = await convertMessageDTO({
      userID: userID,
      message: oldMessage,
      attachments: async (attachmentID) => {
        const response = await this.httpService.axiosRef.get<{
          attachment: AttachmentDTO;
        }>(`http://storage-service/api/storage/attachments/${attachmentID}`, {
          headers: { Authorization: token },
        });
        return response.data.attachment;
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
        return quotedMessage;
      },
    });

    const read = await convertReadDTO({
      userID: userID,
      channel: updateChannel,
      callUser: async (userID) => {
        const response = await this.httpService.axiosRef.get<UserDTO>(
          `http://auth-service/api/users/${userID}`,
          { headers: { Authorization: token } },
        );
        return response.data;
      },
    });

    this.eventsGateway.sendMessage({
      type: 'message.read',
      channelID: channelID,
      message: messageDTO,
      oldMessage: oldMessageDTO,
      read: read,
    });
  }

  async notificationPushToDevice({
    channelID,
    token,
    userID,
    type,
    attachments,
    message,
    reaction,
  }: {
    channelID: string;
    token: string;
    userID: string;
    type: 'attachments' | 'message' | 'reaction' | 'call';
    attachments?: AttachmentDTO[];
    message?: MessageDTO;
    reaction?: Reaction;
  }) {
    if (!attachments && !message && !reaction) return;
    if (type === 'attachments' && !attachments) return;
    if (type === 'message' && !message) return;
    if (type === 'reaction' && !reaction && !message) return;

    const channel: Channel = await this.channelService.findChannelByID(
      channelID,
    );

    const membersMute = channel.members
      .filter((member) => member.activeNotify)
      .map((x) => x.userID);

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
    });

    const otherMembers = channelDTO.members.filter(
      (member) =>
        member.userID != userID && membersMute.includes(member.userID),
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
      if (channelDTO.channel.name?.length ?? false) {
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

      let body: string = '';

      switch (type) {
        case 'attachments':
          body = `${isGroup ? `${senderName}: ` : ''}Đã gửi ${
            attachments.length
          } ảnh`;
          break;
        case 'message':
          switch (message.type) {
            case 'SYSTEM_ADDED_MEMBER':
              body = `${senderName} đã thêm ${message.text} vào nhóm`;
              break;
            case 'SYSTEM_MEMBER_LEFT':
              body = `${message.text} đã rời khỏi nhóm`;
              break;
            case 'SYSTEM_REMOVED_MEMBER':
              body = `${senderName} đã xóa ${message.text} ra khỏi nhóm`;
              break;
            default:
              body = `${isGroup ? `${senderName}: ` : ''}${message.text}`;
              break;
          }
          break;
        case 'reaction':
          body = `${isGroup ? `${senderName}: ` : ''}Đã phản ứng ${
            reaction.type
          } với tin nhắn: ${message.text}`;
          break;
      }

      try {
        const messageResponse = await this.firebaseMessaging.sendMulticast({
          tokens: devices,
          data: {
            type: type,
            channelID: channelID,
            click_action: 'FLUTTER_NOTIFICATION_CLICK',
          },
          notification: {
            title: channelName,
            body: body,
          },
          android: {
            priority: 'high',
            data: {
              type: type,
              channelID: channelID,
              click_action: 'FLUTTER_NOTIFICATION_CLICK',
            },
            ttl: 60 * 60 * 24,
            notification: {
              title: channelName,
              body: body,
              priority: 'high',
              defaultSound: true,
              sticky: true,
              visibility: 'private',
            },
          },
          apns: {
            headers: {
              'apns-priority': '10',
              'apns-push-type': 'alert',
            },
            payload: {
              aps: {
                aps: {
                  'content-available': 1,
                },
                contentAvailable: true,
                sound: {
                  name: 'default',
                  volume: 1,
                  critical: true,
                },
                alert: {
                  title: channelName,
                  body: body,
                },
              },
              type: type,
              channelID: channelID,
              click_action: 'FLUTTER_NOTIFICATION_CLICK',
            },
          },
        });
        console.log(messageResponse);
        if (messageResponse.failureCount > 0) {
          const failedTokens = [];
          messageResponse.responses.forEach((resp, idx) => {
            if (!resp.success) {
              console.log(resp.error);
              failedTokens.push(devices[idx]);
            }
          });
          console.log('List of tokens that caused failures: ' + failedTokens);
        }
      } catch (error) {
        console.log(error);
      }
    }
  }

  @Post('/:channelID/call/:callType')
  async call(
    @Param('channelID') channelID: string,
    @Param('callType') callType: string,
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }
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
    }

    try {
      const messageResponse = await this.firebaseMessaging.sendMulticast({
        tokens: devices,
        data: {
          type: callType,
          uuid: channelID,
          callerName: channelName,
        },
      });
      console.log(messageResponse);
      if (messageResponse.failureCount > 0) {
        const failedTokens = [];
        messageResponse.responses.forEach((resp, idx) => {
          if (!resp.success) {
            console.log(resp.error);
            failedTokens.push(devices[idx]);
          }
        });
        console.log('List of tokens that caused failures: ' + failedTokens);
      }
    } catch (error) {
      console.log(error);
    }
  }

  @Post('/:channelID/response/:responseType')
  async response(
    @Param('channelID') channelID: string,
    @Param('responseType') responseType: 'decline' | 'end' | 'accept',
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

    const userDTO = response.data;

    this.eventsGateway.sendMessage({
      type: `call.${responseType}`,
      channelID: channelID,
      user: userDTO,
    });
  }

  @Put('/:channelID/messages/:messageID')
  async updateMessage(
    @Param('channelID') channelID: string,
    @Param('messageID') messageID: string,
    @Body() message: Partial<Message>,
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }

    const updatedMessage = await this.messageService.updateMessage(messageID, {
      ...message,
      pinnedBy: userID,
    });

    const messageDTO = await convertMessageDTO({
      userID: userID,
      message: updatedMessage,
      attachments: async (attachmentID) => {
        const response = await this.httpService.axiosRef.get<{
          attachment: AttachmentDTO;
        }>(`http://storage-service/api/storage/attachments/${attachmentID}`, {
          headers: { Authorization: token },
        });
        return response.data.attachment;
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
        return quotedMessage;
      },
    });

    this.eventsGateway.sendMessage({
      type: 'message.updated',
      message: messageDTO,
      channelID: channelID,
    });

    return messageDTO;
  }

  @Post('/:channelID/mute')
  async muteChannel(
    @Param('channelID') channelID: string,
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }

    await this.channelService.updateMember(channelID, userID, {
      activeNotify: false,
    });

    return {};
  }

  @Post('/:channelID/unmute')
  async unmuteChannel(
    @Param('channelID') channelID: string,
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }

    await this.channelService.updateMember(channelID, userID, {
      activeNotify: true,
    });

    return {};
  }

  @Post('/:channelID/members')
  async addMembers(
    @Param('channelID') channelID: string,
    @Body() { members: newMembers }: { members: string[] },
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }

    const channel = await this.channelService.findChannelByID(channelID);
    const newMembersOj: ChannelMember[] = newMembers.map((member) => ({
      userID: member,
      addBy: userID,
      lastRead: Date().toString(),
    }));
    const updatedMembers = [...channel.members, ...newMembersOj];
    channel.members = updatedMembers;

    const updatedChannel = await this.channelService.saveChannel(channel);

    const newMembersUpdated = updatedChannel.members.filter((member) =>
      newMembers.includes(member.userID),
    );

    const membersDTO = await Promise.all(
      newMembersUpdated.map(async (member) => {
        return await convertMemberDTO({
          member,
          callUser: async (userID) => {
            const response = await this.httpService.axiosRef.get<UserDTO>(
              `http://auth-service/api/users/${userID}`,
              { headers: { Authorization: token } },
            );
            return response.data;
          },
        });
      }),
    );

    const text = membersDTO
      .map((member) => `${member.user.firstName} ${member.user.lastName}`)
      .join(',');

    const message: Message = {
      _id: v4(),
      senderID: userID,
      status: 'READY',
      type: 'SYSTEM_ADDED_MEMBER',
      channelID: channelID,
      text: text,
    };

    const newMessage = await this.messageService.createMessage(
      channelID,
      message,
    );

    await this.channelService.updateChannel(channelID, {
      lastMessageAt: newMessage.createdAt,
    });

    const messageDTO = await convertMessageDTO({
      userID: userID,
      message: newMessage,
      attachments: async (attachmentID) => {
        const response = await this.httpService.axiosRef.get<{
          attachment: AttachmentDTO;
        }>(`http://storage-service/api/storage/attachments/${attachmentID}`, {
          headers: { Authorization: token },
        });
        return response.data.attachment;
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
        return quotedMessage;
      },
    });

    await this.notificationPushToDevice({
      channelID,
      token,
      userID,
      type: 'message',
      message: messageDTO,
    });

    this.eventsGateway.sendMessage({
      type: 'member.added',
      members: membersDTO,
      channelID: channelID,
    });

    this.eventsGateway.sendMessage({
      type: 'message.new',
      channelID: channelID,
      message: messageDTO,
    });

    return {};
  }

  @Delete('/:channelID/members/:memberID/:removeType')
  async removeMember(
    @Param('channelID') channelID: string,
    @Param('memberID') memberID: string,
    @Param('removeType') removeType: 'leave' | 'remove',
    @Query('userID') userID?: string,
    @Headers('Authorization') token?: string,
  ) {
    if (!userID || userID.trim().length === 0) {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
      userID = id;
    }

    const channel = await this.channelService.findChannelByID(channelID);
    channel.members = channel.members.filter(
      (member) => member.userID != memberID,
    );

    const updatedChannel = await this.channelService.saveChannel(channel);

    const response = await this.httpService.axiosRef.get<UserDTO>(
      `http://auth-service/api/users/${memberID}`,
      { headers: { Authorization: token } },
    );

    const userRemoved = response.data;

    const message: Message = {
      _id: v4(),
      senderID: userID,
      status: 'READY',
      type:
        removeType === 'leave' ? 'SYSTEM_MEMBER_LEFT' : 'SYSTEM_REMOVED_MEMBER',
      channelID: channelID,
      text: `${userRemoved.firstName} ${userRemoved.lastName}`,
    };

    const newMessage = await this.messageService.createMessage(
      channelID,
      message,
    );

    await this.channelService.updateChannel(channelID, {
      lastMessageAt: newMessage.createdAt,
    });

    const messageDTO = await convertMessageDTO({
      userID: userID,
      message: newMessage,
      attachments: async (attachmentID) => {
        const response = await this.httpService.axiosRef.get<{
          attachment: AttachmentDTO;
        }>(`http://storage-service/api/storage/attachments/${attachmentID}`, {
          headers: { Authorization: token },
        });
        return response.data.attachment;
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
        return quotedMessage;
      },
    });

    await this.notificationPushToDevice({
      channelID,
      token,
      userID,
      type: 'message',
      message: messageDTO,
    });

    this.eventsGateway.sendMessage({
      type: 'member.removed',
      channelID: channelID,
      user: userRemoved,
    });

    this.eventsGateway.sendMessage({
      type: 'message.new',
      channelID: channelID,
      message: messageDTO,
    });

    return {};
  }
}
