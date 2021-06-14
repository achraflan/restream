import { IChaine } from 'app/shared/model/chaine.model';

export interface ITag {
  id?: number;
  tagNom?: string | null;
  chaines?: IChaine[] | null;
}

export const defaultValue: Readonly<ITag> = {};
