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

  @Prop({ type: String, required: true })
  reacter_id: string;
}

export const MessageReactionSchema =
  SchemaFactory.createForClass(MessageReaction);

export type MessageDocument = HydratedDocument<Message>;

@Schema({ timestamps: true })
export class Message {
  @Prop({ type: String })
  _id: string;

  @Prop({ type: Boolean, default: false })
  updated: boolean;

  @Prop({ type: String, required: true })
  sender_id: string;

  @Prop({
    type: String,
    enum: ['DELETED', 'ERROR', 'READY'],
  })
  status: string;

  @Prop({ type: String })
  text: string;

  @Prop({ type: String, enum: ['SYSTEM_NOTIFICATION', 'NORMAL'] })
  type: string;

  @Prop({ type: [String], default: [] })
  viewers: string[];

  @Prop({ type: [MessageReactionSchema], default: [] })
  reactions: MessageReaction[];

  @Prop({ type: String, required: true })
  channel_id: string;
}

export const MessageSchema = SchemaFactory.createForClass(Message);
