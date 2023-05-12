import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument, Types } from 'mongoose';

@Schema({ timestamps: true, _id: false })
export class MessageReaction {
  @Prop({
    type: String,
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
  updated?: boolean;

  @Prop({ type: String, required: true })
  senderID: string;

  @Prop({
    type: String,
    enum: ['ERROR', 'READY'],
  })
  status: 'ERROR' | 'READY';

  @Prop({ type: String })
  text?: string;

  @Prop({
    type: String,
    enum: [
      'SYSTEM_NOTIFICATION',
      'NORMAL',
      'DELETED',
      'SYSTEM_ADDED_MEMBER',
      'SYSTEM_REMOVED_MEMBER',
      'SYSTEM_MEMBER_LEFT',
      'SYSTEM_CREATED_CHANNEL',
    ],
  })
  type:
    | 'SYSTEM_NOTIFICATION'
    | 'NORMAL'
    | 'DELETED'
    | 'SYSTEM_ADDED_MEMBER'
    | 'SYSTEM_REMOVED_MEMBER'
    | 'SYSTEM_MEMBER_LEFT'
    | 'SYSTEM_CREATED_CHANNEL';

  @Prop({ type: [String], default: [] })
  viewers?: string[];

  @Prop({ type: [MessageReactionSchema], default: [] })
  reactions?: MessageReaction[];

  @Prop({ type: String, required: true })
  channelID: string;

  createdAt?: string;
  updatedAt?: string;

  @Prop({ type: [String] })
  attachments?: string[];

  @Prop({ type: String })
  quotedMessageID?: string;

  @Prop({ type: [String] })
  ignoreUser?: [string];

  @Prop({ type: Boolean, default: false })
  pinned?: boolean;

  @Prop({ type: String })
  pinnedBy?: string;
}

export const MessageSchema = SchemaFactory.createForClass(Message);
