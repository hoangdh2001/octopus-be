import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { ChannelModule } from './modules/channel.module';
import { EventModule } from './modules/events.module';
import { MessageModule } from './modules/message.module';

@Module({
  imports: [
    MongooseModule.forRoot('mongodb://localhost:27017/messagedb'),
    ChannelModule,
    EventModule,
  ],
})
export class AppModule {}
