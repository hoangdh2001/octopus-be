import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import {
  FilterQuery,
  Model,
  PipelineStage,
  SortOrder,
  UpdateQuery,
} from 'mongoose';
import { Message, MessageDocument } from '../models/message.model';

@Injectable()
export class MessageServive {
  constructor(
    @InjectModel(Message.name) private messageModel: Model<MessageDocument>,
  ) {}

  async findMessageById(messageID: string) {
    const message: Message = await this.messageModel.findById(messageID);
    return message;
  }

  async findAllByChannel({
    channelID,
    messageID,
    skip = 0,
    limit = 20,
    condition = 'gt',
  }: {
    channelID: string;
    messageID?: string;
    skip?: number;
    limit?: number;
    condition?: 'gt' | 'gte' | 'lt' | 'lte';
  }) {
    const params: PipelineStage[] = [
      { $match: { channelID: channelID } },
      { $sort: { createdAt: -1 } },
    ];
    if (messageID) {
      const message = await this.findMessageById(messageID);
      const c = `\$${condition}`;
      params.concat({
        $match: { createdAt: { c: new Date(message.createdAt) } },
      });
    } else {
      params.concat({ $skip: skip * limit });
    }
    params.concat({ $limit: limit });

    const messages: Message[] = await this.messageModel.aggregate(params);
    return messages;
  }

  async seachMessage(
    channelID: string,
    filter: FilterQuery<Message>,
    {
      sort,
      limit = 30,
      offset = 0,
    }: {
      sort?: { [key: string]: SortOrder };
      limit?: number;
      offset?: number;
    },
  ) {
    const messages: Message[] = await this.messageModel
      .find({
        channelID: channelID,
        ...filter,
      })
      .sort(sort)
      .exec();
    return messages;
  }

  async queryAroundMessage({
    channelID,
    messageID,
    limit = 20,
  }: {
    channelID: string;
    messageID: string;
    limit?: number;
  }) {
    const message = await this.findMessageById(messageID);
    var messages: Message[] = await this.messageModel.aggregate([
      { $match: { channelID: channelID } },
      { $sort: { createdAt: -1 } },
      { $match: { createdAt: { $lte: new Date(message.createdAt) } } },
      { $limit: limit / 2 },
    ]);
    messages.concat(
      await this.messageModel.aggregate([
        { $match: { channelID: channelID } },
        { $sort: { createdAt: -1 } },
        { $match: { createdAt: { $gt: new Date(message.createdAt) } } },
        { $limit: limit / 2 },
      ]),
    );
    return messages;
  }

  async createMessage(channelID: string, message: Message) {
    const newMessage: Message = await this.messageModel.create(message);
    return newMessage;
  }

  async countByChannel(channelID: string) {
    const count = await this.messageModel
      .find({
        members: { $elemMatch: { channelID: channelID } },
      })
      .count()
      .exec();
    return count;
  }

  async updateMessage(messageID: string, update: UpdateQuery<Message>) {
    const message: Message = await this.messageModel.findOneAndUpdate(
      {
        _id: messageID,
      },
      update,
      {
        new: true,
      },
    );
    return message;
  }

  async deleteMessage(messageID: string) {
    await this.messageModel.findOneAndDelete({ _id: messageID });
  }
}
