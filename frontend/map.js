// ______________________Map Creation______________________
var map = L.map('mapid', {
    zoomControl: false, maxBounds: [
        //south west
        [48.65, 9.4],
        //north east
        [48.9, 9.0]
    ],
}).setView([48.782, 9.18], 12);
var OpenStreetMap_DE = L.tileLayer('https://{s}.tile.openstreetmap.de/tiles/osmde/{z}/{x}/{y}.png', {
    maxZoom: 15,
    minZoom: 11,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
});
var CartoDB_Positron = L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>',
    subdomains: 'abcd',
    maxZoom: 15,
    minZoom: 11,
});
CartoDB_Positron.addTo(map);

L.geoJSON(stuttgart_geo, {"fillOpacity": .5, color:'#7d7d7d', stroke:false}).addTo(map);

// _______________________Legende________________________
var legend = L.control({ position: 'bottomright' });
legend.onAdd = function (map) {
    var colors = ["#ffffff", "#deebf7", "#9ecae1", "#4292c6", "#08519c", "#08306b"];
    var div = L.DomUtil.create('div', 'legend');
    div.style.backgroundColor = "rgba(255,255,255,transparent)";
    labels = ['<div><div style="float:left">&nbsp;<img src="./resources/pm.png" width="18" height="18"><b style ="font-size:14px;">&nbsp;Feinstaubmessstation&nbsp;</b><br></br></div>'];
    div.innerHTML += labels.push('<b style ="font-size:14px;">&nbsp;Feinstaubwerte:</b>');
    categories = ['0 µg/m³', '10 µg/m³', '20 µg/m³', '30 µg/m³', '40 µg/m³', '≥50 µg/m³'];
    for (var i = categories.length - 1; i >= 0; i--) {
        div.innerHTML +=
            labels.push(
                '<div><div style="float:left">&nbsp;</div><div id="circle" style="background:' + colors[i] + ';float:left"></div><b style ="font-size:14px;">&nbsp;' + categories[i] + '&nbsp;</b></div>');
    }
    div.innerHTML = labels.join('<br>');
    return div;
};
legend.addTo(map);

// _______________________Weather/Rain________________________
var rain = L.control({ position: 'topleft' });
rain.onAdd = function (map) {
    var div = L.DomUtil.create('div', 'rain');
    div.style.backgroundColor = "transparent";
    // Icons made by BZZRINCANTATION from https://www.flaticon.com/
    div.innerHTML = '<div id="rain_div"></div><img id="rain_img" alt="weather" style="width:100px;height:100px;visibility:hidden;">';
    return div;
};
rain.addTo(map);

// ______________________Wind Layer______________________
var velocityLayer = L.velocityLayer({
    displayValues: true,
    displayOptions: {
        velocityType: "Wind",
        position: "bottomleft",
        emptyString: "Keine Winddaten verfügbar."
    },
    maxVelocity: 15
}).addTo(map);

// _________________Leaflet IDW PM Layer__________________
var idwPM10Layer = L.idwLayer([[0,0,0]],
    {
        opacity: 0.25, cellSize: 20, exp: 2, max: 50, gradient: {
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

var idwPM2_5Layer = L.idwLayer([[0,0,0]],
    {
        opacity: 0.25, cellSize: 20, exp: 2, max: 50, gradient: {
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

// _____________________Layer Control_____________________
var baseMaps = {};
var layerControl = L.control.layers(baseMaps);
layerControl.addTo(map);

layerControl.addOverlay(velocityLayer, "Wind");
layerControl.addOverlay(idwPM2_5Layer, "Feinstaub - PM2.5");
layerControl.addOverlay(idwPM10Layer, "Feinstaub - PM10");

// ____________________PM Station Marker____________________

var pmicon = L.icon({
    iconUrl: 'resources/pm.png',
    iconSize: [20, 20],
    iconAnchor: [0, 16]
});

drawPMStations()

async function drawPMStations() {
    let x = await fetch("http://localhost:8080/PMStations");
    let y = await x.text();
    let stations = JSON.parse(y)

    markers = []

    for (const element of stations.pmstations) {
        var marker = L.marker(
            [element.lat, element.lon],
            {
                icon: pmicon,
                title: 'PM Station: ' + element.id
            }
        ).on('click', L.bind(onClick, null, element.id))
        markers.push(marker)
    }

    var pmmarkers = L.layerGroup(markers).addTo(map);
    layerControl.addOverlay(pmmarkers, "Feinstaub Messstationen");
}

function onClick(id) {
    document.getElementById("rightsidebar").click();
    document.getElementById("chart_1").innerText = "";
    var day = document.getElementById("selectDay");
    var hour = document.getElementById("selectHour");
    var min = document.getElementById("selectMin");
    updateSensorData(id, day, hour, min);
    drawDiagram1(id);
}

async function updateSensorData(id, day, hour, min) {
    var tstamp = day.value.slice(6) + day.value.slice(3, 5)
        + day.value.slice(0, 2) + hour.value + min.value;
    let x = await fetch("http://localhost:8080/dataSingleSensor?id=" + id + "&tstamp=" + tstamp);
    let y = await x.text();
    let z = JSON.parse(y);

    let a = await fetch("http://localhost:8080/PMStations/" + id);
    let b = await a.text();
    let c = JSON.parse(b);
    document.getElementById("info").innerHTML = '<h6 class="center">Aktuelle Werte (' + day.value +' - ' + hour.value + ':' + min.value + ' Uhr)<br></h6><br>Feinstaubmessstations-ID: ' + id 
        + '<br>Längengrad: ' + c.lon + '<br>Breitengrad: ' + c.lat + '<br>PM2.5: ' + z.P2 + ' µg/m³<br>PM10: ' + z.P1 + ' µg/m³<br>Windgeschwindigkeit: ' + z.SPEED + ' m/s<br>Windrichtung: ' 
        + z.DEGREE + ' °<br>Niederschlagsmenge (letzte 10 Min.): ' + z.MM + ' mm<br>Niederschlagsdauer (letzte 10 Min.): ' + z.DURATION + ' Minuten<br><br>';
}