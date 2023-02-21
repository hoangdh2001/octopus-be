import { MessageDTO, Reaction } from 'src/dtos/message.dto';
import { Message, MessageReaction } from 'src/models/message.model';

export const convertMessageDTO = (message: Message): MessageDTO => {
  const messageDTO: MessageDTO = {
    _id: message._id,
    channelID: message.channelID,
    createdAt: message.createdAt,
    updatedAt: message.updatedAt,
    status: message.status,
    text: message.text,
    reactions: message.reactions.map((messageReaction) =>
      convertReaction(messageReaction),
    ),
    senderID: message.senderID,
    type: message.type,
    updated: message.updated,
  };
  return messageDTO;
};

export const convertReaction = (messageReaction: MessageReaction) => {
  const reaction: Reaction = {
    reacterID: messageReaction.reacter_id,
    reaction: messageReaction.reaction,
  };
  return reaction;
};
