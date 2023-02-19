import { IsNumber, IsOptional, Min, IsArray } from 'class-validator';
import { Type } from 'class-transformer';

export class Pagination {
  @IsOptional()
  @Type(() => Number)
  @IsNumber()
  @Min(0)
  skip?: number;

  @IsOptional()
  @Type(() => Number)
  @IsNumber()
  @Min(1)
  limit?: number;

  @IsOptional()
  @Type(() => Number)
  @IsNumber()
  @Min(1)
  totalItem?: number;

  @IsOptional()
  @Type(() => Number)
  @IsNumber()
  @Min(1)
  totalPage?: number;

  @IsOptional()
  @Type(() => Array)
  @IsArray()
  data: object[];
}
