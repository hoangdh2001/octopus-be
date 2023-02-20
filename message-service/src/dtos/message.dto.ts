import { Message, MessageReaction } from 'src/models/message.model';
import { UserDTO } from './user.dto';
import { IsNumber, IsOptional, Min, IsString, Matches } from 'class-validator';
import { Type } from 'class-transformer';
import { ChannelDTO } from './channel.dto';

export type Reaction = Pick<MessageReaction, 'reaction'> & {
  reacter?: UserDTO;
  reacterID?: string;
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
};

export const EVENT_MAP = {
  'channel.created': true,
  'channel.added': true,
  'channel.removed': true, // Xóa thành viên
  'channel.deleted': true, // Xóa channel
  'channel.renamed': true, //Đổi tên channel
  'channel.avatar': true,
  'message.deleted': true,
  'message.created': true,
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
  user?: UserDTO;
  userID?: string | null;
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
}

export class SendMessageParams {
  @IsOptional()
  @Type(() => String)
  @IsString()
  @Matches(/^[a-zA-Z0-9-]+$/)
  messageID?: string;
  @IsOptional()
  @Type(() => String)
  @IsString()
  senderID?: string;
  @IsOptional()
  @IsString()
  text?: string;
}
