import {
  MessageBody,
  OnGatewayConnection,
  OnGatewayDisconnect,
  OnGatewayInit,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Server } from 'socket.io';

@WebSocketGateway()
export class EventsGateway
  implements OnGatewayConnection, OnGatewayDisconnect, OnGatewayInit
{
  @WebSocketServer()
  server: Server;

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
  handleEvent(@MessageBody() data: string): string {
    console.log('Received');
    return 'Hello';
  }
}
