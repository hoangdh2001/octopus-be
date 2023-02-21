import { MessageEvent } from 'src/dtos/message.dto';

export type ListenEvents = {
  identity: (userID: string) => void;
  subscribe: (room: string, otherUserIds: string[]) => void;
  unsubscribe: (roomID: string) => void;
};

export type ServerSideEvents = {
  ping: () => void;
};

export type EmitEvents = {
  noArg: () => void;
  sendMessage: (message: MessageEvent) => void;
  test: (data: string) => void;
};

export type SocketData = {
  name: string;
  age: number;
};
