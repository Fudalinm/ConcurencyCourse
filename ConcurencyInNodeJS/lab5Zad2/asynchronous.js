//it shell run file reader simultaneously
var fs = require('fs');
var sleep = require('system-sleep');
var startTime = 0;
var endTime = 0;
//tu sie de facto zaczyna nasza funkcja asynchroniczna
var count2 = 0;
function countLinesInFile(file){
    var count = 0;
    fs.createReadStream(file).on('data', function(chunk) {
        count += chunk.toString('utf8')
            .split(/\r\n|[\n\r\u0085\u2028\u2029]/g)
            .length-1;
        count2 += count;
    }).on('end', function() {
        console.log(file, count);
    }).on('error', function(err) {
        console.error(err);
    });
}
var sy = function(starteroni){
    fs.readdir(starteroni, function (err, files) {
        if (err)
            throw err;
        for (var index in files) {
            var currentFile = starteroni + '/' + files[index];
            var stats = fs.statSync(currentFile);
            if(stats.isDirectory()){
                //to ma nie dzialac blokujaco
                sy(currentFile);
            }else if(stats.isFile()){
                //to ma nie dzialac blokujaco
                countLinesInFile(currentFile);
            }
        }
    });
};
var msy = function(starteroni){
    hrTime = process.hrtime();
    startTime = hrTime[0] * 1000000 + hrTime[1] / 1000;
    sy(starteroni);
    hrTime = process.hrtime();
    endTime = hrTime[0] * 1000000 + hrTime[1] / 1000;
};
msy('./PAM08');
sleep(1000);
console.log("Program zakonczono po: " + (endTime-startTime)/1000 + " ms");
console.log(count2);