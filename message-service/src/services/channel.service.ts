import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { FilterQuery, Model, SortOrder, UpdateQuery } from 'mongoose';
import { ChannelDTO } from 'src/dtos/channel.dto';
import { Message } from 'src/models/message.model';
import {
  Channel,
  ChannelDocument,
  ChannelMember,
  ChannelMemberDocument,
} from '../models/channel.model';

@Injectable()
export class ChannelService {
  constructor(
    @InjectModel(Channel.name)
    private channelModel: Model<ChannelDocument>,
  ) {}

  async saveChannel(channel: Channel) {
    const newChannel: Channel = await this.channelModel.create(channel);
    return newChannel;
  }

  async findAllByUser(
    userID: string,
    documentToSkip: number = 0,
    limitOfDocuments: number = 10,
  ) {
    const channels: Channel[] = await this.channelModel.aggregate([
      { $match: { members: { $elemMatch: { userID: userID } } } },
      { $sort: { lastMessageAt: -1 } },
      { $skip: documentToSkip * limitOfDocuments },
      { $limit: Number.parseInt(limitOfDocuments.toString()) },
    ]);

    return channels;
  }

  async search(
    filter: FilterQuery<Channel>,
    {
      sort,
      limit = 20,
      offset = 0,
    }: {
      sort?: { [key: string]: SortOrder };
      limit?: number;
      offset?: number;
    },
  ) {
    const channels: Channel[] = await this.channelModel
      .find(filter)
      .sort(sort)
      .skip(offset)
      .limit(limit);
    return channels;
  }

  async countByUserID(userID: string): Promise<number> {
    const count = await this.channelModel
      .find({
        members: { $elemMatch: { userID: userID } },
      })
      .count()
      .exec();
    return count;
  }

  async findChannelByID(channelID: string) {
    return await this.channelModel.findById(channelID);
  }

  async updateChannel(channelID: string, update: UpdateQuery<Channel>) {
    const channel: Channel = await this.channelModel.findOneAndUpdate(
      {
        _id: channelID,
      },
      update,
      {
        new: true,
      },
    );
    return channel;
  }

  async updateMember(
    channelID: string,
    memberID: string,
    update: UpdateQuery<ChannelMember>,
  ) {
    const channel: Channel = await this.channelModel.findOneAndUpdate(
      { _id: channelID, 'members.userID': memberID },
      {
        $set: {
          'members.$.activeNotify': update.activeNotify,
          'members.$.hidden': update.hidden,
          'members.$.updatedAt': Date(),
        },
      },
      { upsert: true, new: true },
    );
    return channel;
  }
}
