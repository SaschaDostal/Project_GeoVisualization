var diagramWindData = [];
var diagramRainData = [];
var tstamps = [];

async function drawDiagram0() {
    let x = await fetch("http://localhost:8080/DiagramLine");
    let y = await x.text();
    let diagrammLine = JSON.parse(y)

    var diagramPMData10 = [];
    var diagramPMData25 = [];

    for (const element of diagrammLine.diagrammLine) {
        diagramWindData.push(element.SPEED);
        diagramRainData.push(element.MM);
        diagramPMData10.push(element.P1);
        diagramPMData25.push(element.P2);
        tstamps.push(element.TSTAMP);
    }

    var options = {
        series: [{
            name: "Feinstaub PM10 (µg/m³)",
            data: diagramPMData10
        },
        {
            name: "Feinstaub PM2.5 (µg/m³)",
            data: diagramPMData25
        },
        {
            name: 'Windgeschwindigkeit (m/s)',
            data: diagramWindData
        },
        {
            name: 'Regenmenge der letzten 10 Min. (mm)',
            data: diagramRainData
        }
        ],
        chart: {
            type: 'line',
            zoom: {
                enabled: true
            },
            toolbar: {
                show: false
            }
        },
        dataLabels: {
            enabled: false
        },
        stroke: {
            width: [1, 1, 1, 1],
            curve: 'straight',
            dashArray: [0, 0, 5, 3]
        },
        legend: {
            tooltipHoverFormatter: function (val, opts) {
                return val + ' - ' + opts.w.globals.series[opts.seriesIndex][opts.dataPointIndex] + ''
            }
        },
        tooltip: {
            custom: function({series, seriesIndex, dataPointIndex, w}) {
                return ''
            },
            fixed: {
                enabled: true,
                position: 'topRight',
                offsetX: 0,
                offsetY: 0,
            }
        },
        markers: {
            size: 0,
            hover: {
                sizeOffset: 1
            }
        },
        xaxis: {
            labels: {
                show: false,
            },
            position: 'top',
            tickAmount: 100,
            categories: tstamps
        },
        yaxis: [
            {
                show: false,
                seriesName: "Feinstaub PM10 (µg/m³)"
            },
            {
                show: false,
                seriesName: "Feinstaub PM10 (µg/m³)"
            },
            {
                show: false
            },
            {
                show: false
            }
        ],
        grid: {
            borderColor: '#f1f1f1'
        }
    };

    var chart0 = new ApexCharts(document.querySelector("#chart_0"), options);
    chart0.render();
}

drawDiagram0();