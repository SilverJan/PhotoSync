import {readFileSync} from 'fs';
const fs = require('fs');

// ----------------------------------------------------------
// h t t p s
// ----------------------------------------------------------
export const systems = {
    windows: 'windows',
    linux: 'linux'
};

// Edit this line if productive!
export const systemRec = process.env.LOCAL_SYSTEM;

// TODO: Make this dynamic like in gulpfile
export const host: string = systemRec === systems.linux ? '192.168.178.47' :
    '192.168.178.46';
export const port: number = 8443;

export const httpsKey: Buffer = readFileSync('../config/https/key.pem');
export const httpsCert: Buffer = readFileSync('../config/https/cert.pem');

// ----------------------------------------------------------
// M I M E
// ----------------------------------------------------------
export const contentType: string = 'content-type';
export const applicationJson: string = 'application/json';
export const multipartFormdata: string = 'multipart/form-data';

// ----------------------------------------------------------
// M U L T I P A R T Y
// ----------------------------------------------------------
const propDir: string = '../config/photosync.properties';
let readPropValue: string = readFileSync(propDir).toString();

// fs.watch(propDir, (event, filename) => {
//     console.log(`${filename} was updated. Event is: ${event}`);
//     readPropValue = readFileSync(propDir).toString();
// });
export const UNSORTED_PATH_PIC_UPLOAD: string = readPropValue.split(';')[0].split('=')[1];
export const BACKUP_PATH: string = readPropValue.split(';')[1].split('=')[1];