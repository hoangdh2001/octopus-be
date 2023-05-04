import {
  AttachmentDTO,
  MessageDTO,
  QuotedMessageDTO,
  Reaction,
  ReactionCount,
} from 'src/dtos/message.dto';
import { UserDTO } from 'src/dtos/user.dto';
import { Message, MessageReaction } from 'src/models/message.model';

export type CallAttachment = (attachmentID: string) => Promise<AttachmentDTO>;

export const convertMessageDTO = async ({
  userID,
  message,
  attachments,
  callUser,
  callQuotedMessage,
}: {
  userID: string;
  message: Message;
  attachments?: AttachmentDTO[] | CallAttachment;
  callUser: (userID: string) => Promise<UserDTO>;
  callQuotedMessage?: (messageID: string) => Promise<Message>;
}): Promise<MessageDTO> => {
  let reactionCounts: ReactionCount = {};

  message.reactions.forEach((reaction) => {
    if (reaction.reaction in reactionCounts) {
      reactionCounts[reaction.reaction] = reactionCounts[reaction.reaction] + 1;
    }
    reactionCounts[reaction.reaction] = 1;
  });

  const ownReactions = message.reactions.filter(
    (reaction) => reaction.reacter_id === userID,
  );

  const messageDTO: MessageDTO = {
    _id: message._id,
    channelID: message.channelID,
    createdAt: message.createdAt,
    updatedAt: message.updatedAt,
    status: message.status,
    text: message.text,
    reactions: await Promise.all(
      message.reactions.map(
        async (messageReaction) =>
          await convertReaction({
            messageReaction,
            callUser,
          }),
      ),
    ),
    ownReactions: await Promise.all(
      ownReactions.map(
        async (ownReaction) =>
          await convertReaction({ messageReaction: ownReaction, callUser }),
      ),
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
    quotedMessage: message.quotedMessageID
      ? await convertQuotedMessage({
          message: await callQuotedMessage(message.quotedMessageID),
          attachments,
          callUser,
        })
      : null,
    reactionCounts: reactionCounts,
    ignoreUser: message.ignoreUser,
  };
  return messageDTO;
};

export const convertReaction = async ({
  messageReaction,
  callUser,
}: {
  messageReaction: MessageReaction;
  callUser: (userID: string) => Promise<UserDTO>;
}): Promise<Reaction> => {
  const reaction: Reaction = {
    reacterID: messageReaction.reacter_id,
    type: messageReaction.reaction,
    reacter: await callUser(messageReaction.reacter_id),
  };
  return reaction;
};

export const convertQuotedMessage = async ({
  message,
  attachments,
  callUser,
}: {
  message: Message;
  attachments?: AttachmentDTO[] | CallAttachment;
  callUser: (userID: string) => Promise<UserDTO>;
}): Promise<QuotedMessageDTO> => {
  const messageDTO: QuotedMessageDTO = {
    _id: message._id,
    channelID: message.channelID,
    createdAt: message.createdAt,
    updatedAt: message.updatedAt,
    status: message.status,
    text: message.text,
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
