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
});


// ______________________Layer Control______________________
var baseMaps = {};
var layerControl = L.control.layers(baseMaps);
layerControl.addTo(map);

layerControl.addOverlay(velocityLayer, "Wind");
map.addLayer(velocityLayer);


/*
// Example how to get data from the server
getText("http://localhost:8080/PMStations");

async function getText(file) {
    let x = await fetch(file);
    let y = await x.text();
    document.getElementById("test").innerHTML = y;
}
*/
