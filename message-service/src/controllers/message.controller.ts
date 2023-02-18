import { Controller } from '@nestjs/common';
import { MessageServive } from '../services/message.service';

@Controller('/messages')
export class MessageController {
  constructor(private readonly messageService: MessageServive) {}
}
