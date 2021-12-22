async function drawDiagram3() {
    let x = await fetch("http://localhost:8080/DiagramWindDirection");
    let y = await x.text();
    let diagramWindDirection = JSON.parse(y)

    var diagramPMData10 = [];
    var diagramPMData25 = [];

    for (const element of diagramWindDirection.diagramWindDirection) {
        diagramPMData10.push(element.P1);
        diagramPMData25.push(element.P2);
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
        height: 350,
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
      title: {
        text: 'Ø Feinstaub bei Windrichtung (µg/m³)',
        align: 'center'
      },
      subtitle: {
        text: "(ab 1 m/s Windgeschwindigkeit)",
        align: 'center',
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