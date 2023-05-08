import { ChannelDTO, ChannelMemberDTO } from 'src/dtos/channel.dto';
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
  const channelDTO: ChannelDTO = {
    channel: {
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
    },
    messages: await Promise.all(
      messages?.map(async (message) => {
        const messageDTO = await convertMessageDTO({
          userID,
          message,
          callUser,
          attachments,
          callQuotedMessage: callQuotedMessage,
        });
        return messageDTO;
      }),
    ),
    members: await Promise.all(
      channel.members.map(async (member): Promise<ChannelMemberDTO> => {
        const user = await callUser(member.userID);
        return {
          user,
          createdAt: member.createdAt,
          updatedAt: member.createdAt,
          userID: member.userID,
        };
      }),
    ),
  };
  return channelDTO;
};
