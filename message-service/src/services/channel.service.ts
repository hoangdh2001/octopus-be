import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { ChannelDTO } from 'src/dtos/channel.dto';
import { Message } from 'src/models/message.model';
import { Channel, ChannelDocument } from '../models/channel.model';

@Injectable()
export class ChannelService {
  constructor(
    @InjectModel(Channel.name)
    private channelModel: Model<ChannelDocument>,
  ) {}

  async createChannel(channel: Channel) {
    const newChannel: Channel = await this.channelModel.create(channel);
    return newChannel;
  }

  async findAllByUser(
    userID: number,
    documentToSkip: number = 0,
    limitOfDocuments: number = 10,
  ) {
    const channels: Channel[] = await this.channelModel.aggregate([
      { $match: { members: { $elemMatch: { userID: userID } } } },
      { $sort: { receivedMessageAt: -1 } },
      { $skip: documentToSkip * limitOfDocuments },
      { $limit: Number.parseInt(limitOfDocuments.toString()) },
    ]);

    return channels;
  }

  async countByUserID(userID: number): Promise<number> {
    const count = await this.channelModel
      .find({
        members: { $elemMatch: { userID: userID } },
      })
      .count()
      .exec();
    return count;
  }
}
