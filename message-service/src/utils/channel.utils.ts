import { ChannelDTO, ChannelMemberDTO } from 'src/dtos/channel.dto';
import { Channel, ChannelMember } from 'src/models/channel.model';
import { Message } from 'src/models/message.model';
import { convertMessageDTO } from './message.util';

export const convertChannelDTO = async ({
  channel,
  userID,
  messages,
  callMembers,
}: {
  channel: Channel;
  userID?: string;
  messages?: Message[];
  callMembers: (member: ChannelMember) => Promise<ChannelMemberDTO>;
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
    },
    messages: messages?.map((message) => {
      const messageDTO = convertMessageDTO(message);
      return messageDTO;
    }),
    members: await Promise.all(
      channel.members.map(async (member) => {
        return await callMembers(member);
      }),
    ),
  };
  return channelDTO;
};
