import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { ChannelRequest } from 'src/dtos/channel.dto';
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
}
