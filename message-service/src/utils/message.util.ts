import { AttachmentDTO, MessageDTO, Reaction } from 'src/dtos/message.dto';
import { UserDTO } from 'src/dtos/user.dto';
import { Message, MessageReaction } from 'src/models/message.model';

export type CallAttachment = (attachmentID: string) => Promise<AttachmentDTO>;

export const convertMessageDTO = async ({
  message,
  attachments,
  callUser,
}: {
  message: Message;
  attachments?: AttachmentDTO[] | CallAttachment;
  callUser: (userID: string) => Promise<UserDTO>;
}): Promise<MessageDTO> => {
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
    sender: await callUser(message.senderID),
    attachments:
      typeof attachments === 'function'
        ? await Promise.all(
            message.attachments?.map(
              async (attachment) => await attachments(attachment),
            ) ?? [],
          )
        : attachments,
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
