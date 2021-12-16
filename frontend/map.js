// ______________________Map Creation______________________
var map = L.map('mapid').setView([48.782, 9.18], 12);
var OpenStreetMap_DE = L.tileLayer('https://{s}.tile.openstreetmap.de/tiles/osmde/{z}/{x}/{y}.png', {
    maxZoom: 18,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
});
var CartoDB_Positron = L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>',
    subdomains: 'abcd',
    maxZoom: 19
});
CartoDB_Positron.addTo(map);

// _______________________Legende________________________
var legend = L.control({ position: 'bottomright' });
legend.onAdd = function (map) {
    var colors = ["#ffffff", "#fee6ce", "#fdae6b", "#f16913", "#a63603", "#7f2704"];
    var div = L.DomUtil.create('div', 'legend');
    div.style.backgroundColor = "white";
    labels = ['<b>&nbsp;Feinstaubwerte:</b>'];
    categories = ['0 µg/m³', '240 µg/m³', '480 µg/m³', '720 µg/m³', '960 µg/m³', '1200 µg/m³'];
    for (var i = categories.length - 1; i >= 0; i--) {
        div.innerHTML +=
            labels.push(
                '<div><div style="float:left">&nbsp;</div><div id="circle" style="background:' + colors[i] + ';float:left"></div>&nbsp;' + categories[i] + '&nbsp;</div>');
    }
    div.innerHTML = labels.join('<br>');
    return div;
};
legend.addTo(map);

// ______________________Wind Layer______________________
var velocityLayer = L.velocityLayer({
    displayValues: true,
    displayOptions: {
        velocityType: "Wind",
        position: "bottomleft",
        emptyString: "Keine Winddaten verfügbar."
    },
    data: winddata,
    maxVelocity: 15
}).addTo(map);

// _________________Leaflet IDW PM Layer__________________
var idwPM10Layer = L.idwLayer(idwData,
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

var idwPM2_5Layer = L.idwLayer(idwData2,
    {
        opacity: 0.25, cellSize: 20, exp: 2, max: 1200, gradient: {
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

// ____________________PM Station Marker____________________

var pmicon = L.icon({
    // Icons made by https://www.flaticon.com/authors/victoruler from https://www.flaticon.com/
    iconUrl: 'resources/pmcloud.png',
    iconSize: [32, 32],
    iconAnchor: [0, 16]
});

var markert1 = L.marker(
    [48.77069, 9.18207],
    {
        icon: pmicon,
        title: 'PM Station Test'
    }
).on('click', onClick)

var markert2 = L.marker(
    [48.77336, 9.17313],
    {
        icon: pmicon,
        title: 'PM Station Test'
    }
).on('click', onClick)

var markert3 = L.marker(
    [48.76944, 9.15423],
    {
        icon: pmicon,
        title: 'PM Station Test'
    }
).on('click', onClick)

var markert4 = L.marker(
    [48.78688, 9.17915],
    {
        icon: pmicon,
        title: 'PM Station Test'
    }
).on('click', onClick)

var pmmarkers = L.layerGroup([markert1, markert2, markert3, markert4]).addTo(map);

function onClick(){
    document.getElementById("rightsidebar").click();
}

// _____________________Layer Control_____________________
var baseMaps = {};
var layerControl = L.control.layers(baseMaps);
layerControl.addTo(map);

layerControl.addOverlay(velocityLayer, "Wind");
layerControl.addOverlay(idwPM10Layer, "Feinstaub - PM 10");
layerControl.addOverlay(idwPM2_5Layer, "Feinstaub - PM 2.5");
layerControl.addOverlay(pmmarkers, "Feinstaub Messstationen");

// Example how to get data from the server
getText("http://localhost:8080/PMStations");

async function getText(file) {
    let x = await fetch(file);
    let y = await x.text();
    document.getElementById("test").innerHTML = y;
}

