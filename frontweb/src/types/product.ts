import { Categorie } from "./categorie";

export type Product = {
  id: number;
  name: string;
  description: string;
  price: number;
  imgUrl: string;
  date: string;
  categories: Categorie[];
};
