import { Message, MessageReaction } from 'src/models/message.model';
import { UserDTO } from './user.dto';

export type Reaction = Pick<MessageReaction, 'reaction'> & {
  reacter: UserDTO;
};

export type MessageDTO = Pick<
  Message,
  '_id' | 'updated' | 'status' | 'text' | 'type'
> & {
  sender: UserDTO;
  reaction: Reaction[];
};
