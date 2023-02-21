import { Module } from '@nestjs/common';
import { JwtModule } from '@nestjs/jwt';
import { MongooseModule } from '@nestjs/mongoose';
import { EurekaModule } from 'nestjs-eureka';
import { ChannelController } from '../controllers/channel.controller';
import { Channel, ChannelSchema } from '../models/channel.model';
import { ChannelService } from '../services/channel.service';
import { EventModule } from './events.module';
import { MessageModule } from './message.module';
import ip from 'ip';
@Module({
  imports: [
    MongooseModule.forRoot('mongodb://localhost:27017/messagedb'),
    MongooseModule.forFeature([{ name: Channel.name, schema: ChannelSchema }]),
    EurekaModule.forRoot({
      eureka: {
        host: 'localhost',
        port: 8761,
        servicePath: '/eureka/apps',
        maxRetries: 10,
        requestRetryDelay: 10000,
      },
      // disable: false,
      // disableDiscovery: false,
      // instanceExtra: {
      //   hostName: 'localhost',
      //   status: 'UP',
      //   ipAddr: '127.0.0.1',
      //   port: {
      //     $: 3000,
      //     '@enabled': true,
      //   },
      //   vipAddress: 'message-service',
      //   instanceId: 'message-service',
      //   dataCenterInfo: {
      //     '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      //     name: 'MyOwn',
      //   },
      //   app: 'message-serivce',
      // },
      service: {
        name: 'message-service',
        port: 3000,
      },
    }),
    JwtModule.register({ secret: 'scretKey' }),
    MessageModule,
    EventModule,
  ],
  controllers: [ChannelController],
  providers: [ChannelService],
})
export class ChannelModule {}
