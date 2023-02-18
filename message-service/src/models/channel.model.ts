import { Type } from '@nestjs/common';
import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument, Types } from 'mongoose';

@Schema({ _id: false, timestamps: true })
export class ChannelMember {
  @Prop({ type: String, required: true })
  user_id: string;

  @Prop({ type: Boolean, default: false })
  active_notify: boolean;

  @Prop({ type: Boolean, default: false })
  hidden: boolean;
}

export const ChannelMemberSchema = SchemaFactory.createForClass(ChannelMember);

export type ChannelDocument = HydratedDocument<Channel>;

@Schema({ timestamps: true })
export class Channel {
  @Prop({ type: Types.ObjectId })
  channel_id: Types.ObjectId;

  @Prop({ type: String })
  avatar: string;

  @Prop({ type: String })
  name: string;

  @Prop({
    type: [ChannelMemberSchema],
  })
  members: ChannelMember[];
}

export const ChannelSchema = SchemaFactory.createForClass(Channel);
