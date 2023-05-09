import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

@Schema({ _id: false, timestamps: true })
export class ChannelMember {
  @Prop({ type: String, required: true })
  userID: string;

  @Prop({ type: Boolean, default: true })
  activeNotify?: boolean;

  @Prop({ type: Boolean, default: false })
  hidden?: boolean;

  createdAt?: string;

  updatedAt?: string;
}

export type ChannelMemberDocument = HydratedDocument<ChannelMember>;

export const ChannelMemberSchema = SchemaFactory.createForClass(ChannelMember);

@Schema({ _id: false, timestamps: true })
export class Read {
  @Prop({ type: Date })
  lastRead: string;
  @Prop({ type: String })
  userID: string;
  @Prop({ type: Number, default: 0 })
  unreadMessage?: number;
}

export const ReadSchema = SchemaFactory.createForClass(Read);

export type ChannelDocument = HydratedDocument<Channel>;

@Schema({ timestamps: true })
export class Channel {
  @Prop({ type: String })
  _id: string;

  @Prop({ type: String })
  avatar?: string;

  @Prop({ type: String })
  name?: string;

  @Prop({
    type: [ChannelMemberSchema],
  })
  members: ChannelMember[];

  @Prop({ type: [ReadSchema] })
  read?: Read[];

  @Prop({ type: Date, default: Date.now() })
  lastMessageAt?: string;

  createdAt?: string;
  updatedAt?: string;

  @Prop({ type: String })
  createdBy?: string;
}

export const ChannelSchema = SchemaFactory.createForClass(Channel);
