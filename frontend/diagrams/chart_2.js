async function drawDiagram3() {
  let x = await fetch("http://localhost:8080/DiagramWindDirection");
  let y = await x.text();
  let diagramWindDirection = JSON.parse(y)

  var diagramPMData10 = [];
  var diagramPMData25 = [];

  for (const element of diagramWindDirection.diagramWindDirection) {
    diagramPMData10.push(parseFloat(element.P1).toFixed(1));
    diagramPMData25.push(parseFloat(element.P2).toFixed(1));
  }

  var options = {
    series: [{
      name: 'PM10',
      data: diagramPMData10,
    }, {
      name: 'PM2.5',
      data: diagramPMData25,
    }],
    chart: {
      type: 'radar',
      toolbar: {
        show: false
      },
      dropShadow: {
        enabled: true,
        blur: 1,
        left: 1,
        top: 1
      }
    },
    dataLabels: {
      enabled: true
    },
    subtitle: {
      text: "in µg/m³ (nur Werte ab 1 m/s Windgeschwindigkeit)",
      align: 'center',
      offsetY: -3
    },
    stroke: {
      width: 2
    },
    fill: {
      opacity: 0.1
    },
    markers: {
      size: 0
    },
    xaxis: {
      categories: ['N', 'NO', 'O', 'SO', 'S', 'SW', 'W', 'NW']
    }
  };

  var chart = new ApexCharts(document.querySelector("#chart_3"), options);
  chart.render();
}
drawDiagram3();