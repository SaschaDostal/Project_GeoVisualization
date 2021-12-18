function setTime(){
    var day = document.getElementById("selectDay");
    var hour = document.getElementById("selectHour");
    var min = document.getElementById("selectMin");

    var tstamp = day.value.slice(6) + day.value.slice(3, 5)
        + day.value.slice(0, 2) + hour.value + min.value;
    updatePrecipitation(tstamp);
    updateWind(tstamp);

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
        })
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

async function updateWind(tstamp) {
    let i = await fetch("http://localhost:8080/windVector?tstamp=" + tstamp);
    let j = await i.text();
    let z = JSON.parse(j)

    var x_component = z.x;
    var y_component = z.y;

    var winddata = [{
        "header": { "discipline": 0,
        "parameterCategory": 2,
        "parameterNumber": 2,
        "parameterNumberName": "U-component_of_wind",
        "parameterUnit": "m.s-1",
        "genProcessType": 2,
        "forecastTime": 0,
        "surface1Type": 103,
        "surface1Value": 10.0,
        "surface2Value": 0.0,
        "gridDefinitionTemplate": 0,
        "numberPoints": 2,
        "resolution": 48,
        "winds": "true",
        "scanMode": 0,
        "nx": 2,
        "ny": 2,
        "basicAngle": 0,
        "subDivisions": 0,
        "lo1": 8.6346,
        "la1": 48.9304,
        "lo2": 9.6849,
        "la2": 48.6239,
        "dx": 1.0,
        "dy": 1.0 },
        "data": [x_component, x_component, x_component, x_component]
    }, {
        "header": { "discipline": 0,
        "parameterCategory": 2,
        "parameterNumber": 3,
        "parameterNumberName": "V-component_of_wind",
        "parameterUnit": "m.s-1",
        "genProcessType": 2,
        "forecastTime": 0,
        "surface1Type": 103,
        "surface1Value": 10.0,
        "surface2Type": 255,
        "surface2Value": 0.0,
        "gridDefinitionTemplate": 0,
        "numberPoints": 2,
        "shape": 6,
        "resolution": 48,
        "winds": "true",
        "scanMode": 0,
        "nx": 2,
        "ny": 2,
        "basicAngle": 0,
        "subDivisions": 0,
        "lo1": 8.6346,
        "la1": 48.9304,
        "lo2": 9.6849,
        "la2": 48.6239,
        "dx": 1.0,
        "dy": 1.0 },
        "data": [y_component, y_component, y_component, y_component]
    }];
    map.removeLayer(velocityLayer);
    layerControl.removeLayer(velocityLayer);
    velocityLayer = L.velocityLayer({
        displayValues: true,
        displayOptions: {
            velocityType: "Wind",
            position: "bottomleft",
            emptyString: "Keine Winddaten verfügbar."
        },
        data: winddata,
        maxVelocity: 15
    }).addTo(map);
    layerControl.addOverlay(velocityLayer, "Wind");
}
updateWind("202101010000");