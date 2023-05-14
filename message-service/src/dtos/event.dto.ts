export const EVENT_MAP = {
  'health.check': true,
  'channel.added': true,
  'channel.deleted': true, // Xóa channel
  'channel.updated': true,
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
  'member.added': true,
  'member.removed': true,
  'channel.truncated': true,
};

export type EventTypes = 'all' | keyof typeof EVENT_MAP;

export type EventDTO = {
  type: EventTypes;
};
