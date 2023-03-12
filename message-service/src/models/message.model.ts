import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument, Types } from 'mongoose';

@Schema({ timestamps: true, _id: false })
export class MessageReaction {
  @Prop({
    type: String,
    enum: ['LIKE', 'HAHA', 'HEART', 'CRY', 'WOW', 'ANGRY'],
    default: 'LIKE',
  })
  reaction: string;

  @Prop({ type: Number, required: true })
  reacter_id: number;
}

export const MessageReactionSchema =
  SchemaFactory.createForClass(MessageReaction);

export type MessageDocument = HydratedDocument<Message>;

@Schema({ timestamps: true })
export class Message {
  @Prop({ type: String })
  _id: string;

  @Prop({ type: Boolean, default: false })
  updated?: boolean;

  @Prop({ type: Number, required: true })
  senderID: number;

  @Prop({
    type: String,
    enum: ['DELETED', 'ERROR', 'READY'],
  })
  status: 'DELETED' | 'ERROR' | 'READY';

  @Prop({ type: String })
  text?: string;

  @Prop({ type: String, enum: ['SYSTEM_NOTIFICATION', 'NORMAL'] })
  type: 'SYSTEM_NOTIFICATION' | 'NORMAL';

  @Prop({ type: [String], default: [] })
  viewers?: string[];

  @Prop({ type: [MessageReactionSchema], default: [] })
  reactions?: MessageReaction[];

  @Prop({ type: String, required: true })
  channelID: string;

  createdAt?: number;
  updatedAt?: number;
}

export const MessageSchema = SchemaFactory.createForClass(Message);
