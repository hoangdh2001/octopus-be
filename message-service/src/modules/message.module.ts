import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { MessageController } from '../controllers/message.controller';
import {
  Message,
  MessageReaction,
  MessageReactionSchema,
  MessageSchema,
} from '../models/message.model';
import { MessageServive } from '../services/message.service';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: Message.name, schema: MessageSchema }]),
  ],
  controllers: [MessageController],
  providers: [MessageServive],
})
export class MessageModule {}
