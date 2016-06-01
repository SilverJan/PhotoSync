import {IFolder} from '../model/folder';
import {IPictureVideo} from '../model/pictureVideo';
import {Stats} from 'fs';
import {isPicture, isStringNullOrEmpty, isVideo} from '../../util/utils';
import {BACKUP_PATH} from '../../util/constants';
import {Logger} from '../../util/logger';
import forEachChild = ts.forEachChild;
const fs = require('fs');
export default class FolderService {
    private clazzName = "FolderService";

    // findById(id: string) {
    //     return Promise.resolve(this._demoMessage).then(
    //         (messages: Array<IMessage>) => messages.filter(message => message._id === id)[0]
    //     );
    // }

    findByName(name: string): Promise<IFolder> {
        return new Promise((resolve, reject) => {
            fs.readdir(BACKUP_PATH + '/' + name, (err, data) => {
                if (err) {
                    reject(err);
                }
                resolve(data);
            });
        });
    };

    find(query?: any): Promise<IFolder[]> {
        this.assertFolderExistsOtherwiseCreate(BACKUP_PATH);
        return new Promise((resolve, reject) => {
            fs.readdir(BACKUP_PATH, (err, data) => {
                if (err) {
                    reject(err);
                }

                let folders: IFolder[] = [];

                for (let i = 0; i !== data.length; i++) {
                    let folder = <IFolder>{};
                    folder.name = data[i];
                    folder.absolutePath = BACKUP_PATH + '/' + folder.name;
                    folder.childAmount = this.getFolderChildAmount(folder);
                    folder.size = this.getFolderSize(folder);
                    folder.selected = true;
                    folder.pictures = this.getFolderPictureVideos(folder);
                    folders.push(folder);
                }

                resolve(folders);
            });
        });
    };

    saveFile(files, fields, folderToPutIn: string): Promise<void> {
        return new Promise((resolve, reject) => {
            try {
                this.assertFolderExistsOtherwiseCreate(BACKUP_PATH);

                if (isStringNullOrEmpty(folderToPutIn)) {
                    folderToPutIn = fields['folderToPutIn'][0];
                }

                // STEP 1: Define recent, wrong file path (wrong from multiparty)
                // STEP 2: Define new, correct file path from fields 
                // (intended parentPath name is passed, f.e. 'Download')

                if (fields === undefined || files === undefined) {
                    resolve();
                    // TODO handle this
                    // reject("Fields or files are undefined! Request not valid!")
                    return;
                }
                let sortedFilePathParent = BACKUP_PATH + '/' + folderToPutIn;
                let sortedFilePath = sortedFilePathParent + '/';

                // TODO Add multiphoto upload handling
                let fileName = '';
                Object.keys(files).forEach(function (name) {
                    fileName += name;
                    sortedFilePath += name;
                });

                let unsortedFilePath = files[fileName][0].path;

                // STEP 3: Relocate file to new, correct file path
                this.renameFile(unsortedFilePath, sortedFilePath, sortedFilePathParent);

                Logger.logInfo(`File {${fileName}} (Folder = ${folderToPutIn}) successfully saved on server`,
                    'postPicture', this.clazzName);
                resolve();
            } catch (err: any) {
                reject(err);
            }
        });
    }

    saveFolder(folder: IFolder): Promise<void> {
        return new Promise((resolve, reject) => (fs.mkdir(
            BACKUP_PATH + '/' + folder.name, (err) => {
                if (err) {
                    reject(err);
                }
                resolve();
            })));
    }

    /**
     * Returns the fs Stats of a folder / file
     * @param absolutePath
     * @returns {Stats}
     */
    getStats(absolutePath: string): Stats {
        return fs.statSync(absolutePath);
    }

    /**
     * Returns an array of IPicturesVideo which lie beneath a folder
     * @param folder
     * @returns {IPicture[]}
     */
    getFolderPictureVideos(folder: IFolder): IPictureVideo[] {
        let pictures: IPictureVideo[] = [];
        let folderData = fs.readdirSync(folder.absolutePath);

        for (var i = 0; i !== folderData.length; i++) {
            const fileName = folderData[i];
            const filePath = folder.absolutePath + "/" + fileName;
            const fileData = this.getStats(filePath);

            if (!fileData.isDirectory()) {
                if (isPicture(fileName) || isVideo(fileName)) {
                    var picture = <IPictureVideo>{};
                    picture.absolutePath = filePath;
                    picture.name = fileName;
                    picture.size = fileData["size"];

                    pictures.push(picture);
                }
            }
        }
        return pictures;
    }

    /**
     * Returns the amount of childs of a folder
     * @param folder
     * @returns {number}
     */
    getFolderChildAmount(folder: IFolder): number {
        return fs.readdirSync(folder.absolutePath).length;
    }

    /**
     * Returns the size of a folder
     * Attention: Directories inside the folder are ignored! Only files are counted
     * @param folder
     * @returns {number}
     */
    getFolderSize(folder: IFolder): number {
        let folderSize = 0;
        const folderPictures = this.getFolderPictureVideos(folder);
        for (let i = 0; i !== folderPictures.length; i++) {
            folderSize += folderPictures[i].size;
        }
        return folderSize;
    }


    renameFile(oldPath: string, newPath: string, newPathWithoutFile: string) {
        this.assertFolderExistsOtherwiseCreate(newPathWithoutFile);
        // return fs.renameSync(oldPath, newPath);

        var is = fs.createReadStream(oldPath);
        var os = fs.createWriteStream(newPath);

        is.pipe(os);
        is.on('end', function () {
            fs.unlinkSync(oldPath);
        });
        return;
    }

    removeDir(path: string) {
        return fs.rmdirSync(path);
    }

    assertFolderExistsOtherwiseCreate(path: string) {
        try {
            let stats: Stats = this.getStats(path);
            if (!stats.isDirectory()) {
                throw new Error(path + ' is not a directory!');
            }
        } catch (err) {
            try {
                fs.mkdirSync(path);
            } catch (err2) {
                Logger.logError(err2, 'assertFolderExistsOtherwiseCreate (mkdirSync err)',
                    this.clazzName);
            }
        }
    }

    // getFolderSizeAsync(folder: IFolder): Promise<number> {
    //     return new Promise((resolve, reject) => (fs.stat(folder.absolutePath,
    //         (err, stats) => {
    //             if (err) {
    //                 console.log(err);
    //                 reject();
    //             }
    //             resolve(stats["size"]);
    //         })));
    // }
}