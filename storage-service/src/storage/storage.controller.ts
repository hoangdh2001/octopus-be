import {
  BadRequestException,
  Controller,
  OnModuleDestroy,
  OnModuleInit,
  Post,
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

@Controller('/storage')
@UseInterceptors(AnyFilesInterceptor())
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
  ): Promise<Attachment | UploadApiErrorResponse> {
    const response = await this.cloudinaryService
      .uploadImage(file)
      .catch(() => {
        throw new BadRequestException('Invalid file type.');
      });

    if (this.isUploadApiResponse(response)) {
      return await this.createAttachment(response, file);
    } else {
      return response;
    }
  }

  @Post('/uploads')
  async uploadImages(
    @UploadedFiles() files: Express.Multer.File[],
  ): Promise<(Attachment | UploadApiErrorResponse)[]> {
    const responses = await this.cloudinaryService
      .uploadImages(files)
      .catch(() => {
        throw new BadRequestException('Invalid file type');
      });

    console.log(responses);

    return await Promise.all(
      responses.map(async (response, index) => {
        if (this.isUploadApiResponse(response)) {
          return await this.createAttachment(response, files[index]);
        } else {
          return response;
        }
      }),
    );
  }

  async createAttachment(
    response: UploadApiResponse,
    file: Express.Multer.File,
  ): Promise<Attachment> {
    const attachment: Attachment = {
      _id: v4(),
      filesize: response.bytes,
      mineType: file.mimetype,
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
