import {
  MessageBody,
  OnGatewayConnection,
  OnGatewayDisconnect,
  OnGatewayInit,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
  WsResponse,
} from '@nestjs/websockets';
import { Server } from 'socket.io';
import { MessageEvent } from 'src/dtos/message.dto';
import {
  EmitEvents,
  ListenEvents,
  ServerSideEvents,
  SocketData,
} from './event.map';

@WebSocketGateway({
  maxHttpBufferSize: 1e8,
  transports: ['websocket'],
  cors: { origin: '*', methods: ['GET', 'POST'], credentials: true },
})
export class EventsGateway
  implements OnGatewayConnection, OnGatewayDisconnect, OnGatewayInit
{
  @WebSocketServer()
  server: Server<ListenEvents, EmitEvents, ServerSideEvents, SocketData>;

  afterInit(server: any) {
    console.log('Init');
  }

  handleConnection(client: any, ...args: any[]) {
    console.log('Connect');
  }

  handleDisconnect(client: any) {
    console.log('Disconnect');
  }

  @SubscribeMessage('events')
  handleEvent(@MessageBody() data: unknown): WsResponse<unknown> {
    return { event: 'events', data: data };
  }

  sendMessage(message: MessageEvent) {
    this.server.emit('messages', message);
  }
}
