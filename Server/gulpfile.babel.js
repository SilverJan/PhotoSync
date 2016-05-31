// TODO: import instead of require
var del = require('del');
var bundle = require('gulp-bundle-assets');
var gulp = require('gulp');
var gulpTslint = require('gulp-tslint');
var gulpTypescript = require('gulp-typescript');
var rename = require('gulp-rename');
var typescript = require('typescript');
var shelljs = require('shelljs');
var tscConfig = require('./tsconfig.json');
var yargs = require('yargs');
var uglify = require('gulp-uglify');

// ----------------------------------------------------------
// P A R A M E T E R S
// ----------------------------------------------------------
var argv = yargs.argv.prod;
var prod = (argv === 'true') ? true : false;

// ----------------------------------------------------------
// C O N F I G
// ----------------------------------------------------------

/* RUN CONFIG */
const systems = {
    windows: 'windows',
    linux: 'linux'
};

const systemRec = systems.windows;

/* GENERAL PATHS */

const srcPath = './src';
const buildPath = './build';
const srcPaths = {
    tsFiles: `${srcPath}/**/*.ts`
};

// ----------------------------------------------------------
// P R E - S T E P S
// ----------------------------------------------------------

gulp.task('clean', function () {
    'use strict';
    return removeDirectory(buildPath);
});

gulp.task('tslint', function () {
    'use strict';
    return gulp.src(srcPaths.tsFiles)
        .pipe(gulpTslint())
        .pipe(gulpTslint.report('verbose'));
});

gulp.task('tsc', function () {
    'use strict';

    return gulp.src(srcPaths.tsFiles)
        .pipe(gulpTypescript(tscConfig.compilerOptions))
        .pipe(gulp.dest(buildPath));
});

gulp.task('ts', gulp.series('tslint', 'tsc'));


gulp.task('build', gulp.series('clean', 'ts'));


gulp.task('watch', function () {
    'use strict';
    gulp.watch(srcPaths.tsFiles, gulp.series('tsc'));
});

// ----------------------------------------------------------
// R U N
// ----------------------------------------------------------

gulp.task('nodemon', function () {
    shelljs.exec(`cd ${buildPath} && node ../node_modules/nodemon/bin/nodemon.js`);
});


// Run 'watch' in another task
gulp.task('run', gulp.series('ts', 'nodemon'));

gulp.task('stopNode', function() {
    'use strict';
    if (systemRec === systems.linux) {
        shelljs.exec('killall node');
    } else if (systemRec === systems.windows) {
        shelljs.exec(`taskkill /IM node.exe /F`);
    }
});

// ----------------------------------------------------------
// U T I L S
// ----------------------------------------------------------

function removeDirectory(dir) {
    'use strict';
    return del([dir]);
}

function getLocalIp() {
    'use strict';
    let ip = '';
    if (systemRec === systems.linux) {
        ip = shelljs.exec("ifconfig | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1'", {silent: true}).stdout;
        ip = ip.replace(/(\r\n|\n|\r)/gm,''); // remove line breaks
        ip = ip.split(':')[1];
        ip = ip.split('i')[0];
    } else if (systemRec === systems.windows) {
        ip = shelljs.exec(`netsh interface ip show addresses "WiFi" | findstr /R /C:"IP.*"`, {silent: true}).stdout;
        ip = ip.trim().substring(11).trim();
    }
    return ip;
}

gulp.task('ip', function () {
    console.log(getLocalIp());
});