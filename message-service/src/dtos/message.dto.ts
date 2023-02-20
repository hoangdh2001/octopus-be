import { Message, MessageReaction } from 'src/models/message.model';
import { UserDTO } from './user.dto';
import { IsNumber, IsOptional, Min, IsString } from 'class-validator';
import { Type } from 'class-transformer';

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
