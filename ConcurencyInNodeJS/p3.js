
function printAsync(s, cb) {
   var delay = Math.floor((Math.random()*1000)+500);
   setTimeout(function() {
       console.log(s);
       if (cb) cb();
   }, delay);
}

function task1(cb,a){
    printAsync("1", function() {
        task2(cb,a);
    });
}

function task2(cb,a) {
    printAsync("2", function() {
        task3(cb,a);
    });
}

function task3(cb,a) {
	if(a>1){
		printAsync("3", function() {
			task1(cb,a-1);
		}
		);
	}else{
		printAsync("3", cb);
	}
    
}

// wywolanie sekwencji zadan
task1(function() {
    console.log('done!');
},6);


/* 
** Zadanie:
** Napisz funkcje loop(n), ktora powoduje wykonanie powyzszej 
** sekwencji zadan n razy.
** 
*/

// loop(4);