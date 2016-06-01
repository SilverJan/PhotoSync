import {IPictureVideo} from './pictureVideo';
export interface IFolder {
    _id?: string;
    absolutePath: string;
    name: string;
    childAmount: number;
    size: number;
    selected: boolean;
    pictures: IPictureVideo[];
}