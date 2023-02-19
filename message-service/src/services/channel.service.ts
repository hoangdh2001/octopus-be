import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { skip } from 'rxjs';
import { ChannelDTO } from 'src/dtos/channel.dto';
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
    let data: ChannelDTO[] = await this.channelModel.aggregate([
      { $match: { members: { $elemMatch: { userID: userID } } } },
      { $sort: { receivedMessageAt: -1 } },
      { $skip: documentToSkip * limitOfDocuments },
      { $limit: Number.parseInt(limitOfDocuments.toString()) },
      {
        $lookup: {
          from: 'messages',
          localField: '_id',
          foreignField: 'channelID',
          as: 'messages',
        },
      },
      {
        $project: {
          _id: 0,
          channel: {
            _id: '$_id',
            name: '$name',
            hiddenChannel: {
              $first: {
                $map: {
                  input: {
                    $filter: {
                      input: '$members',
                      as: 'member',
                      cond: { $eq: ['$$member.userID', userID] },
                    },
                  },
                  as: 'member',
                  in: '$$member.hidden',
                },
              },
            },
            activeNotify: {
              $first: {
                $map: {
                  input: {
                    $filter: {
                      input: '$members',
                      as: 'member',
                      cond: { $eq: ['$$member.userID', userID] },
                    },
                  },
                  as: 'member',
                  in: '$$member.activeNotify',
                },
              },
            },
            createdAt: '$createdAt',
            updatedAt: '$updatedAt',
          },
          members: {
            $map: {
              input: '$members',
              as: 'member',
              in: {
                userID: '$$member.userID',
                createdAt: '$$member.createdAt',
                updatedAt: '$$member.updatedAt',
              },
            },
          },
          messages: {
            $sortArray: { input: '$messages', sortBy: { createdAt: -1 } },
          },
        },
      },
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
    const count = await this.channelModel
      .find({
        members: { $elemMatch: { userID: userID } },
      })
      .count()
      .exec();
    return count;
  }
}
