export type PaginationParam = {
  limit: number;
  before: number;
  after: number;
  offset?: number;
  next?: number;
  id_around?: string;
  id_gt?: string;
  id_gte?: string;
  id_lt?: string;
  id_lte?: string;
  created_at_after_or_equal?: string;
  created_at_after?: string;
  created_at_before_or_equal?: string;
  created_at_before?: string;
  created_at_around?: string;
};
