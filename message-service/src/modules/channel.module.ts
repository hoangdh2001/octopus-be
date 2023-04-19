import { Module } from '@nestjs/common';
import { JwtModule } from '@nestjs/jwt';
import { MongooseModule } from '@nestjs/mongoose';
import { EurekaModule } from 'nestjs-eureka';
import { ChannelController } from '../controllers/channel.controller';
import { Channel, ChannelSchema } from '../models/channel.model';
import { ChannelService } from '../services/channel.service';
import { EventModule } from './events.module';
import { MessageModule } from './message.module';
import { ConfigModule, ConfigService } from '@nestjs/config';
import * as Joi from 'joi';
import { Partitioners } from 'kafkajs';
import { KafkaModule } from '@rob3000/nestjs-kafka';
import { HttpModule } from '@nestjs/axios';
import { DiscoveryInterceptor } from 'src/providers/discovery.interceptor';
import { RedisClientOptions } from 'redis';
import { APP_INTERCEPTOR } from '@nestjs/core';
import {
  CacheInterceptor,
  CacheModule,
  CacheStore,
} from '@nestjs/cache-manager';
import { redisStore } from 'cache-manager-redis-store';
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
    CacheModule.registerAsync<RedisClientOptions>({
      isGlobal: true,
      imports: [ConfigModule],
      useFactory: async (configService: ConfigService) => ({
        store: (await redisStore({
          url:
            configService.get('REDIS_API') != null
              ? configService.get('REDIS_API')
              : `rediss://${configService.get(
                  'REDIS_USER',
                )}:${configService.get(
                  'REDIS_PASS',
                )}@db-redis-sgp1-58343-do-user-12877679-0.b.db.ondigitalocean.com:${configService.get(
                  'REDIS_PORT',
                )}`,
          ttl: configService.get('CACHE_TTL'),
        })) as unknown as CacheStore,
      }),
      inject: [ConfigService],
    }),
    MessageModule,
    EventModule,
  ],
  controllers: [ChannelController],
  providers: [
    ChannelService,
    DiscoveryInterceptor,
    { provide: APP_INTERCEPTOR, useClass: CacheInterceptor },
  ],
})
export class ChannelModule {}
