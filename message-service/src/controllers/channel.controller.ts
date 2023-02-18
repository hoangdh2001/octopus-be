import { Controller, Get } from '@nestjs/common';
import { ChannelService } from '../services/channel.service';

@Controller('/channels')
export class ChannelController {
  constructor(private readonly channelService: ChannelService) {}

  @Get()
  test(): string {
    return this.channelService.test();
  }
}
