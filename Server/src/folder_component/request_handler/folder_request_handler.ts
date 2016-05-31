import {Request, Response} from 'express';
import FolderService from '../service/folder_service';
import {
    contentType,
    applicationJson,
    multipartFormdata,
    UNSORTED_PATH_PIC_UPLOAD
} from '../../util/constants';
import {IFolder} from '../model/folder';
import {Logger} from '../../util/logger';
var multiparty = require('multiparty');

class FolderRouter {
    private clazzName = 'FolderRouter';
    private _folderService: FolderService = new FolderService();

    // getById(req: Request, res: Response): void {
    //     const id: string = req.params.id;
    //
    //     this._folderService
    //         .findById(id)
    //         .then((message: IMessage) => {
    //             res.json(message);
    //         })
    //         .catch((err: any) => {
    //             res.sendStatus(500);
    //         });
    // }

    getByName(req: Request, res: Response): void {
        const name: string = req.params.name;

        this._folderService
            .findByName(name)
            .then((folder: IFolder) => {
                Logger.logInfo(JSON.stringify(folder), 'getByName', this.clazzName);
                res.json(folder);
            })
            .catch((err: any) => {
                Logger.logError(err, 'getByName', this.clazzName);
                res.sendStatus(500);
            });
    }

    getByQuery(req: Request, res: Response): void {
        // z.B. http://.../buecher?titel=Alpha
        const query: any = req.query;

        this._folderService
            .find()
            .then((folders: Array<IFolder>) => {
                Logger.logInfo(JSON.stringify(folders), 'getByQuery', this.clazzName);
                res.json(folders);
            })
            .catch((err: any) => {
                Logger.logError(err, 'getByQuery', this.clazzName);
                res.sendStatus(500);
            });
    }

    postFolder(req: Request, res: Response): void {
        if (req.header(contentType) === undefined
            || req.header(contentType).toLowerCase() !== applicationJson) {
            res.sendStatus(406);
            return;
        }

        // TODO: Add check on req.body

        var folder: IFolder = req.body;

        this._folderService
            .saveFolder(folder)
            .then(() => {
                Logger.logInfo(`Created new folder: ${folder.name}`, 'postFolder',
                    this.clazzName);
                res.json(folder);
                res.sendStatus(201);
            })
            .catch((err: any) => {
                if (err.code === "EEXIST") {
                    Logger.logInfo('Dir already exists, but is ok (200): ' + err,
                        'postFolder', this.clazzName);
                    // res.json(folder);
                    res.sendStatus(200);
                } else {
                    Logger.logError(err.stack, 'postFolder', this.clazzName);
                    res.sendStatus(500);
                }
            });
    }

    // postPicture(req: Request, res: Response): void {
    //     if (req.header(contentType) === undefined
    //         || !req.header(contentType).toLowerCase().startsWith(multipartFormdata)) {
    //         res.sendStatus(406);
    //         return;
    //     }
    //
    //
    //     let form = new multiparty.Form({uploadDir: UNSORTED_PATH_PIC_UPLOAD});
    //     form.on('error', (err) => {
    //         Logger.logError(err, 'postPicture (form.on err)', this.clazzName);
    //         res.sendStatus(500);
    //     });
    //
    //     form.on('part', (part) => {
    //         part.resume();
    //     });
    //
    //     // fields = { description: [ 'Download' ] }
    //     // files = { 'da6ad85bfd.jpg':
    //     // [ { fieldName: 'da6ad85bfd.jpg',
    //     //     originalFilename: 'da6ad85bfd.jpg',
    //     //     path: 'C:\\Users\\Jan\\Desktop\\Test\\unsorted\\X5LQres8kGHuzTb8BTUpKtNq.jpg',
    //     //     headers: [Object],
    //     //     size: 47987 } ] }
    //
    //     form.parse(req, (err, fields, files) => {
    //         if (err) {
    //             Logger.logError(err.stack, 'postPicture (form.parse err)', this.clazzName);
    //             res.sendStatus(500);
    //         }
    //
    //         this._folderService
    //             .saveFile(files, fields)
    //             .then(() => {
    //                 res.end();
    //                 // Remove unsorted folder after successful file upload
    //                 // this._folderService.removeDir(tempDir);
    //             })
    //             .catch((err: any) => {
    //                 Logger.logError(err.stack === undefined ? err.stack : err, 'postPicture (saveFile err)',
    //                     this.clazzName);
    //                 res.sendStatus(500);
    //             });
    //     });
    // }

    postPicture(req: Request, res: Response): void {
        if (req.header(contentType) === undefined
            || !req.header(contentType).toLowerCase().startsWith(multipartFormdata)) {
            res.sendStatus(406);
            return;
        }

        let form = new multiparty.Form({uploadDir: UNSORTED_PATH_PIC_UPLOAD});

        this._folderService.assertFolderExistsOtherwiseCreate(UNSORTED_PATH_PIC_UPLOAD);

        // form.on('error', (err) => {
        //     Logger.logError(err, 'postPicture (form.on err)', this.clazzName);
        //     res.sendStatus(500);
        // });

        form.on('part', (part) => {
            part.resume();
        });

        // fields = { description: [ 'Download' ] }
        // files = { 'da6ad85bfd.jpg':
        // [ { fieldName: 'da6ad85bfd.jpg',
        //     originalFilename: 'da6ad85bfd.jpg',
        //     path: 'C:\\Users\\Jan\\Desktop\\Test\\unsorted\\X5LQres8kGHuzTb8BTUpKtNq.jpg',
        //     headers: [Object],
        //     size: 47987 } ] }

        form.parse(req, (err, fields, files) => {
            if (err) {
                // TODO handle this
                // Logger.logError(err, 'postPicture (form.parse err)', this.clazzName);
                // res.sendStatus(500);
            }
            const folderToPutIn: string = req.get('foldertoputinheader');

            this._folderService
                .saveFile(files, fields, folderToPutIn)
                .then(() => {
                    res.end();
                    // Remove unsorted folder after successful file upload
                    // this._folderService.removeDir(UNSORTED_PATH_PIC_UPLOAD);
                })
                .catch((err: any) => {
                    Logger.logError(typeof err.stack !== 'undefined' ? err.stack : err,
                        'postPicture (saveFile err)',
                        this.clazzName);
                    res.sendStatus(500);
                });
        });
    }

    deleteMeth(req: Request, res: Response): void {
        // bla
    }

    toString(): string {
        return 'FolderRouter';
    }
}

// -----------------------------------------------------------------------
// E x p o r t i e r t e   F u n c t i o n s
// -----------------------------------------------------------------------
const folderRouter: FolderRouter = new FolderRouter();
// export function getById(req: Request, res: Response): void {
//     'use strict';
//     chatRouter.getById(req, res);
// }

export function getByName(req: Request, res: Response): void {
    'use strict';
    folderRouter.getByName(req, res);
}

export function getByQuery(req: Request, res: Response): void {
    'use strict';
    folderRouter.getByQuery(req, res);
}

export function postFolder(req: Request, res: Response): void {
    'use strict';
    folderRouter.postFolder(req, res);
}

export function postPicture(req: Request, res: Response): void {
    'use strict';
    folderRouter.postPicture(req, res);
}

export function deleteFn(req: Request, res: Response): void {
    'use strict';
    folderRouter.deleteMeth(req, res);
}
