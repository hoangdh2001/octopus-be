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
import { ClientsModule, Transport } from '@nestjs/microservices';
import { Partitioners } from 'kafkajs';
@Module({
  imports: [
    ConfigModule.forRoot({
      envFilePath: '.env',
      validationSchema: Joi.object({
        NODE_ENV: Joi.string()
          .valid('development', 'production', 'test', 'provision')
          .default('development'),
        PORT: Joi.number().default(3000),
        EUREKA_HOST: Joi.string().default('localhost'),
        DB_USERNAME: Joi.string().default('admin'),
        DB_PASSWORD: Joi.string().default('admin'),
        DB_HOST: Joi.string().default('localhost'),
        DB_PORT: Joi.string().default('27017'),
        DB_DATABASE: Joi.string().default('messagedb'),
      }),
    }),
    MongooseModule.forRootAsync({
      useFactory: () => {
        console.log(
          `mongodb://${process.env.DB_USERNAME}:${process.env.DB_PASSWORD}@${process.env.DB_HOST}:${process.env.DB_PORT}/${process.env.DB_DATABASE}?authMechanism=DEFAULT&authSource=messagedb`,
        );
        return {
          uri: `mongodb://${process.env.DB_HOST}:${process.env.DB_PORT}/${process.env.DB_DATABASE}`,
          user: process.env.DB_USERNAME,
          pass: process.env.DB_PASSWORD,
          dbName: process.env.DB_DATABASE,
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
    JwtModule.register({ secret: 'khoa_luan_tot_nghiep_nhom40_octopus' }),
    ClientsModule.register([
      {
        name: 'MESSAGE_SERVICE',
        transport: Transport.KAFKA,
        options: {
          client: {
            clientId: 'octopus-client-id',
            brokers: ['localhost:9092'],
          },
          producer: {
            createPartitioner: Partitioners.DefaultPartitioner,
          },
          producerOnlyMode: true,
        },
      },
    ]),
    MessageModule,
    EventModule,
  ],
  controllers: [ChannelController],
  providers: [ChannelService],
})
export class ChannelModule {}
