import {
  IsNumber,
  IsOptional,
  Min,
  IsString,
  IsArray,
} from 'class-validator';
import { Type } from 'class-transformer';
import { Channel } from 'src/models/channel.model';
import { UserDTO } from './user.dto';
import { MessageDTO } from './message.dto';

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
};

export type ChannelInfo = Pick<
  Channel,
  '_id' | 'avatar' | 'lastMessageAt' | 'name' | 'createdAt' | 'updatedAt' | 'createdBy'
> & {
  hiddenChannel?: boolean;
  activeNotify?: boolean;
};

export type ChannelDTO = {
  channel: ChannelInfo;
  messages: MessageDTO[];
  members: ChannelMemberDTO[];
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
