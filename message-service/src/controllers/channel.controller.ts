import {
  Body,
  Controller,
  Get,
  Param,
  Post,
  Query,
  Req,
  Res,
  UseInterceptors,
} from '@nestjs/common';
import { Request, Response } from 'express';
import { ChannelService } from '../services/channel.service';
import { v4 } from 'uuid';
import { JwtService } from '@nestjs/jwt';
import { Channel, ChannelMember } from 'src/models/channel.model';
import { ChannelPaginationParams } from 'src/dtos/channel.dto';

@Controller('/channels')
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
  async createChannel(@Req() req: Request, @Res() res: Response) {
    try {
      let { userIDs, name }: { userIDs: string[]; name: string } = req.body;
      const token = req.header('Authorization')?.split(' ')[1] as any;
      const { userID } = this.jwtService.decode(token || '') as any;
      userIDs.push(userID);
      userIDs = [...new Set(userIDs)];

      if (userIDs.length < 2) {
        return res.status(400).json({
          status: 400,
          message: 'Số thành viên của 1 nhóm phải từ 2 trở lên.',
        });
      }
      const channel: Channel = {
        _id: v4(),
        name: name,
        members: [],
      };

      const channelMember: ChannelMember[] = userIDs.map((userId) => {
        return {
          userID: userId,
        };
      });

      channel.members = channelMember;
      const newChannel = await this.channelService.createChannel(channel);
      return res.status(201).json(newChannel);
    } catch (error) {
      console.log(error);

      return res
        .status(400)
        .send({ status: 400, message: 'Vui lòng kiểm tra dữ liệu đầu vào!' });
    }
  }
}
