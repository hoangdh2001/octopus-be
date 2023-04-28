import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { MongooseModule } from '@nestjs/mongoose';
import * as Joi from 'joi';
import { EurekaModule } from 'nestjs-eureka';
import { CloudinaryModule } from 'src/cloudinary/cloudinary.module';
import { Attachment, AttachmentSchema } from 'src/models/attachment.model';
import { StorageController } from './storage.controller';
import { StorageService } from './storage.service';

@Module({
  imports: [
    ConfigModule.forRoot({
      envFilePath: '.env',
      validationSchema: Joi.object({
        NODE_ENV: Joi.string()
          .valid('development', 'production', 'test', 'provision')
          .default('development'),
        PORT: Joi.number().default(3001),
        EUREKA_HOST: Joi.string().default('localhost'),
        MONGODB_API: Joi.string(),
        MONGODB_USER: Joi.string(),
        MONGODB_PASS: Joi.string(),
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
    MongooseModule.forFeature([
      { name: Attachment.name, schema: AttachmentSchema },
    ]),
    EurekaModule.forRoot({
      eureka: {
        host: process.env.EUREKA_HOST,
        port: 8761,
        servicePath: '/eureka/apps',
        maxRetries: 10,
        requestRetryDelay: 10000,
      },
      service: {
        name: 'storage-service',
        port: parseInt(process.env.PORT, 10),
      },
    }),
    CloudinaryModule,
  ],
  controllers: [StorageController],
  providers: [StorageService],
})
export class StorageModule {}
