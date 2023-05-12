import { IsNumber, IsOptional, Min, IsString, IsArray } from 'class-validator';
import { Type } from 'class-transformer';
import { Channel } from 'src/models/channel.model';
import { UserDTO } from './user.dto';
import { AttachmentDTO, MessageDTO } from './message.dto';
import { FilterQuery } from 'mongoose';
import { Message } from 'src/models/message.model';
import { PaginationParam } from './pagination_param';

export class CreateChannelDTO {
  @IsArray()
  newMembers: string[];

  @IsOptional()
  @Type(() => String)
  @IsString()
  name?: string;

  @IsOptional()
  @IsString()
  @Type(() => String)
  userID?: string;
}

export type ChannelMemberDTO = {
  user?: UserDTO;
  userID?: string;
  createdAt?: string;
  updatedAt?: string;
  addBy?: string;
};

export type ChannelInfo = Pick<
  Channel,
  '_id' | 'avatar' | 'lastMessageAt' | 'name' | 'createdAt' | 'updatedAt'
> & {
  hiddenChannel?: boolean;
  activeNotify?: boolean;
  createdBy?: UserDTO;
};

export type ChannelDTO = {
  channel: ChannelInfo;
  messages: MessageDTO[];
  members: ChannelMemberDTO[];
  pinnedMessages: MessageDTO[];
};

export class ChannelPaginationParams {
  @Type(() => String)
  @IsString()
  userID: string;

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

export type SortOption = {
  field: string;
  direction: 1 | -1;
};

export type Payload = PaginationParam & {
  filter_conditions: FilterQuery<Channel>;
  sort?: SortOption[];
  query?: string;
  message_filter_conditions?: FilterQuery<Message>;
  attachment_filter_conditions?: FilterQuery<AttachmentDTO>;
};
