// Teoria Współbieżnośi, implementacja problemu 5 filozofów w node.js
// Opis problemu: http://en.wikipedia.org/wiki/Dining_philosophers_problem
//   https://pl.wikipedia.org/wiki/Problem_ucztuj%C4%85cych_filozof%C3%B3w
// 1. Dokończ implementację funkcji podnoszenia widelca (Fork.acquire).
// 2. Zaimplementuj "naiwny" algorytm (każdy filozof podnosi najpierw lewy, potem
//    prawy widelec, itd.).
// 3. Zaimplementuj rozwiązanie asymetryczne: filozofowie z nieparzystym numerem
//    najpierw podnoszą widelec lewy, z parzystym -- prawy.
// 4. Zaimplementuj rozwiązanie z kelnerem (według polskiej wersji strony)
// 5. Zaimplementuj rozwiążanie z jednoczesnym podnoszeniem widelców:
//    filozof albo podnosi jednocześnie oba widelce, albo żadnego.
// 6. Uruchom eksperymenty dla różnej liczby filozofów i dla każdego wariantu
//    implementacji zmierz średni czas oczekiwania każdego filozofa na dostęp
//    do widelców. Wyniki przedstaw na wykresach.
var async = require('async');
var sleep = require('system-sleep');
var Fork = function() {
    this.state = 0;
    return this;
};

Fork.prototype.acquire = function(cb) {
    //zaimplementuj funkcję acquire, tak by korzystala z algorytmu BEB
    // (http://pl.wikipedia.org/wiki/Binary_Exponential_Backoff), tzn:
    // 1. przed pierwszą próbą podniesienia widelca Filozof odczekuje 1ms
    // 2. gdy próba jest nieudana, zwiększa czas oczekiwania dwukrotnie
    //    i ponawia próbę, itd.
    var binaryBackoff = function(fork, time) {
        if(fork.state === 1) {
            if(time <= 10){
                setTimeout(function() { binaryBackoff(fork, time ++); }, Math.floor(Math.random() * (2^(time))) + 2);
            }else{
                setTimeout(function() { binaryBackoff(fork, time ); }, Math.floor(Math.random() * (2^(time))) + 2);
            }
        } else {
            fork.state = 1;
            cb();
        }
    }
    // setTimeout() accepts a function to execute as its first argument and the millisecond delay defined as a number as the second argument.>
    setTimeout(binaryBackoff, 1, this, 1)
}

Fork.prototype.release = function() {
    this.state = 0;
};

var Philosopher = function(id, forks) {
    this.id = id;
    this.forks = forks;
    this.f1 = id % forks.length;
    this.f2 = (id+1) % forks.length;
    return this;
};

//naive
Philosopher.prototype.startNaive = function(count) {
    var forks = this.forks,
        f1 = forks[this.f1],
        f2 = forks[this.f2],
        id = this.id;
    // zaimplementuj rozwiązanie naiwne
    // każdy filozof powinien 'count' razy wykonywać cykl
    // podnoszenia widelców -- jedzenia -- zwalniania widelców
    toTrigger = []
    var fu = function(count,callback) {
        f1.acquire(function () {
            console.log("zajałem pierwszy widelec")
            f2.acquire(
                function () {
                    console.log("zajałem drugi widelec")
                    var hrTime = process.hrtime();
                    var eatTime = hrTime[0] * 1000000 + hrTime[1] / 1000;
                    console.log("Philosopher " + id + ": eats " + count + " times at: " + eatTime / 1000);
                    f2.release();
                    f1.release();
                    setTimeout(callback,Math.random()*100,null,count+1);
                   // callback(null,count+1);
                }
            )
        })
    };
    toTrigger.push(function (c) {
        c(null,0); //zaczynamy przegladac bez bledu z indexem 0 i result 0
    });
    for(var i=0; i<count;i++){
        toTrigger.push(fu);
    }
    async.waterfall(toTrigger);
};
//asym
Philosopher.prototype.startAsym = function(count) {
    var forks = this.forks,
        f1 = forks[this.f1],
        f2 = forks[this.f2],
        id = this.id;
        // zaimplementuj rozwiązanie asymetryczne
        // każdy filozof powinien 'count' razy wykonywać cykl
        // podnoszenia widelców -- jedzenia -- zwalniania widelców
        // parzysty podnosi najpierw lewy potem prawy
        //nieparzysty podnosi najpierw prawy potem lewy
    var fu;
    if(this.id%2 === 0){
        fu = function(count,callback) {// console.log("xDD")
            f1.acquire(function () {
                console.log("zajałem pierwszy widelec")
                f2.acquire(
                    function () {
                        console.log("zajałem drugi widelec")
                        var hrTime = process.hrtime();
                        var eatTime = hrTime[0] * 1000000 + hrTime[1] / 1000;
                        console.log("Philosopher " + id + ": eats " + count + " times at: " + eatTime / 1000);
                        f2.release();
                        f1.release();
                        setTimeout(callback,Math.random()*100,null,count+1); //callback(null,count+1);
                    }
                )
            })
        };
    }else{
        fu = function(count,callback) {// console.log("xDD")
            f2.acquire(function () {
                console.log("zajałem pierwszy widelec")
                f1.acquire(
                    function () {
                        console.log("zajałem drugi widelec")
                        var hrTime = process.hrtime();
                        var eatTime = hrTime[0] * 1000000 + hrTime[1] / 1000;
                        console.log("Philosopher " + id + ": eats " + count + " times at: " + eatTime / 1000);
                        f1.release();
                        f2.release();
                        setTimeout(callback,Math.random()*100,null,count+1) //callback(null,count+1);
                    }
                )
            })
        };
    }
    toTrigger = []
    toTrigger.push(function (c) {
        c(null,0); //zaczynamy przegladac bez bledu z indexem 0 i result 0
    });

    for(var i=0; i<count;i++){
        toTrigger.push(fu);
    }
    async.waterfall(toTrigger);

}

//kelner

var Conductor = function(id, forks) {
    this.forks = forks;
    this.busy = false;
    return this;
};

Conductor.prototype.release = function() {
    this.busy = false;
}


Conductor.prototype.acquire = function(cb) {
    var binaryBackoff = function(cb, conductor, time) {
        if(conductor.busy) {
            console.log("Kelner zajety")
            setTimeout(function() { return binaryBackoff(cb, conductor, time * 2); }, time);
        } else {
            conductor.busy = true;
            cb();
        }
    }
    binaryBackoff(cb, this, 1);
}



Philosopher.prototype.startConductor = function(count, conductor) {
    var forks = this.forks,
        f1 = forks[this.f1],
        f2 = forks[this.f2],
        id = this.id;
        conductor = conductor;
        var ate = 0;

    var eat = function(cb) {
        conductor.acquire(function() {
            f1.acquire(function() {
                f2.acquire(function() {
                    setTimeout(function () {
                        var hrTime = process.hrtime();
                        var eatTime = hrTime[0] * 1000000 + hrTime[1] / 1000;
                        ate ++;
                        console.log("Philosopher " + id + ": eats " + ate + " times at: " + eatTime / 1000);
                        conductor.release();
                        f1.release();
                        f2.release();
                        cb();
                    }, 100 * Math.random())
                })
            })
        })
    }

    var toTrigger = []
    for(var i = 0; i < count; i++)
        toTrigger.push(eat)

    async.waterfall(toTrigger);
}
Conductor.prototype.releaseForks = function(f1,f2){
    //console.log("oddaje")
    f1.release();
    f2.release();
};
Conductor.prototype.getForks = function(f1,f2){//console.log("daje widelce")
    if(f1.state === 0 && f2.state === 0){
       // console.log("daje widelce")
        f1.state = 1
        f2.state = 1
        return true;
    }else{
       // console.log("nie daje widelcow")
        return false;
    }
};
// Algorytm BEB powinien obejmować podnoszenie obu widelców,
// a nie każdego z osobna
Philosopher.prototype.startLiftBoth = function(count) {
    var forks = this.forks,
        f1 = forks[this.f1],
        f2 = forks[this.f2],
        id = this.id;
        // zaimplementuj rozwiązanie z kelnerem
        // każdy filozof powinien 'count' razy wykonywać cykl
        // podnoszenia widelców -- jedzenia -- zwalniania widelców
        toTrigger = []
        var fu = function(count,time,callback) {
            if(Conductor.prototype.getForks(f1,f2)){
                var hrTime = process.hrtime();
                var eatTime = hrTime[0] * 1000000 + hrTime[1] / 1000;
                console.log("Philosopher " + id + ": i get forks and eat " + count + " times at: " + eatTime / 1000);
                Conductor.prototype.releaseForks(f1,f2);
                setTimeout(callback,Math.random()*100,null,count+1,1);
            }else{//nie udalo sie probujemy jeszcze raz
                if(time<10){
                    setTimeout(function() { fu(count,time+1,callback); }, Math.floor(Math.random() * (2^(time))) + 2);
                }else{
                    setTimeout(function() { fu(count,time,callback); }, Math.floor(Math.random() * (2^(time))) + 2);
                }
            }
        };
        toTrigger.push(function (c) {
            c(null,0,1); //zaczynamy przegladac bez bledu z indexem 0 i result 0
        });
        for(var i=0; i<count;i++){
            toTrigger.push(fu);
        }
        sleep(1);
        async.waterfall(toTrigger);
}




var N = 5;
var forks = [];
var philosophers = [];
for (var i = 0; i < N; i++) {
    forks.push(new Fork());
}

for (var i = 0; i < N; i++) {
    philosophers.push(new Philosopher(i, forks));
}

//nowy kelner do obu i pojedynczych
var cond = new Conductor(1,forks);
//console.log(cond.free)
for (var i = 0; i < N; i++) {
    //philosophers[i].startNaive(5);
    //philosophers[i].startAsym(5);
    //philosophers[i].startConductor(5,cond);
    philosophers[i].startLiftBoth(5);
}