import { Module } from '@nestjs/common';
import { EventsGateway } from 'src/listeners/events.gateway';

@Module({
  providers: [EventsGateway],
  exports: [EventsGateway],
})
export class EventModule {}
