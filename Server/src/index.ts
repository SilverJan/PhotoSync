/**
 * Created by Jan on 14.04.2016.
 */

import * as express from 'express';
import {Express} from 'express';
import {createServer} from 'https';
import {host, port, httpsCert, httpsKey} from './util/constants';
import folderRouter from './folder_component/index';
import {addSecurityHeader} from './util/utils';

class Server {
    private _app: Express = this._initApp();

    constructor(private _host: string, private _port: number, private _httpsKey: Buffer,
                private _httpsCert: Buffer) {
    }

    start(): void {
        createServer({key: this._httpsKey, cert: this._httpsCert}, this._app)
            .listen(this._port, this._host, () => {
                console.log(
                    `Der Server ist gestartet: https://${this._host}:${this._port}`);
            });
    }

    toString(): string {
        return 'Server';
    }

    private _initApp(): Express {
        const app: Express = express();

        app.use(addSecurityHeader)
            .use('/test', function (req, res) {
                res.send(`Please change the URI to: https://${host}:${port}/chat`);
            })
            .use('/folders', folderRouter);

        return app;
    }
}

new Server(host, process.env.PORT || port, httpsKey, httpsCert).start();