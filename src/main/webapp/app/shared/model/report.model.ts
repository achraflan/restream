import dayjs from 'dayjs';
import { IChemin } from 'app/shared/model/chemin.model';

export interface IReport {
  id?: number;
  message?: string;
  dateCreation?: string | null;
  chemin?: IChemin | null;
}

export const defaultValue: Readonly<IReport> = {};
