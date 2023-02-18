import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { ChannelController } from '../controllers/channel.controller';
import {
  Channel,
  ChannelMember,
  ChannelMemberSchema,
  ChannelSchema,
} from '../models/channel.model';
import { ChannelService } from '../services/channel.service';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: Channel.name, schema: ChannelSchema }]),
  ],
  controllers: [ChannelController],
  providers: [ChannelService],
})
export class ChannelModule {}
