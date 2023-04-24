import { ChannelDTO, ChannelMemberDTO } from 'src/dtos/channel.dto';
import { Channel, ChannelMember } from 'src/models/channel.model';
import { Message } from 'src/models/message.model';
import { convertMessageDTO } from './message.util';
import { UserDTO } from 'src/dtos/user.dto';

export const convertChannelDTO = async ({
  channel,
  userID,
  messages,
  callUser,
}: {
  channel: Channel;
  userID?: string;
  messages?: Message[];
  callUser: (userID: string) => Promise<UserDTO>;
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
      createdBy: channel.createdBy,
      avatar: channel.avatar,
    },
    messages: await Promise.all(
      messages?.map(async (message) => {
        const messageDTO = await convertMessageDTO({ message, callUser });
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
