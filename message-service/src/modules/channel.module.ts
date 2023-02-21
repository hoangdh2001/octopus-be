import { Module } from '@nestjs/common';
import { JwtModule } from '@nestjs/jwt';
import { MongooseModule } from '@nestjs/mongoose';
import { ChannelController } from '../controllers/channel.controller';
import { Channel, ChannelSchema } from '../models/channel.model';
import { ChannelService } from '../services/channel.service';
import { EventModule } from './events.module';
import { MessageModule } from './message.module';

@Module({
  imports: [
    MongooseModule.forRoot('mongodb://localhost:27017/messagedb'),
    MongooseModule.forFeature([{ name: Channel.name, schema: ChannelSchema }]),
    JwtModule.register({ secret: 'scretKey' }),
    MessageModule,
    EventModule,
  ],
  controllers: [ChannelController],
  providers: [ChannelService],
})
export class ChannelModule {}
