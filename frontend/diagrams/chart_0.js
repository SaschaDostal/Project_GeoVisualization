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

    // Chart 0

    var options = {
        subtitle: {
            text: "Zeitachse in 10 Min. Abständen",
            align: 'center',
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
                return val + ' - ' + opts.w.globals.series[opts.seriesIndex][opts.dataPointIndex].toFixed(2) + ''
            }
        },
        tooltip: {
            custom: function ({ series, seriesIndex, dataPointIndex, w }) {
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
                sizeOffset: 0
            }
        },
        xaxis: {
            labels: {
                show: false,
            },
            position: 'top',
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


    // Chart 2
    
    
    diagrammLine.diagrammLine.sort(function (a, b) {
        return parseFloat(a.SPEED) - parseFloat(b.SPEED);
    })
    // Calculation of Average PM for specific wind speed
    var wind = [];
    var PM10 = [];
    var PM25 = [];
    var weight = 1;
    var lastWind = -1.0;
    var P1 = 0.0;
    var P2 = 0.0;
    for (const element of diagrammLine.diagrammLine) {
        if(parseFloat(element.SPEED) == lastWind){
            P1 +=  parseFloat(element.P1);
            P2 +=  parseFloat(element.P2);
            weight++;
        } else if (lastWind == -1.0){
            lastWind = parseFloat(element.SPEED);
            P1 = parseFloat(element.P1);
            P2 = parseFloat(element.P2);
        } else {
            wind.push(lastWind);
            PM10.push(P1/weight);
            PM25.push(P2/weight);
            weight = 1;
            lastWind = parseFloat(element.SPEED);
            P1 = parseFloat(element.P1);
            P2 = parseFloat(element.P2);
        }
    }
    wind.push(lastWind);
    PM10.push(P1/weight);
    PM25.push(P2/weight);
    
    var options2 = {
        series: [{
            name: "Feinstaub PM10 (µg/m³)",
            data: PM10
        },
        {
            name: "Feinstaub PM2.5 (µg/m³)",
            data: PM25
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
            width: [1, 1],
            curve: 'straight',
            dashArray: [0, 0]
        },
        legend: {
            tooltipHoverFormatter: function (val, opts) {
                return val + ' - ' + opts.w.globals.series[opts.seriesIndex][opts.dataPointIndex].toFixed(2) + ''
            },
        },
        tooltip: {
            custom: function ({ series, seriesIndex, dataPointIndex, w }) {
                return ''
            },
            fixed: {
                enabled: true,
                position: 'topRight',
                offsetX: 0,
                
            }
        },
        markers: {
            size: 0,
            hover: {
                sizeOffset: 0
            }
        },
        xaxis: {
            labels: {
                show: true,
                formatter: function (value) {
                    return parseFloat(value);
                },
                tooltip: {
                    enabled: false
                }
            },
            tickAmount: 10,
            title: {
                text: 'Windgeschwindigkeit (m/s)',
                offsetY: -16,
                style: {
                    fontWeight:  '1'
                }
            },
            position: 'bottom',
            tooltip: {
                enabled: false
            },
            categories: wind
        },
        yaxis: [
            {
                show: true,
                seriesName: "Feinstaub PM10 (µg/m³)",
                title: {
                    text: 'Ø Feinstaub (µg/m³)',
                    offsetX: -5,
                    style: {
                        fontWeight:  '1'
                    }
                },
                labels: {
                    show: true,
                    formatter: function (value) {
                        return parseInt(value);
                    },
                    offsetX: -10
                }
            },
            {
                show: false,
                seriesName: "Feinstaub PM10 (µg/m³)"
            },
        ],
        grid: {
            borderColor: '#f1f1f1'
        }
    };

    var chart2 = new ApexCharts(document.querySelector("#chart_2"), options2);
    chart2.render();
}

drawDiagram0();