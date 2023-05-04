import { Logger, Module } from '@nestjs/common';
import { JwtModule } from '@nestjs/jwt';
import { MongooseModule } from '@nestjs/mongoose';
import { EurekaModule } from 'nestjs-eureka';
import { ChannelController } from '../controllers/channel.controller';
import { Channel, ChannelSchema } from '../models/channel.model';
import { ChannelService } from '../services/channel.service';
import { MessageModule } from './message.module';
import { ConfigModule, ConfigService } from '@nestjs/config';
import * as Joi from 'joi';
import { Partitioners } from 'kafkajs';
import { KafkaModule } from '@rob3000/nestjs-kafka';
import { HttpModule } from '@nestjs/axios';
import { DiscoveryInterceptor } from 'src/providers/discovery.interceptor';
import { EventsGateway } from 'src/listeners/events.gateway';
import { FirebaseAdminModule } from '@aginix/nestjs-firebase-admin';
import * as admin from 'firebase-admin';
const serviceAccount = require('../../octopus-40-firebase-adminsdk-pdtjk-7d430b6962.json');
@Module({
  imports: [
    HttpModule.register({
      timeout: 30000,
      maxRedirects: 1000,
      params: {},
      headers: {
        Accept: '*/*',
        'Accept-Encoding': 'gzip, deflate, br',
        Connection: 'keep-alive',
      },
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
        REDIS_API: Joi.string(),
        REDIS_USER: Joi.string(),
        REDIS_PASS: Joi.string(),
        REDIS_PORT: Joi.string().default('25061'),
        CACHE_TTL: Joi.number().default(600),
      }),
    }),
    MongooseModule.forRootAsync({
      imports: [ConfigModule],
      useFactory: (config: ConfigService) => {
        return {
          uri: config.get('MONGODB_API'),
          user: config.get('MONGODB_USER'),
          pass: config.get('MONGODB_PASS'),
        };
      },
      inject: [ConfigService],
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
        expiresIn: 24 * 60 * 60 * 1000,
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
    // CacheModule.registerAsync<RedisClientOptions>({
    //   isGlobal: true,
    //   imports: [ConfigModule],
    //   useFactory: async (configService: ConfigService) => ({
    //     store: (await redisStore({
    //       url:
    //         configService.get('REDIS_API') != null
    //           ? configService.get('REDIS_API')
    //           : `rediss://${configService.get(
    //               'REDIS_USER',
    //             )}:${configService.get(
    //               'REDIS_PASS',
    //             )}@db-redis-sgp1-58343-do-user-12877679-0.b.db.ondigitalocean.com:${configService.get(
    //               'REDIS_PORT',
    //             )}`,
    //       ttl: configService.get('CACHE_TTL'),
    //       socket: {
    //         keepAlive: 300,
    //         tls: false,
    //         reconnectStrategy: 2000,

    //       },
    //     })) as unknown as CacheStore,
    //   }),
    //   inject: [ConfigService],
    // }),
    MessageModule,
    FirebaseAdminModule.forRootAsync({
      useFactory: () => {
        return {
          credential: admin.credential.cert(
            serviceAccount as Partial<admin.ServiceAccount>,
          ),
          projectId: serviceAccount.project_id,
        };
      },
    }),
  ],
  controllers: [ChannelController],
  providers: [
    ChannelService,
    DiscoveryInterceptor,
    EventsGateway,
    // { provide: APP_INTERCEPTOR, useClass: CacheInterceptor },
  ],
})
export class ChannelModule {}
