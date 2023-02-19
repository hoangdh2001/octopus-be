import { ChannelDTO, ChannelMemberDTO } from 'src/dtos/channel.dto';
import { Channel } from 'src/models/channel.model';
import { Message } from 'src/models/message.model';
import { convertMessageDTO } from './message.util';

export const convertChannelDTO = async ({
  channel,
  userID,
  messages,
}: {
  channel: Channel;
  userID?: string;
  messages?: Message[];
}): Promise<ChannelDTO> => {
  const channelDTO: ChannelDTO = {
    channel: {
      ...channel,
      hiddenChannel: channel.members.find((member) => member.userID === userID)
        .hidden,
      activeNotify: channel.members.find((member) => member.userID === userID)
        .activeNotify,
    },
    messages: await Promise.all(
      messages?.map(async (message) => {
        const messageDTO = await convertMessageDTO(message);
        return messageDTO;
      }),
    ),
    members: channel.members.map((member) => {
      return {
        userID: member.userID,
      };
    }),
  };
  return channelDTO;
};
