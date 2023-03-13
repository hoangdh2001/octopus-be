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
  app.setGlobalPrefix('api');
  const config = new DocumentBuilder().build();
  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('api', app, document);
  await app.listen(3000);
}
bootstrap();
