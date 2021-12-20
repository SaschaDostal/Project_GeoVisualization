function setTime() {
    var day = document.getElementById("selectDay");
    var hour = document.getElementById("selectHour");
    var min = document.getElementById("selectMin");

    var tstamp = day.value.slice(6) + day.value.slice(3, 5)
        + day.value.slice(0, 2) + hour.value + min.value;
    updatePrecipitation(tstamp);
    updateWind(tstamp);
    updateParticulateMatter(tstamp);
}

async function updatePrecipitation(tstamp) {
    let x = await fetch("http://localhost:8080/precipitation?tstamp=" + tstamp);
    let y = await x.text();
    let z = JSON.parse(y)

    document.getElementById("rain_div").innerHTML = '<b style ="font-size:14px;">&nbsp;Niederschlag in den letzten 10 Min.: ' + z.precipitation[0].mm.toFixed(2)
        + ' mm</b><br><b style ="font-size:14px;">&nbsp;Regendauer in den letzten 10 Min.: ' + z.precipitation[0].duration + ' Min.</b>';
    hour_of_day = parseInt(tstamp.slice(8,10))
    if (hour_of_day < 6 || hour_of_day > 18){
        var day = 0;
    } else {
        var day = 1;
    }
    if (z.precipitation[0].raining == "1") {
        document.getElementById("rain_img").src = "resources/rain1_" + day + ".png";
    } else {
        document.getElementById("rain_img").src = "resources/rain0_" + day + ".png";
    }
    document.getElementById("rain_img").style.visibility = "visible";
}
updatePrecipitation("202101010000");

async function updateWind(tstamp) {
    let i = await fetch("http://localhost:8080/windVector?tstamp=" + tstamp);
    let j = await i.text();
    let z = JSON.parse(j)

    var x_component = z.x;
    var y_component = z.y;

    var winddata = [{
        "header": {
            "discipline": 0,
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
            "dy": 1.0
        },
        "data": [x_component, x_component, x_component, x_component]
    }, {
        "header": {
            "discipline": 0,
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
            "dy": 1.0
        },
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

async function updateParticulateMatter(tstamp) {
    // 202101010000 to 2021-01-01T00:01:00
    tstamp = tstamp.slice(0, 4) + "-" + tstamp.slice(4, 6) + "-" + tstamp.slice(6, 8) + "T" + tstamp.slice(8, 10) + ":" + tstamp.slice(10, 11);

    let x = await fetch("http://localhost:8080/PMData?tstamp=" + tstamp);
    let y = await x.text();
    let z = JSON.parse(y)

    var dataPM10 = [];
    var dataPM25 = [];

    for (const element of z.sensors) {
        dataPM10.push([element.LAT, element.LON, element.P1]);
        dataPM25.push([element.LAT, element.LON, element.P2]);
    }

    // _____________________redraw PM2.5 Layer_____________________
    map.removeLayer(idwPM2_5Layer);
    layerControl.removeLayer(idwPM2_5Layer);
    idwPM2_5Layer = L.idwLayer(dataPM25,
        {
            opacity: 0.35, cellSize: 20, exp: 2, max: 50, gradient: {
                0.0: '#ffffff',
                0.1: '#f7fbff',
                0.2: '#deebf7',
                0.3: '#c6dbef',
                0.4: '#9ecae1',
                0.5: '#6baed6',
                0.6: '#4292c6',
                0.7: '#2171b5',
                0.8: '#08519c',
                0.9: '#08306b',
                1.0: '#08306b'
            }
        })
    layerControl.addOverlay(idwPM2_5Layer, "Feinstaub - PM2.5");

    // _____________________redraw PM10 Layer_____________________
    map.removeLayer(idwPM10Layer);
    layerControl.removeLayer(idwPM10Layer);
    idwPM10Layer = L.idwLayer(dataPM10,
        {
            opacity: 0.35, cellSize: 20, exp: 2, max: 50, gradient: {
                0.0: '#ffffff',
                0.1: '#f7fbff',
                0.2: '#deebf7',
                0.3: '#c6dbef',
                0.4: '#9ecae1',
                0.5: '#6baed6',
                0.6: '#4292c6',
                0.7: '#2171b5',
                0.8: '#08519c',
                0.9: '#08306b',
                1.0: '#08306b'
            }
        }).addTo(map);
    layerControl.addOverlay(idwPM10Layer, "Feinstaub - PM10");
}
updateParticulateMatter("202101010000");
