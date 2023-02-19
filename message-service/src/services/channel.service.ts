import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { skip } from 'rxjs';
import { Pagination } from 'src/dtos/pagination.dto';
import { Channel, ChannelDocument } from '../models/channel.model';

@Injectable()
export class ChannelService {
  constructor(
    @InjectModel(Channel.name)
    private channelModel: Model<ChannelDocument>,
  ) {}

  async createChannel(channel: Channel) {
    return await this.channelModel.create(channel);
  }

  async findAllByUser(
    userID: string,
    documentToSkip = 0,
    limitOfDocuments = 1,
  ): Promise<Pagination> {
    const totalItem = await this.countByUserID(userID);
    const totalPage = Math.floor(totalItem / limitOfDocuments) + 1;
    const data = await this.channelModel.aggregate([
      { $match: { members: { $elemMatch: { userID: userID } } } },
      { $sort: { receivedMessageAt: 1 } },
      { $skip: documentToSkip * limitOfDocuments },
      { $limit: Number.parseInt(limitOfDocuments.toString()) },
    ]);

    return {
      skip: documentToSkip,
      limit: limitOfDocuments,
      totalItem: totalItem,
      totalPage: totalPage,
      data: data,
    };
  }

  private async countByUserID(userID: string): Promise<number> {
    const count: any[] = await this.channelModel.aggregate([
      {
        $match: {
          members: { $elemMatch: { userID: userID } },
        },
      },
      {
        $count: 'count',
      },
    ]);
    return count[0].count;
  }
}
