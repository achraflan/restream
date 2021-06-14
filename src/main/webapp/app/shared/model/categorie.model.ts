export interface ICategorie {
  id?: number;
  categorieNom?: string | null;
  categorieImage?: string | null;
  categorieActive?: boolean | null;
}

export const defaultValue: Readonly<ICategorie> = {
  categorieActive: false,
};
