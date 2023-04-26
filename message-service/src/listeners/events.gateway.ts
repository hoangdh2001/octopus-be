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
import { Server, Socket } from 'socket.io';
import { MessageEvent } from 'src/dtos/message.dto';
import {
  EmitEvents,
  ListenEvents,
  ServerSideEvents,
  SocketData,
} from './event.map';
import { ChannelService } from 'src/services/channel.service';
import { MessageServive } from 'src/services/message.service';
import { HttpService } from '@nestjs/axios';
import { OwnUserDTO, UserDTO } from 'src/dtos/user.dto';
import { Channel } from 'src/models/channel.model';

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
  constructor(
    private readonly channelService: ChannelService,
    private readonly messageService: MessageServive,
    private readonly httpService: HttpService,
  ) {}

  afterInit(server: any) {
    console.log('Init');
  }

  async handleConnection(client: Socket, ...args: any[]) {
    const {
      user_id: userID,
      user_details: userDetail,
      user_token: token,
    }: {
      user_id: string;
      user_details: UserDTO;
      user_token: string;
    } = JSON.parse(client.handshake.query.data as any);

    console.log(`Connect ${client.id}-${userID}`);

    const firstRes = await this.httpService.axiosRef.get<OwnUserDTO>(
      `http://auth-service/api/users/${userID}/me`,
      { headers: { Authorization: `Bearer ${token}` } },
    );

    const user = firstRes.data;

    user.active = true;
    user.connections.push(client.id);

    const response = await this.httpService.axiosRef.put<OwnUserDTO>(
      `http://auth-service/api/users/${userID}/me`,
      user,
      { headers: { Authorization: `Bearer ${token}` } },
    );

    await this.subcribeAllChannel(response.data);

    this.server.in(client.id).emit('messages', {
      type: 'health.check',
      me: response.data,
      connectionID: client.id,
    });
  }

  async handleDisconnect(client: Socket) {
    const {
      user_id: userID,
      user_details: userDetail,
      user_token: token,
    }: {
      user_id: string;
      user_details: UserDTO;
      user_token: string;
    } = JSON.parse(client.handshake.query.data as any);

    console.log(`Disconnect ${client.id}-${userID}`);

    const firstRes = await this.httpService.axiosRef.get<OwnUserDTO>(
      `http://auth-service/api/users/${userID}/me`,
      { headers: { Authorization: `Bearer ${token}` } },
    );

    const user = firstRes.data;

    await this.unsubcribeAllChannel(user);

    user.active = false;
    user.connections = user.connections.filter(
      (connection) => connection != client.id,
    );

    const response = await this.httpService.axiosRef.put<OwnUserDTO>(
      `http://auth-service/api/users/${userID}/me`,
      user,
      { headers: { Authorization: `Bearer ${token}` } },
    );
    this.server.in(client.id).emit('messages', {
      type: 'health.check',
      me: response.data,
      connectionID: client.id,
    });
  }

  @SubscribeMessage('events')
  handleEvent(@MessageBody() data: unknown): WsResponse<unknown> {
    return { event: 'events', data: data };
  }

  sendMessage(message: MessageEvent) {
    this.server.in(message.channelID).emit('messages', message);
  }

  private async subcribeAllChannel(user: OwnUserDTO) {
    try {
      const channels: Channel[] = await this.channelService.findAllByUser(
        user.id,
      );
      for (const channel of channels) {
        user.connections.forEach((connection) => {
          this.server.in(connection).socketsJoin(channel._id);
        });
      }
    } catch (e) {
      console.log(e);
    }
  }

  private async unsubcribeAllChannel(user: OwnUserDTO) {
    try {
      const channels: Channel[] = await this.channelService.findAllByUser(
        user.id,
      );
      for (const channel of channels) {
        user.connections.forEach((connection) => {
          this.server.in(connection).socketsLeave(channel._id);
        });
      }
    } catch (error) {
      console.log(error);
    }
  }
}
