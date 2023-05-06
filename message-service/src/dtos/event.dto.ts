export const EVENT_MAP = {
  'health.check': true,
  'channel.created': true,
  'channel.added': true,
  'channel.removed': true, // Xóa thành viên
  'channel.deleted': true, // Xóa channel
  'channel.renamed': true, //Đổi tên channel
  'channel.avatar': true,
  'message.deleted': true,
  'message.new': true,
  'message.updated': true,
  'reaction.deleted': true,
  'reaction.new': true,
  'reaction.updated': true,
  'typing.start': true,
  'typing.stop': true,
  'call.accept': true,
  'call.decline': true,
  'call.end': true,
};

export type EventTypes = 'all' | keyof typeof EVENT_MAP;

export type EventDTO = {
  type: EventTypes;
};
