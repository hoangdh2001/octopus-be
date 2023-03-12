import { NestFactory } from '@nestjs/core';
import { NestExpressApplication } from '@nestjs/platform-express';
import helmet from 'helmet';
import { ValidationPipe } from '@nestjs/common';
import { AppInterceptor } from './app.interceptor';
import { ChannelModule } from './modules/channel.module';
import { SwaggerModule, DocumentBuilder } from '@nestjs/swagger';

async function bootstrap() {
  const app = await NestFactory.create<NestExpressApplication>(ChannelModule);
  app.enableCors({ origin: '*' });
  app.use(helmet());
  app.useGlobalPipes(new ValidationPipe());
  app.useGlobalInterceptors(new AppInterceptor());
  const config = new DocumentBuilder()
    .setTitle('Cats example')
    .setDescription('The cats API description')
    .setVersion('1.0')
    .addTag('cats')
    .build();
  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('api', app, document);
  // app.connectMicroservice<MicroserviceOptions>({
  //   transport: Transport.KAFKA,
  //   options: {
  //     client: {
  //       clientId: 'test-id',
  //       brokers: ['localhost:9092'],
  //     },
  //     consumer: {
  //       groupId: 'message-consumer',
  //     },
  //     producer: {
  //       createPartitioner: Partitioners.LegacyPartitioner,
  //     },
  //   },
  // });
  // await app.startAllMicroservices();
  await app.listen(3000);
}
bootstrap();
