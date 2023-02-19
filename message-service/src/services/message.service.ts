import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Message, MessageDocument } from '../models/message.model';

@Injectable()
export class MessageServive {
  constructor(
    @InjectModel(Message.name) private messageModel: Model<MessageDocument>,
  ) {}

  async findAllByChannel(channelID: string) {
    const messages = await this.messageModel.aggregate([
      { $match: { channelID: channelID } },
      { $sort: { createdAt: -1 } },
      { $limit: 30 },
    ]);
    return messages;
  }
}
