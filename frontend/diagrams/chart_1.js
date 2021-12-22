async function drawDiagram1(PMid) {
    let x = await fetch("http://localhost:8080/DiagramLinePM/" + PMid);
    let y = await x.text();
    let diagrammLinePM = JSON.parse(y)

    var diagramPMData10 = [];
    var diagramPMData25 = [];

    for (const element of diagrammLinePM.diagrammLinePM) {
        diagramPMData10.push(element.P1);
        diagramPMData25.push(element.P2);
    }

    var options = {
        title: {
            text: "Verschiedene Daten über Zeit",
            align: 'center',
            offsetY: 20
        },
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
                enabled: false
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

    var chart1 = new ApexCharts(document.querySelector("#chart_1"), options);
    chart1.render();
}