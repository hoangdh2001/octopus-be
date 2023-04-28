import { OwnUserDTO, UserDTO } from './user.dto';
import { IsNumber, IsOptional, Min, IsString, Matches } from 'class-validator';
import { Type } from 'class-transformer';
import { ChannelDTO } from './channel.dto';
import { Message, MessageReaction } from '../models/message.model';

export type Reaction = Pick<MessageReaction, 'reaction'> & {
  reacter?: UserDTO;
  reacterID?: string;
};

export type AttachmentDTO = {
  _id: string;
  filesize?: number;
  mineType?: string;
  type?: string;
  originalHeight?: number;
  originalName?: string;
  originalWidth?: number;
  thumbnailUrl?: string;
  url?: string;
  secureUrl?: string;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
};

export type MessageDTO = Pick<
  Message,
  | '_id'
  | 'updated'
  | 'status'
  | 'text'
  | 'type'
  | 'channelID'
  | 'createdAt'
  | 'updatedAt'
> & {
  sender?: UserDTO;
  senderID?: string;
  reactions?: Reaction[];
  attachments?: AttachmentDTO[];
};

export const EVENT_MAP = {
  'health.check': true,
  'channel.created': true,
  'channel.added': true,
  'channel.removed': true, // Xóa thành viên
  'channel.deleted': true, // Xóa channel
  'channel.renamed': true, //Đổi tên channel
  'channel.avatar': true,
  'message.deleted': true,
  'message.new': true,
  'message.updated': true,
  'reaction.deleted': true,
  'reaction.new': true,
  'reaction.updated': true,
  'typing.start': true,
  'typing.stop': true,
};

export type EventTypes = 'all' | keyof typeof EVENT_MAP;

export type MessageEvent = {
  type: EventTypes;
  message?: MessageDTO | object;
  channel?: ChannelDTO;
  dataUpdate?: object;
  channelID?: string;
  user?: UserDTO;
  userID?: string | null;
  me?: OwnUserDTO;
  connectionID?: string;
};

export class MessagePaginationParams {
  @IsOptional()
  @Type(() => Number)
  @IsNumber()
  @Min(0)
  skip?: number;

  @IsOptional()
  @Type(() => Number)
  @IsNumber()
  @Min(1)
  limit?: number;

  @IsOptional()
  @Type(() => String)
  @IsString()
  id_gt?: string;

  @IsOptional()
  @Type(() => String)
  @IsString()
  id_gte?: string;

  @IsOptional()
  @Type(() => String)
  @IsString()
  id_lt?: string;

  @IsOptional()
  @Type(() => String)
  @IsString()
  id_lte?: string;

  @IsOptional()
  @Type(() => String)
  @IsString()
  id_around?: string;
}

export class SendMessageParams {
  @IsOptional()
  @Type(() => String)
  @IsString()
  @Matches(/^[a-zA-Z0-9-]+$/)
  _id?: string;
  @IsOptional()
  @Type(() => String)
  @IsString()
  senderID?: string;
  @IsOptional()
  @IsString()
  text?: string;
}
