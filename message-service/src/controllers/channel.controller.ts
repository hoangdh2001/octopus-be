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
} from '@nestjs/common';
import { ChannelService } from '../services/channel.service';
import { JwtService } from '@nestjs/jwt';
import {
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
  MessagePaginationParams,
  SendMessageParams,
} from 'src/dtos/message.dto';
import { convertMessageDTO } from 'src/utils/message.util';
import { Message } from 'src/models/message.model';
import { EventsGateway } from 'src/listeners/events.gateway';
import { ClientKafka } from '@nestjs/microservices';

@Controller('/channels')
@UseFilters(new ChannelExceptionFilter())
export class ChannelController implements OnModuleInit, OnModuleDestroy {
  constructor(
    private readonly channelService: ChannelService,
    private readonly jwtService: JwtService,
    private readonly messageService: MessageServive,
    private readonly eventsGateway: EventsGateway,
    @Inject('MESSAGE_SERVICE') private readonly client: ClientKafka,
  ) {}

  async onModuleDestroy() {
    await this.client.close();
  }
  async onModuleInit() {
    this.client.subscribeToResponseOf('user-get');
    await this.client.connect();
  }

  @Get('/search')
  async findAllByUser(
    @Query() { userID, skip, limit }: ChannelPaginationParams,
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
        const messages = await this.messageService.findAllByChannel(
          channel._id,
        );
        return await convertChannelDTO({ channel, userID, messages });
      }),
    );

    return { skip, limit, totalItem, totalPage, data };
  }

  @Post()
  async createChannel(
    @Body() createChannelDTO: CreateChannelDTO,
    @Headers('Authorization') token?: string,
  ) {
    let { newMembers, name, userID } = createChannelDTO;
    if (!userID || userID === 0) {
      const { id }: { id: number } = this.jwtService.decode(
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
    const channelMember: ChannelMember[] = newMembers.map((userId) => {
      return {
        userID: userId,
      };
    });
    this.client.send('user-get', userID);
    const channel: Channel = {
      _id: v4(),
      name: name,
      members: channelMember,
    };

    this.client.send('user-get', userID);

    const newChannel = await this.channelService.createChannel(channel);
    return await convertChannelDTO({ channel: newChannel, userID });
  }

  @Get('/:channelID/messages')
  async findAllMessageByChannel(
    @Query() { skip = 0, limit = 30 }: MessagePaginationParams,
    @Param('channelID') channelID: string,
  ) {
    const totalItem = await this.messageService.countByChannel(channelID);
    const totalPage = Math.floor(totalItem / limit) + 1;
    const messages = await this.messageService.findAllByChannel(
      channelID,
      skip,
      limit,
    );

    const data = await Promise.all(
      messages.map((message) => {
        return convertMessageDTO(message);
      }),
    );

    return { skip, limit, totalItem, totalPage, data };
  }

  @Post('/:channelID/messages')
  async sendMessage(
    @Param('channelID') channelID: string,
    @Body() { senderID, messageID, text }: SendMessageParams,
    @Headers('Authorization') token?: string,
  ) {
    const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
    const message: Message = {
      _id: messageID || v4(),
      senderID: senderID || id,
      status: 'READY',
      type: 'NORMAL',
      channelID: channelID,
      text: text,
    };

    const newMessage = await this.messageService.createMessage(
      channelID,
      message,
    );

    const messageDTO = convertMessageDTO(newMessage);

    this.eventsGateway.sendMessage({
      type: 'message.created',
      message: messageDTO,
    });

    return messageDTO;
  }
}
