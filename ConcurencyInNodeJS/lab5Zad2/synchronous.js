//it shall run one after another
 fs = require("fs");
 file = require("file");
 async = require('async');

//chcemy wczytac wszystkie pliki
function getAllDirectories(pathToDir,callback){
    console.time("Synchronous time");
    var Directories = [];
    fs.readdir(pathToDir, function (err,fileNames) {
        fileNames.forEach(function(f) {Directories.push(pathToDir + '/' + f)});
        callback(Directories);//wywolanie funkcji na kazdym z tych podkatalogow zakladamy ze tam mamy same pliki
    });
    console.timeEnd("Synchronous time");
}

//ona ma przygotowac tablice wywołań
function countSynchronously(directories){
    //pierwsza funkcja do wywolania
    var toTrigger = [];
    toTrigger.push(function (c) {
        c(null,0,0); //zaczynamy przegladac bez bledu z indexem 0 i result 0
    });
    //bierzemy kazdy podkatalog
    for(var x = 0; x < directories.length;x++){
        toTrigger.push(
            function (index,result,callback) {
                var count = 0;
                //przechodzimy przez wszystkie pliki w katalogu
                fs.readdir(directories[index], function(err,fileNames){//mamy err i wszystkie pliki w danym katalogu
                    if(err){return;}
                    //robimy wszystko
                    var inSubDir = 0;
                    fileNames.forEach(function(fileName){
                        fs.createReadStream(directories[index] + "/" + fileName).on('data', function(chunk) {
                            count += chunk.toString('utf8')
                                .split(/\r\n|[\n\r\u0085\u2028\u2029]/g)
                                .length-1;
                        }).on('end', function() {
                            //console.log(fileName, count);
                            inSubDir ++;
                            //jezeli zrobilismy ostatni plik w danym podkatalogu to callback
                            if(inSubDir === fileNames.length){
                               callback(null,index + 1,result+count)
                            }
                        }).on('error', function(err) {
                            console.error(err);
                        });
                    })
                })
            });
    }
    //mamy wypelnione teraz trzeb wykonywac
    async.waterfall(toTrigger, function(err, index, result){
            if(err){throw err;}
            console.log("Synchronous sum: " + result);
        }
    );
}
getAllDirectories('./PAM08',function (a) {countSynchronously(a);});