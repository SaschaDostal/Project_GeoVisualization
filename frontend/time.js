function setTime(){
    var day = document.getElementById("selectDay");
    var hour = document.getElementById("selectHour");
    var min = document.getElementById("selectMin");
    
    if(hour.value == "00"){
        document.getElementById("rain_img").src="resources/rainy.png";
        document.getElementById("rain_div").innerHTML = "<b>&nbsp;Niederschlag in den letzten 10 Min.: 0.12 mm</b>";
        idwPM10Layer.data=idwData2;
        //map.removeLayer(L.geoJson);
    } else {
        document.getElementById("rain_img").src="resources/sunny.png";
        document.getElementById("rain_div").innerHTML = "<b>&nbsp;Niederschlag in den letzten 10 Min.: 0.00 mm</b>";
    }

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
    
    alert("Update für Zeit: " + day.value + " - " + hour.value + ":" + min.value);

    // Icons made by berkahicon from https://www.flaticon.com/
}