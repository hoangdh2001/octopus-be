import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type AttachmentDocument = HydratedDocument<Attachment>;

@Schema({ timestamps: true })
export class Attachment {
  @Prop({ type: String })
  _id: string;

  @Prop({ type: Number })
  filesize?: number;

  @Prop({ type: String })
  mimeType?: string;

  @Prop({ type: String })
  type?: string;

  @Prop({ type: Number })
  originalHeight?: number;

  @Prop({ type: String })
  originalName?: string;

  @Prop({ type: Number })
  originalWidth?: number;

  @Prop({ type: String })
  thumbnailUrl?: string;

  @Prop({ type: String })
  url?: string;

  @Prop({ type: String })
  secureUrl?: string;

  createdAt?: string;
  updatedAt?: string;

  @Prop({ type: String })
  createdBy?: string;
}

export const AttachmentSchema = SchemaFactory.createForClass(Attachment);
