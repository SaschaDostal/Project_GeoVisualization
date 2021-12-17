function setTime(){
    var day = document.getElementById("selectDay");
    var hour = document.getElementById("selectHour");
    var min = document.getElementById("selectMin");

    var tstamp = day.value.slice(6) + day.value.slice(3, 5)
        + day.value.slice(0, 2) + hour.value + min.value;
    updatePrecipitation(tstamp)

    // _____________________redraw PM10 Layer_____________________
    map.removeLayer(idwPM10Layer);
    layerControl.removeLayer(idwPM10Layer);
    idwPM10Layer = L.idwLayer(idwData2,
        {
            opacity: 0.25, cellSize: 20, exp: 2, max: 1200, gradient: {
                0.0: '#ffffff',
                0.1: '#fff5eb',
                0.2: '#fee6ce',
                0.3: '#fdd0a2',
                0.4: '#fdae6b',
                0.5: '#fd8d3c',
                0.6: '#f16913',
                0.7: '#d94801',
                0.8: '#a63603',
                0.9: '#7f2704',
                1.0: '#7f2704'
            }
        }).addTo(map);
    layerControl.addOverlay(idwPM10Layer, "Feinstaub - PM 10");

    // Icons made by berkahicon from https://www.flaticon.com/
}

async function updatePrecipitation(tstamp) {
    let x = await fetch("http://localhost:8080/precipitation?tstamp=" + tstamp);
    let y = await x.text();
    let z = JSON.parse(y)

    document.getElementById("rain_div").innerHTML = "<b>&nbsp;Niederschlag in den letzten 10 Min.: " + z.precipitation[0].mm.toFixed(2) 
        + " mm</b><br><b>&nbsp;Regendauer in den letzten 10 Min.: " + z.precipitation[0].duration + " Min.</b>";
    if(z.precipitation[0].raining == "1"){
        document.getElementById("rain_img").src="resources/rainy.png";
    } else {
        document.getElementById("rain_img").src="resources/sunny.png";
    }
}
updatePrecipitation("202101010000");