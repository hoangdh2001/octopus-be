import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Attachment, AttachmentDocument } from 'src/models/attachment.model';

@Injectable()
export class StorageService {
  constructor(
    @InjectModel(Attachment.name)
    private channelModel: Model<AttachmentDocument>,
  ) {}

  async saveAttachment(attachment: Attachment) {
    const newAttachment: Attachment = await this.channelModel.create(
      attachment,
    );
    return newAttachment;
  }
}
