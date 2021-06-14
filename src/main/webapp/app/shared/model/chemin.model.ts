import { IChaine } from 'app/shared/model/chaine.model';
import { TypeChemin } from 'app/shared/model/enumerations/type-chemin.model';

export interface IChemin {
  id?: number;
  cheminNon?: string | null;
  type?: TypeChemin | null;
  cheminValide?: boolean | null;
  cheminMarche?: boolean | null;
  chaine?: IChaine | null;
}

export const defaultValue: Readonly<IChemin> = {
  cheminValide: false,
  cheminMarche: false,
};
