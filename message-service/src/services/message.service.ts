import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Message, MessageDocument } from '../models/message.model';

@Injectable()
export class MessageServive {
  constructor(
    @InjectModel(Message.name) private messageModel: Model<MessageDocument>,
  ) {}

  async findAllByChannel(
    channelID: string,
    skip: number = 0,
    limit: number = 30,
  ) {
    const messages: Message[] = await this.messageModel.aggregate([
      { $match: { channelID: channelID } },
      { $sort: { createdAt: -1 } },
      { $skip: skip * limit },
      { $limit: limit },
    ]);
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
}
