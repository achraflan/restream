import { IChemin } from 'app/shared/model/chemin.model';
import { ICategorie } from 'app/shared/model/categorie.model';
import { ITag } from 'app/shared/model/tag.model';

export interface IChaine {
  id?: number;
  chaineNom?: string | null;
  chaineImage?: string | null;
  chaineActive?: boolean | null;
  chemins?: IChemin[] | null;
  categorie?: ICategorie | null;
  tags?: ITag[] | null;
}

export const defaultValue: Readonly<IChaine> = {
  chaineActive: false,
};
