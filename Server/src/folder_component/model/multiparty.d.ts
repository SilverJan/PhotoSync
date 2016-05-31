/**
 * Created by Jan on 19.05.2016.
 */
export interface IFile {
    fileName: ISubFile[];

}

interface ISubFile {
    fieldName: string;
    originalFilename: string;
    path: string;
    headers: any;
    size: number;
}

export interface IField {
    name: string;
    value: string;
}
