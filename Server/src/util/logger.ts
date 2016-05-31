var moment = require('moment');

export class Logger {
    public static logError(err:any, methodName:string, clazz:string): void {
        console.log(`BE LOG ${this.getTimeNow()} > Error while [[${clazz}.${methodName}]: ${err}`);
    }

    public static logInfo(info:any, methodName:string, clazz:string): void {
        console.log(`BE LOG ${this.getTimeNow()} > Info while [${clazz}.${methodName}]: ${info}`);
    }

    private static getTimeNow(): string {
        return moment().format();
    }
}