import { IsNumber, IsOptional, Min, IsString } from 'class-validator';
import { Type } from 'class-transformer';
import { Message } from 'src/models/message.model';
import { Channel, ChannelMember } from 'src/models/channel.model';
import { UserDTO } from './user.dto';

export type ChannelMemberDTO = {
  user: UserDTO;
};

export type ChannelResponse = Pick<
  Channel,
  '_id' | 'avatar' | 'lastMessageAt' | 'name'
> & {
  hiddenChannel: boolean;
  activeNotify: boolean;
};

export type ChannelDTO = {
  channel: ChannelResponse;
  message: Message;
  members: ChannelMemberDTO;
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
