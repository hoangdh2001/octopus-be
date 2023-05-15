import {
  ChannelDTO,
  ChannelInfo,
  ChannelMemberDTO,
  ReadDTO,
} from 'src/dtos/channel.dto';
import { Channel, ChannelMember } from 'src/models/channel.model';
import { Message } from 'src/models/message.model';
import { CallAttachment, convertMessageDTO } from './message.util';
import { UserDTO } from 'src/dtos/user.dto';
import { AttachmentDTO, MessageDTO } from 'src/dtos/message.dto';

export const convertChannelDTO = async ({
  channel,
  userID,
  messages,
  callUser,
  attachments,
  callQuotedMessage,
}: {
  channel: Channel;
  userID?: string;
  messages?: Message[];
  callUser: (userID: string) => Promise<UserDTO>;
  attachments?: AttachmentDTO[] | CallAttachment;
  callQuotedMessage?: (messageID: string) => Promise<Message>;
}): Promise<ChannelDTO> => {
  const messagesDTO = await Promise.all(
    messages?.map(async (message) => {
      const messageDTO = await convertMessageDTO({
        userID,
        message,
        callUser,
        attachments,
        callQuotedMessage: callQuotedMessage,
      });
      return messageDTO;
    }) ?? [],
  );

  const pinnedMessages = messagesDTO.filter((message) => message.pinned);

  const member = channel.members.find((member) => member.userID == userID);

  const channelDTO: ChannelDTO = {
    channel: {
      _id: channel._id,
      name: channel.name,
      lastMessageAt: channel.lastMessageAt,
      createdAt: channel.createdAt,
      updatedAt: channel.updatedAt,
      hiddenChannel: member.hidden,
      activeNotify: member.activeNotify,
      createdBy:
        channel.createdBy != null && channel.createdBy != undefined
          ? await callUser(channel.createdBy)
          : null,
      avatar: channel.avatar,
    },
    messages: messagesDTO,
    members: await Promise.all(
      channel.members.map(async (member): Promise<ChannelMemberDTO> => {
        const user = await callUser(member.userID);
        return {
          user,
          createdAt: member.createdAt,
          updatedAt: member.createdAt,
          userID: member.userID,
          addBy: member.addBy,
        };
      }),
    ),
    read: await Promise.all(
      channel.members.map(async (member) => {
        const read: ReadDTO = {
          lastRead: member.lastRead,
          unreadMessage: member.unreadMessage,
          user: await callUser(member.userID),
        };
        return read;
      }),
    ),
    pinnedMessages: pinnedMessages,
  };
  return channelDTO;
};

export const convertChannelModel = async ({
  userID,
  channel,
  callUser,
}: {
  userID: string;
  channel: Channel;
  callUser: (userID: string) => Promise<UserDTO>;
}): Promise<ChannelInfo> => {
  return {
    _id: channel._id,
    name: channel.name,
    lastMessageAt: channel.lastMessageAt,
    createdAt: channel.createdAt,
    updatedAt: channel.updatedAt,
    hiddenChannel: channel.members.find((member) => member.userID == userID)
      .hidden,
    activeNotify: channel.members.find((member) => member.userID == userID)
      .activeNotify,
    createdBy:
      channel.createdBy != null && channel.createdBy != undefined
        ? await callUser(channel.createdBy)
        : null,
    avatar: channel.avatar,
  };
};

export const convertMemberDTO = async ({
  member,
  callUser,
}: {
  member: ChannelMember;
  callUser: (userID: string) => Promise<UserDTO>;
}): Promise<ChannelMemberDTO> => {
  const userDTO = await callUser(member.userID);
  return {
    user: userDTO,
    createdAt: member.createdAt,
    updatedAt: member.updatedAt,
    userID: member.userID,
    addBy: member.addBy,
  };
};

export const convertReadDTO = async ({
  userID,
  channel,
  callUser,
}: {
  userID: string;
  channel: Channel;
  callUser: (userID: string) => Promise<UserDTO>;
}): Promise<ReadDTO> => {
  const member = channel.members.find((member) => member.userID == userID);
  return {
    user: await callUser(member.userID),
    lastRead: member.lastRead,
    unreadMessage: member.unreadMessage,
  };
};
