window.changeVid=function(myTitle, myFile) {
	console.log("Entered function");
    var header = document.getElementById("header");
    header.innerHTML = myTitle;
    document.querySelector("#playing > source").src = myFile;
    document.getElementById("playing").load();
    console.log("Exiting function");
}