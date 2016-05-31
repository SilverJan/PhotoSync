import {Router} from 'express';
import {
    getByQuery, postFolder, getByName,
    postPicture
} from './request_handler/folder_request_handler';
import {json} from 'body-parser';
import {isFolderName} from '../util/utils';

const folderRouter: Router = Router();
folderRouter.route('/')
    .get(getByQuery)
    .post(json(), postFolder);

folderRouter.param('folder', isFolderName)
    .get('/:folder', getByName)
    .post('/:folder', postPicture);

// folderRouter.param('id', isChatId)
//     .get('/:id', getById);

export default folderRouter;