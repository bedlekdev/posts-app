import { Page } from './page';
import { Sort } from './sort';

export interface Response<T> {
  content: Array<T>;
  pageable: Page;
  last: boolean;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  sort: Sort;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
  sql: string;
}
