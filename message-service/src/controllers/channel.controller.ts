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

@Controller('/channels')
@UseFilters(new ChannelExceptionFilter())
export class ChannelController {
  constructor(
    private readonly channelService: ChannelService,
    private readonly jwtService: JwtService,
  ) {}

  @Get('/search')
  async findAllByUser(
    @Query() { userID, skip, limit }: ChannelPaginationParams,
  ) {
    const channelPagination = await this.channelService.findAllByUser(
      userID,
      skip,
      limit,
    );
    return channelPagination;
  }

  @Post()
  async createChannel(
    @Body() createChannelDTO: CreateChannelDTO,
    @Headers('Authorization') token?: string,
  ) {
    let { newMembers, name, userID } = createChannelDTO;
    if (!userID || userID.trim() === '') {
      const { id } = this.jwtService.decode(token?.split(' ')[1] || '') as any;
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
    const channel: Channel = {
      _id: v4(),
      name: name,
      members: channelMember,
    };
    channelMember;
    const newChannel = await this.channelService.createChannel(channel);
    return newChannel;
  }
}
