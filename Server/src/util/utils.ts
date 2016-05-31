import {Request, Response, NextFunction} from 'express';
import {systemRec, systems} from './constants';

/**
 * Validates an chat ID
 * @param req
 * @param res
 * @param next The next function to be called in the stack
 * @param folderName The chat ID
 */
export function isFolderName(req: Request, res: Response, next: NextFunction,
                             folderName: string) {
    // UUID RegEx
    let patt = new RegExp('^.]$');
    if (folderName === null || folderName === undefined) {
        res.status(400).send(folderName + ' is no valid folderName');
    } else {
        next();
    }
}

/**
 * Returns true if a fileName ends with jpg, jpeg or png
 * @param fileName
 * @returns {boolean}
 */
export function isPicture(fileName: string) {
    if (fileName.endsWith('.jpg') || fileName.endsWith('.JPG')) {
        return true;
    }
    if (fileName.endsWith('.jpeg') || fileName.endsWith('.JPEG')) {
        return true;
    }
    if (fileName.endsWith('.png') || fileName.endsWith('.PNG')) {
        return true;
    }
    return false;
}

/* tslint:disable:max-line-length */
export function addSecurityHeader(req: Request, res: Response, next: NextFunction): void {
    // HSTS = HTTP Strict Transport Security
    // https://www.owasp.org/index.php/HTTP_Strict_Transport_Security
    res.setHeader(
        'Strict-Transport-Security', 'max-age=31536000; includeSubDomains');

    // CORS = Cross Origin Resource Sharing
    // http://www.html5rocks.com/en/tutorials/cors
    let uri = systemRec === systems.linux ? '192.168.178.47' : '192.168.178.46';
    res.setHeader('Access-Control-Allow-Origin', `https://${uri}:8000`);
    res.setHeader('Access-Control-Allow-Credentials', 'true');
    res.setHeader(
        'Access-Control-Allow-Methods',
        'OPTIONS, GET, POST, PUT, DELETE, HEAD');
    res.setHeader(
        'Access-Control-Allow-Headers',
        'origin, content-type, accept, authorization, access-control-allow-origin, access-control-allow-methods, access-control-allow-headers, allow, content-length, date, last-modified, if-modified-since');
    res.setHeader('Access-Control-Max-Age', '1728000');

    // CSP = Content Security Policy
    // http://www.html5rocks.com/en/tutorials/security/content-security-policy
    res.setHeader(
        'Content-Security-Policy',
        'default-src https:; script-src https: \'self\'; img-src https: \'self\'; media-src https: \'self\'');

    // XSS = Cross Site Scripting
    // https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet
    res.setHeader('X-XSS-Protection', '1; mode=block');

    // Clickjacking
    // http://tools.ietf.org/html/draft-ietf-websec-x-frame-options-01
    // https://www.owasp.org/index.php/Clickjacking
    res.setHeader('X-Frame-Options', 'deny');

    // MIME-sniffing
    // https://blogs.msdn.microsoft.com/ie/2008/09/02/ie8-security-part-vi-beta-2-update
    res.setHeader('X-Content-Type-Options', 'nosniff');

    res.setHeader(
        'Cache-Control',
        'private,no-cache,no-store,max-age=0,no-transform');
    res.setHeader('Expires', '-1');
    res.setHeader('Pragma', 'no-cache');

    res.setHeader('X-Provided-By', 'Jan Bissinger');

    next();
}
/* tslint:enable:max-line-length */

/**
 * Returns an generated UUID
 * @returns {string}
 */
export function uuid(): string {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
        s4() + '-' + s4() + s4() + s4();
}

export function isStringNullOrEmpty(str: string): boolean {
    return typeof str === 'undefined' || str.length === 0;
}