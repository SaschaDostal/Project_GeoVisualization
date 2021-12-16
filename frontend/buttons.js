function button1pressed(){
    alert("Button 1 pressed")
}

function button2pressed(){
    alert("Button 2 pressed")
}

function setTime(){
    var day = document.getElementById("selectDay");
    var hour = document.getElementById("selectHour");
    var min = document.getElementById("selectMin");
    alert("" + day.value + " - " + hour.value + ":" + min.value);
}