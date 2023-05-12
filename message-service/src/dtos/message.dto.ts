import { OwnUserDTO, UserDTO } from './user.dto';
import { IsNumber, IsOptional, Min, IsString, Matches } from 'class-validator';
import { Type } from 'class-transformer';
import { ChannelDTO, ChannelMemberDTO } from './channel.dto';
import { Message, MessageReaction } from '../models/message.model';
import { EventTypes } from './event.dto';

export type Reaction = {
  reacter?: UserDTO;
  reacterID?: string;
  type: string;
};

export type AttachmentDTO = {
  _id: string;
  filesize?: number;
  mimeType?: string;
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

export type ReactionCount = { [key: string]: number };

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
  | 'ignoreUser'
  | 'pinned'
> & {
  sender?: UserDTO;
  senderID?: string;
  reactions?: Reaction[];
  ownReactions?: Reaction[];
  attachments?: AttachmentDTO[];
  quotedMessage?: QuotedMessageDTO;
  reactionCounts?: ReactionCount;
  pinnedBy?: UserDTO;
};

export type QuotedMessageDTO = Omit<
  MessageDTO,
  'quotedMessage' | 'reactions' | 'reactionCounts' | 'ownReactions'
>;

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
  members?: ChannelMemberDTO[];
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
  attachments?: AttachmentDTO[];
  quotedMessageID?: string;
}
