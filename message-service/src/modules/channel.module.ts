import { Module } from '@nestjs/common';
import { JwtModule } from '@nestjs/jwt';
import { MongooseModule } from '@nestjs/mongoose';
import { EurekaModule } from 'nestjs-eureka';
import { ChannelController } from '../controllers/channel.controller';
import { Channel, ChannelSchema } from '../models/channel.model';
import { ChannelService } from '../services/channel.service';
import { EventModule } from './events.module';
import { MessageModule } from './message.module';
import { ConfigModule } from '@nestjs/config';
import * as Joi from 'joi';
import { Partitioners } from 'kafkajs';
import { KafkaModule } from '@rob3000/nestjs-kafka';
import { HttpModule } from '@nestjs/axios';
import { DiscoveryInterceptor } from 'src/providers/discovery.interceptor';
@Module({
  imports: [
    HttpModule.register({
      timeout: 5000,
      maxRedirects: 5,
      params: {},
    }),
    ConfigModule.forRoot({
      envFilePath: '.env',
      validationSchema: Joi.object({
        NODE_ENV: Joi.string()
          .valid('development', 'production', 'test', 'provision')
          .default('development'),
        PORT: Joi.number().default(3000),
        EUREKA_HOST: Joi.string().default('localhost'),
        MONGODB_API: Joi.string(),
        MONGODB_USER: Joi.string(),
        MONGODB_PASS: Joi.string(),
        KAFKA_HOST: Joi.string().default('localhost'),
        KAFKA_PORT: Joi.string().default('9092'),
      }),
    }),
    MongooseModule.forRootAsync({
      useFactory: () => {
        return {
          uri: process.env.MONGODB_API,
          user: process.env.MONGODB_USER,
          pass: process.env.MONGODB_PASS,
        };
      },
    }),
    MongooseModule.forFeature([{ name: Channel.name, schema: ChannelSchema }]),
    EurekaModule.forRoot({
      eureka: {
        host: process.env.EUREKA_HOST,
        port: 8761,
        servicePath: '/eureka/apps',
        maxRetries: 10,
        requestRetryDelay: 10000,
      },
      service: {
        name: 'message-service',
        port: parseInt(process.env.PORT, 10),
      },
    }),
    JwtModule.register({
      secret: 'khoa_luan_tot_nghiep_nhom40_octopus',
      signOptions: {
        algorithm: 'HS512',
        expiresIn: 24 * 60 * 60,
      },
    }),
    KafkaModule.register([
      {
        name: 'MESSAGE_SERVICE',
        options: {
          client: {
            brokers: [`${process.env.KAFKA_HOST}:${process.env.KAFKA_PORT}`],
          },
          consumer: {
            groupId: 'message-consumer',
          },
          producer: {
            createPartitioner: Partitioners.LegacyPartitioner,
          },
        },
      },
    ]),
    MessageModule,
    EventModule,
  ],
  controllers: [ChannelController],
  providers: [ChannelService, DiscoveryInterceptor],
})
export class ChannelModule {}
