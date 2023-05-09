import {
  BadRequestException,
  Body,
  Controller,
  Get,
  OnModuleDestroy,
  OnModuleInit,
  Param,
  Post,
  Query,
  UploadedFile,
  UploadedFiles,
  UseInterceptors,
} from '@nestjs/common';
import { AnyFilesInterceptor, FileInterceptor } from '@nestjs/platform-express';
import { CloudinaryService } from 'src/cloudinary/cloudinary.service';
import { Attachment } from 'src/models/attachment.model';
import { v4 } from 'uuid';
import { StorageService } from './storage.service';
import { UploadApiErrorResponse, UploadApiResponse } from 'cloudinary';
import { FilterQuery } from 'mongoose';

@Controller('/storage')
@UseInterceptors(FileInterceptor('file'))
export class StorageController implements OnModuleInit, OnModuleDestroy {
  constructor(
    private readonly cloudinaryService: CloudinaryService,
    private readonly storageService: StorageService,
  ) {}
  onModuleDestroy() {}
  onModuleInit() {}

  @Post('/upload')
  async uploadImage(
    @UploadedFile() file: Express.Multer.File,
    @Body() { attachmentID }: { attachmentID?: string },
  ): Promise<Attachment | UploadApiErrorResponse> {
    const response = await this.cloudinaryService.uploadImage(file);

    if (this.isUploadApiResponse(response)) {
      return await this.createAttachment({ attachmentID, response, file });
    } else {
      return response;
    }
  }

  @Get('/attachments/:attachmentID')
  async getAttachmentByID(
    @Param('attachmentID') attachmentID: string,
    @Query('filter') filter?: string,
  ) {
    const filterConvert: FilterQuery<Attachment> =
      filter != null && filter != undefined && filter.length > 0
        ? JSON.parse(filter)
        : null;
    const attachment: Attachment = await this.storageService.findAttachmentID({
      attachmentID,
      filter: filterConvert,
    });
    console.log(attachment);

    return {
      attachment: attachment,
    };
  }

  async createAttachment({
    attachmentID,
    response,
    file,
  }: {
    attachmentID?: string;
    response: UploadApiResponse;
    file: Express.Multer.File;
  }): Promise<Attachment> {
    const attachment: Attachment = {
      _id: attachmentID || v4(),
      filesize: response.bytes,
      mimeType: file.mimetype,
      createdAt: response.created_at,
      originalHeight: response.height,
      originalWidth: response.width,
      originalName: file.originalname,
      thumbnailUrl: response.url,
      url: response.url,
      secureUrl: response.secure_url,
      type: response.resource_type,
    };

    return await this.storageService.saveAttachment(attachment);
  }

  isUploadApiResponse(
    user: UploadApiResponse | UploadApiErrorResponse,
  ): user is UploadApiResponse {
    return (user as UploadApiResponse).type !== undefined;
  }
}
