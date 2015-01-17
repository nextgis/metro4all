 $(document).ready(function()
 {
     $('#questionSymbol').each(function() {
         $(this).qtip({
             content: {
                 text: $('.accessTooltip'),
                 style: { classes: 'accessTooltip' },
                 button: 'Close'
             },
             show: 'click',
             hide: {
                 event: false
             },
             position: {
                 adjust: {
                     y: 12
                 }
             }
         });
     });
 });

var stationTooltip = d3.select("div#metroMap").append("div").attr("class", "stationTooltip");

queue()
.defer(d3.xml, "data/msk/metro_WebOptimized.svg", "image/svg+xml")
.defer(d3.csv, "data/msk/metroReport.csv")
.defer(d3.csv, "data/msk/stationsReport.csv")
.defer(d3.csv, "data/msk/transfersReport.csv")
.defer(d3.csv, "data/msk/nodesReport.csv")
.await(ready);

function ready(error, xml, metroData, stationsData, transfersData, nodesData) {

  var positionById = {},
  metroListData = [],
  stationDataById = {},
  transferDataById = {},
  nodeDataById = {};

  var importedNode = document.importNode(xml.documentElement, true);
  d3.select("#metroMap").node().appendChild(importedNode);

  var svg = d3.select("svg#svgCanvas"),
  svgWidth = (svg.style("width")).replace('px', ''),
  svgHeight = (svg.style("height")).replace('px', '');

  /*
  d3.select(window).on('resize', resize);
  function resize() {
    console.log(d3.select("#metroMap").attr("width"));
    console.log(svg.style());
    console.log(svg.attr("id"));
    //svgWidth = d3.select('div.shema-infoGraph').attr("width"),
    //svgHeight = d3.select('div.shema-infoGraph').attr("height");
  };*/

  var zoom = d3.behavior.zoom()
    .scaleExtent([0.7, 3])
    .size([svgWidth, svgHeight])
    .on("zoom", zooming);

  svg.call(zoom);

  var mainG = svg.select("g#mainG");

  d3.select("form#zoomControls").selectAll("input")
  .on("click", zoomEvent);

  stationsData.forEach(function(d) {
    stationDataById[d.stationId] = {
      lineId: parseInt(d.lineId),
      nodeId: parseInt(d.nodeId),
      stationName: d.stationName,
      //stationName_en: d.stationName_en,
      lineName: d.lineName,
      routesIn: parseInt(d.routesIn),
      routesOut: parseInt(d.routesOut),
      minTaper: parseInt(d.minTaper),
      //maxTaper: d.maxTaper,
      minStairs: parseInt(d.minStairs),
      avStairs: parseInt(d.avStairs),
      maxStairs: parseInt(d.maxStairs),
      minRailsStairs: parseInt(d.minRailsStairs),
      avRailsStairs: parseInt(d.avRailsStairs),
      maxRailsStairs: parseInt(d.maxRailsStairs),
      //lift: d.lift,
      //minLiftAmount: d.minLiftAmount,
      maxLiftAmount: parseInt(d.maxLiftAmount),
      minLiftStairs: parseInt(d.minLiftStairs),
      avLiftStairs: parseInt(d.avLiftStairs),
      maxLiftStairs: parseInt(d.maxLiftStairs),
      minStairways: parseInt(d.minStairways),
      maxStairways: parseInt(d.maxStairways),
      //stairwayAvailableRoutesIn: d.stairwayAvailableRoutesIn,
      //stairwayAvailableRoutesOut: d.stairwayAvailableRoutesOut,
      //stairwayAvailableRoutesAmount: d.stairwayAvailableRoutesAmount,
      //liftAvailableRoutesIn: d.liftAvailableRoutesIn,
      //liftAvailableRoutesOut: d.liftAvailableRoutesOut,
      //liftAvailableRoutesAmount: d.liftAvailableRoutesAmount,
      wheelchairFriendlyRoutes: parseInt(d.wheelchairFriendlyRoutes),
      wheelchairFriendlyRoutesIn: parseInt(d.wheelchairFriendlyRoutesIn),
      wheelchairFriendlyRoutesOut: parseInt(d.wheelchairFriendlyRoutesOut),
      //wheelchairFriendlyRoutesAmount: d.wheelchairFriendlyRoutesAmount,
      handicappedFriendlyRoutes: parseInt(d.handicappedFriendlyRoutes),
      handicappedFriendlyRoutesIn: parseInt(d.handicappedFriendlyRoutesIn),
      handicappedFriendlyRoutesOut: parseInt(d.handicappedFriendlyRoutesOut),
      //handicappedFriendlyRoutesAmount: d.handicappedFriendlyRoutesAmount,
      luggageFriendlyRoutes: parseInt(d.luggageFriendlyRoutes),
      luggageFriendlyRoutesIn: parseInt(d.luggageFriendlyRoutesIn),
      luggageFriendlyRoutesOut: parseInt(d.luggageFriendlyRoutesOut)
      //luggageFriendlyRoutesAmount: d.luggageFriendlyRoutesAmount
    };
  });
//console.log(stationDataById);

  nodesData.forEach(function(d) {
    nodeDataById[d.nodeId] = {
      nodeName: d.nodeName,
      //nodeStations: d.nodeStations,
      //nodeTransfers: d.nodeTransfers,      
      totalElements: parseInt(d.totalElements),
      stairwaysAmount: parseInt(d.stairwaysAmount),
      //stairway: d.stairway,
      //minStairwayWidth: d.minStairwayWidth,
      //maxStairwayWidth: d.maxStairwayWidth,
      //doorsAmount: d.doorsAmount,
      //minDoorWidth: d.minDoorWidth,
      //maxDoorWidth: d.maxDoorWidth,
      //tapersAmount: d.tapersAmount,
      //minTaperWidth: d.minTaperWidth,
      //maxTaperWidth: d.maxTaperWidth,
      doorsAndTapersAmount: parseInt(d.doorsAndTapersAmount),
      minDoorAndTaperWidth: parseInt(d.minDoorAndTaperWidth),
      //maxDoorAndTaperWidth: d.maxDoorAndTaperWidth,
      turnstilesAmount: parseInt(d.turnstilesAmount),
      minturnstileWidth: parseInt(d.minturnstileWidth),
      //maxturnstileWidth: d.maxturnstileWidth,
      liftAmount: parseInt(d.liftAmount),
      //lift: d.lift,
      surfaceLevelLiftsAmount: parseInt(d.surfaceLevelLiftsAmount),
      hallLevelLiftsAmount: parseInt(d.hallLevelLiftsAmount),
      trainLevelLiftsAmount: parseInt(d.trainLevelLiftsAmount),
      //surfaceLevelLifts: d.surfaceLevelLifts,
      //hallLevelLifts: d.hallLevelLifts,
      //trainLevelLifts: d.trainLevelLifts,
      minLiftWidth: parseInt(d.minLiftWidth),
      //maxLiftWidth: d.maxLiftWidth,
      pandusAmount: parseInt(d.pandusAmount),
      //pandus: d.pandus,
      pandusAvailableAmount: parseInt(d.pandusAvailableAmount),
      pandusAvailable: d.pandusAvailable,
      //pandusRailing: d.pandusRailing,
      //pandusMinSlope: d.pandusMinSlope,
      pandusMaxSlope: parseInt(d.pandusMaxSlope),
      stairsAmount: parseInt(d.stairsAmount),
      //stairs: d.stairs,
      coupleStairsAmount: parseInt(d.coupleStairsAmount),
      //coupleStairs: d.coupleStairs,
      //stairsLength: d.stairsLength,
      railsStairsAmount: parseInt(d.railsStairsAmount),
      //rails: d.rails,
      //railsStairsLength: d.railsStairsLength,
      minRailsWidth: parseInt(d.minRailsWidth),
      maxRailsWidth: parseInt(d.maxRailsWidth),
      //minRailsSlope: d.minRailsSlope,
      maxRailsSlope: parseInt(d.maxRailsSlope),
      //railingAmount: d.railingAmount,
      //noRailing: d.noRailing,
      noRailingAmount: parseInt(d.noRailingAmount)
      //railingStairsLength: d.railingStairsLength,
      //noRailingStairsLength: d.noRailingStairsLength
    };
  });

  transfersData.forEach(function(d) {
    var transferDataObj = {
      fromStation: d.fromStation,
      toStation: d.toStation,
      //transferId: d.transferId,
      fromId: d.fromId,
      toId: d.toId,
      nodeId: d.nodeId,
      minWidth: parseInt(d.minWidth),
      minStairs: parseInt(d.minStairs),
      minRailsStairs: parseInt(d.minRailsStairs),
      lift: parseInt(d.lift),
      //liftStairsEconomy: d.liftStairsEconomy,
      //minRailsWidth: d.minRailsWidth,
      //maxRailsWidth: d.maxRailsWidth,
      //maxAngle: d.maxAngle,
      //maxSlope: d.maxSlope,
      //minStairways: d.minStairways,
      wheelchairFriendlyRoutes: parseInt(d.wheelchairFriendlyRoutes),
      handicappedFriendlyRoutes: parseInt(d.handicappedFriendlyRoutes),
      luggageFriendlyRoutes: parseInt(d.luggageFriendlyRoutes),
      minLiftStairs: parseInt(d.minLiftStairs),
      lineId: d.lineId,
      line: d.line
    };
    if (typeof transferDataById[d.transferId] === "undefined") {
      transferDataById[d.transferId] = [];
    };
    (transferDataById[d.transferId]).push(transferDataObj);
  });

  var lines = d3.select("g#shema").selectAll("path"),
      nodes = d3.select("g#stations").selectAll("g");
      stations = d3.select("g#stations").selectAll("path"),
      //stations = d3.select("g#stations").selectAll("circle"),
      transfers = d3.select("g#stations").selectAll("rect"),
      stationLabels = d3.select("g#stations").selectAll("text.stationLabel"),
      stationIcons = d3.select("g#stations").selectAll("image.stationIcon"),
      transferLabels = d3.select("g#stations").selectAll("text.transferLabel"),
      reset = d3.select("input#resetAll"),
      checkbox = d3.selectAll("input[name=stationsNames]"),
      radio = d3.select("form#accessControls").selectAll("input"),
      nodeInfo = d3.select("div#nodeInfo"),
      targetInfo = d3.select("div#targetInfo"),
      targetAccess = d3.select("div#targetAccess"),
      targetDescription = d3.select("span#targetDescr"),
      nodeDescription = d3.select("p#nodeDescr"),
      shemaHeader = d3.select("h3#shemaHeader"),
      nodeInfoData = [],
      targetInfoData = [],
      targetAccessData = [];
   //console.log(nodeDataById);
  //Clearing all temporary stuff!
  resetShema();
  resetMenu();

  //reset.on("click", resetAll);
  reset.on("click", function() {
    resetShema();
    resetMenu();
    metroStat(metroData);
  });

  //Add info about metro stat
  metroStat(metroData);

  /*//Show/hide station's names
  checkbox.on("change", function() {
    resetLabels(stationLabels);
    resetLabels(transferLabels);
    namesForStations(stationLabels, stationDataById, this.checked);
  })*/

  //Get coordinates for labels
  d3.selectAll(".stationLabel").each( function() {
    positionById[splitId(this.id)] =  {x:d3.select(this).attr("x"),y:d3.select(this).attr("y")};
  });

  radio.on("change", function() {
    resetShema();
    switch (this.value) {
      case "wheelchairFriendly": lines.classed("dim", true);
                           transfers.classed("dim", true);
                           stations.filter(function() {
                             if (stationDataById[splitId(this.id)].wheelchairFriendlyRoutes == 0) {return this.id;}
                           }).classed("dim", true);
                           stationIcons.filter(function() {
                             if (stationDataById[splitId(this.id)].wheelchairFriendlyRoutes != 0) {return this.id;}
                           }).attr("xlink:href", "img/wheelchair_icon.svg").classed("hidden", false);
                           shemaHeader.html('Станции с маршрутами, доступными для инвалидов-колясочников').classed("hidden", false);
                           break;

      case "handicappedFriendly": lines.classed("dim", true);
                            transfers.classed("dim", true);
                            stations.filter(function() {
                              if (stationDataById[splitId(this.id)].handicappedFriendlyRoutes == 0) {return this.id;}
                            }).classed("dim", true);
                            stationIcons.filter(function() {
                              if (stationDataById[splitId(this.id)].handicappedFriendlyRoutes != 0) {return this.id;}
                            }).attr("xlink:href", "img/aged_icon.svg").classed("hidden", false);
                            shemaHeader.html('Станции с маршрутами, доступными для людей с затруднениями передвижения').classed("hidden", false);
                            break;

      case "luggageFriendly": lines.classed("dim", true);
                        transfers.classed("dim", true);
                        stations.filter(function() {
                          if (stationDataById[splitId(this.id)].luggageFriendlyRoutes == 0) {return this.id;}
                        }).classed("dim", true);
                        stationIcons.filter(function() {
                          if (stationDataById[splitId(this.id)].luggageFriendlyRoutes != 0) {return this.id;}
                        }).attr("xlink:href", "img/luggage_icon.svg").classed("hidden", false);
                        shemaHeader.html('Станции с маршрутами, доступными для людей с детскими колясками').classed("hidden", false);
                        break;
    };
    scrollToFocus("metroMap");
  });

  //Stations (circles) events
  stations
  .on("mouseover", function() {
    var id = splitId(this.id),
    t = stationTooltip.html("");

    t.style("display", "block")
    /*.style("left", (d3.event.pageX - 285) + "px")
    .style("top", (d3.event.pageY - 250) + "px");*/
    .style('left', (d3.mouse(d3.select('div.content').node())[0] + 15) + 'px')
    .style('top', (d3.mouse(d3.select('div.content').node())[1] - 5) + 'px'); 
    t.append("span").text(stationDataById[id].stationName);
    //console.log(d3.event.pageX);
  })
  .on("mouseout", function() {
    stationTooltip.style("display", "none");
  })
  .on("click", function() {
    //var targetInfo = d3.select("div#targetInfo");
    //var nodeInfo = d3.select("div#nodeInfo");
    nodeInfoData = [];
    targetInfoData = [];
    targetAccessData = [];

    //Routes by stations (station's data)
    var stationData = stationDataById[splitId(this.id)];
    var tableData = [];
    tableData.push(
      {factor: 'Количество маршрутов на вход', value: stationData.routesIn},
      {factor: 'Количество маршрутов на выход', value: stationData.routesOut},
      {factor: 'Самое узкое место на маршрутах, мм', value: stationData.minTaper},
      {factor: 'Минимальное количество эскалаторов на маршрутах', value: stationData.minStairways},
      {factor: 'Максимальное количество лифтов на маршрутах', value: stationData.maxLiftAmount},
      {factor: 'Минимальная протяжённость лестниц на маршрутах, ступенек', value: stationData.minStairs},
      {factor: 'Средняя протяжённость лестниц на маршрутах, ступенек', value: stationData.avStairs},
      {factor: 'Максимальная протяжённость лестниц на маршрутах, ступенек', value: stationData.maxStairs},
      {factor: 'Минимальная протяжённость лестниц на маршрутах с учетом рельс и пандусов, ступенек', value: stationData.minRailsStairs},
      {factor: 'Средняя протяжённость лестниц на маршрутах с учетом рельс и пандусов, ступенек', value: stationData.avRailsStairs},
      {factor: 'Максимальная протяжённость лестниц на маршрутах с учетом рельс и пандусов, ступенек', value: stationData.maxRailsStairs}
    );

    if (stationData.maxLiftAmount > 0) {
      tableData.push(
        {factor: 'Минимальная протяжённость лестниц на маршрутах с учетом лифтов, ступенек', value: stationData.minLiftStairs},
        {factor: 'Средняя протяжённость лестниц на маршрутах с учетом лифтов, ступенек', value: stationData.avLiftStairs},
        {factor: 'Максимальная протяжённость лестниц на маршрутах с учетом лифтов, ступенек', value: stationData.maxLiftStairs}
      );      
    };  

    targetInfoData = tableData;
    targetDescription.html("по станции " +  "‹‹" + stationData.stationName + "››");

    //Infrastructure by stations (node's data)
    var nodeData = nodeDataById[stationData.nodeId];
    var tableData = [];
    tableData.push(
      {factor: 'Количество элементов инфраструктуры узла', value: nodeData.totalElements},
      {factor: 'Количество эскалаторов', value: nodeData.stairwaysAmount},
      {factor: 'Количество дверей', value: nodeData.doorsAndTapersAmount},
      {factor: 'Минимальная ширина дверей, мм', value: nodeData.minDoorAndTaperWidth},
      {factor: 'Количество турникетов', value: nodeData.turnstilesAmount},
      {factor: 'Минимальная ширина турникетов, мм', value: nodeData.minturnstileWidth}
    );

    if (nodeData.liftAmount > 0) {
      tableData.push(
        {factor: 'Количество лифтов', value: nodeData.liftAmount},
        {factor: 'Количество лифтов на уровень перехода', value: nodeData.hallLevelLiftsAmount},
        {factor: 'Количество лифтов на уровень платформы', value: nodeData.trainLevelLiftsAmount},
        {factor: 'Количество лифтов на уровень поверхности', value: nodeData.surfaceLevelLiftsAmount},
        {factor: 'Минимальная ширина дверей лифтов, мм', value: nodeData.minLiftWidth}
      );      
    };

    if (nodeData.pandusAmount > 0) {
      tableData.push(
        {factor: 'Количество пандусов', value: nodeData.pandusAmount},
        {factor: 'Максимальный уклон пандусов, %', value: nodeData.pandusMaxSlope},
        {factor: 'Количество пандусов, доступных для инвалидов-колясочников', value: nodeData.pandusAvailableAmount}
      );      
    };

    if (nodeData.stairsAmount > 0) {
      tableData.push(
        {factor: 'Количество лестниц', value: nodeData.stairsAmount},
        {factor: 'Количество лестниц без перил', value: nodeData.railsStairsAmount},
        {factor: 'Количество одиночных ступеней', value: nodeData.coupleStairsAmount}        
      );      
    };
      
    if (nodeData.railsStairsAmount > 0) {
      tableData.push(
        {factor: 'Количество лестниц с рельсами', value: nodeData.railsStairsAmount},
        {factor: 'Минимальная ширина рельс, мм', value: nodeData.minRailsWidth},
        {factor: 'Максимальная ширина рельс, мм', value: nodeData.maxRailsWidth},
        {factor: 'Максимальный уклон рельс, %', value: nodeData.maxRailsSlope}      
      );      
    };

    nodeInfoData = tableData;

    nodeDescription.html("Входит в состав узла " + "‹‹" + nodeData.nodeName + "››");
    nodeDescription.classed('hidden', false);

    //Accessibility by stations (station's data)
    var tableData = [];
    tableData.push(
      {factor: 'Количество маршрутов на вход, доступных для инвалидов-колясочников', value: stationData.wheelchairFriendlyRoutesIn},
      {factor: 'Количество маршрутов на выход, доступных для инвалидов-колясочников', value: stationData.wheelchairFriendlyRoutesOut},
      {factor: 'Количество маршрутов на вход, доступных для людей с затруднениями передвижения', value: stationData.handicappedFriendlyRoutesIn},
      {factor: 'Количество маршрутов на выход, доступных для людей с затруднениями передвижения', value: stationData.handicappedFriendlyRoutesOut},
      {factor: 'Количество маршрутов на вход, доступных для людей с детскими колясками', value: stationData.luggageFriendlyRoutesIn},
      {factor: 'Количество маршрутов на выход, доступных для людей с детскими колясками', value: stationData.luggageFriendlyRoutesOut}
    );

    targetAccessData = tableData;

    deleteObjects(nodeInfo, "table");
    deleteObjects(targetInfo, "table");
    deleteObjects(targetAccess, "table");

    tabulate(nodeInfo, nodeInfoData, ['factor', 'value']);
    tabulate(targetInfo, targetInfoData, ['factor', 'value']);
    tabulate(targetAccess, targetAccessData, ['factor', 'value']);

    scrollToFocus("infoPanel");
  });

  //Transfers (rectangles) events
  transfers.on("click", function() {
    //var targetInfo = d3.select("div#targetInfo");
    //var nodeInfo = d3.select("div#nodeInfo");
    nodeInfoData = [];
    targetInfoData = [];
    targetAccessData = [];
    //Routes by transfers (transfer's data)
    var transferData = transferDataById[splitId(this.id)];
    //console.log(transferData);    
    var tableData = [
      {factor: 'Самое узкое место, мм', value: transferData[0].minWidth},
      {factor: 'Общая протяжённость лестниц, ступенек', value: transferData[0].minStairs},
      {factor: 'Протяжённость лестниц с учетом рельс и пандусов, ступенек', value: transferData[0].minRailsStairs}
    ];

    if (transfersData.lift > 0) {
      tableData.push({factor: 'Протяжённость лестниц с учетом лифтов, ступенек', value: transferData.minLiftStairs});
    };

    targetInfoData = tableData;

    targetDescription.html("по переходу " + "‹‹" + transferData[0].fromStation + " - " + transferData[0].toStation + "››");

    //Infrastructure by transfers (node's data)
    var nodeData = nodeDataById[transferData[0].nodeId];
    var tableData = [
      {factor: "Количество эскалаторов", value: nodeData.stairwaysAmount},
      {factor: "Количество лифтов", value: nodeData.liftAmount}
      ];

    nodeInfoData = tableData;

    nodeDescription.html("Входит в состав узла " + "‹‹" + nodeData.nodeName + "››");
    nodeDescription.classed('hidden', false);

    //Accessibility by transfers (transfer's data)
    var tableData = [];
    tableData.push(
      {factor: 'Доступен ли переход для инвалидов-колясочников', value: (transferData[0].wheelchairFriendlyRoutes == 0 ? 'нет' : 'да')},
      {factor: 'Доступен ли переход для людей с затруднениями передвижения', value: transferData[0].handicappedFriendlyRoutes == 0 ? 'нет' : 'да'},
      {factor: 'Доступен ли переход для людей с детскими колясками', value: transferData[0].luggageFriendlyRoutes == 0 ? 'нет' : 'да'}
    );

    targetAccessData = tableData;

    deleteObjects(nodeInfo, "table");
    deleteObjects(targetInfo, "table");
    deleteObjects(targetAccess, "table");

    tabulate(nodeInfo, nodeInfoData, ['factor', 'value']);
    tabulate(targetInfo, targetInfoData, ['factor', 'value']);
    tabulate(targetAccess, targetAccessData, ['factor', 'value']);

    scrollToFocus("infoPanel");
  });


  d3.select("select#infra-select").on("change", function() {
    var sel = this;
    //Clearing all temporary stuff!
    resetShema();
    //Reset radio
    radio.filter("input#allStations").property("checked", true);

    switch( sel.value ) {
      case "lift": //resetLabels(transferLabels);
                   //resetLabels(stationLabels);
                   lines.classed("dim", true);
                   transfers.classed("dim", true);
                   stations.filter(function() {
                     if (stationDataById[splitId(this.id)].maxLiftAmount == 0) { return this.id;};
                   }).classed("dim", true);
                   /*stationLabels[0].forEach(function(label) {
                      // Adding images via coordinates
                     if (stationDataById[splitId(label.id)].maxLiftAmount > 0) {
                       var pos = positionById[splitId(label.id)];
                       d3.select("g#stations").append("image")
                       .attr("x", pos.x).attr("y", function(d) {return pos.y - 15})
                       .attr("width", 24).attr("height", 24)
                       .attr("xlink:href", "data/elevator.png");                            
                     }
                     d3.select("p#selectInfo").html("Станции, оборудованные лифтами, обозначены соответствующим значком")
                   })*/
                   shemaHeader.html(document.querySelector("#infra-select > option:checked").innerHTML)
                    .classed("hidden", false);
                   break;

      case "stairway": lines.classed("dim", true);
                       transfers.classed("dim", true);
                       stations.filter(function() {
                         if (stationDataById[splitId(this.id)].maxStairways == 0) { return this.id;};
                       }).classed("dim", true);
                       shemaHeader.html(document.querySelector("#infra-select > option:checked").innerHTML)
                        .classed("hidden", false);                       
                       break;

      case "pandus": lines.classed("dim", true);
                       transfers.filter(function(d) {
                         var nodeId = transferDataById[splitId(this.id)][0].nodeId;
                         if (nodeDataById[nodeId].pandusAmount == 0) { return this.id;};
                       }).classed("dim", true);
                       stations.filter(function() {
                         var nodeId = stationDataById[splitId(this.id)].nodeId;                      
                         if (nodeDataById[nodeId].pandusAmount == 0) { return this.id;};
                       }).classed("dim", true);
                       shemaHeader.html(document.querySelector("#infra-select > option:checked").innerHTML)
                        .classed("hidden", false);                       
                       break;

      case "friendlyPandus": lines.classed("dim", true);
                             transfers.filter(function() {
                               var nodeId = transferDataById[splitId(this.id)][0].nodeId;
                               if (nodeDataById[nodeId].pandusAvailable == 0) { return this.id;};
                             }).classed("dim", true);
                             stations.filter(function() {
                               var nodeId = stationDataById[splitId(this.id)].nodeId; 
                               if (nodeDataById[nodeId].pandusAvailable == 0) { return this.id;};
                             }).classed("dim", true);
                             shemaHeader.html(document.querySelector("#infra-select > option:checked").innerHTML)
                              .classed("hidden", false);                             
                             break;                      

      case "routesIn": stationLabels.text(function(d) {
                         return "↓" + stationDataById[splitId(this.id)].routesIn;
                       })
                       shemaHeader.html(document.querySelector("#infra-select > option:checked").innerHTML)
                        .classed("hidden", false);
                       break;

      case "routesOut": stationLabels.text(function(d) {
                          return "↑" + stationDataById[splitId(this.id)].routesOut;
                        })
                        shemaHeader.html(document.querySelector("#infra-select > option:checked").innerHTML)
                         .classed("hidden", false);
                        break;

      case "minTaper": stationLabels.text(function(d) {
                         return stationDataById[splitId(this.id)].minTaper;
                       })
                       shemaHeader.html(document.querySelector("#infra-select > option:checked").innerHTML)
                        .classed("hidden", false);
                       break;

      case "avStairs": stationLabels.text(function(d) {
                         return stationDataById[splitId(this.id)].avStairs;
                       })
                       shemaHeader.html(document.querySelector("#infra-select > option:checked").innerHTML)
                        .classed("hidden", false);
                       break;

      case "avRailsStairs": stationLabels.text(function(d) {
                              return stationDataById[splitId(this.id)].avRailsStairs;
                            })
                            shemaHeader.html(document.querySelector("#infra-select > option:checked").innerHTML)
                             .classed("hidden", false);
                            break;

      case "avLiftStairs": stationLabels.text(function(d) {
                             return stationDataById[splitId(this.id)].avLiftStairs;
                           })
                           shemaHeader.html(document.querySelector("#infra-select > option:checked").innerHTML)
                            .classed("hidden", false);
                           break;
      };

      scrollToFocus("metroMap");

  }); // <-- Main programm end


  function metroStat(metroData) {
    nodeInfoData = [];
    targetInfoData = [];
    targetAccessData = [];
    metroData.forEach(function(d) {
      if (d.factor == 'Количество элементов инфраструктуры' ||
          d.factor == 'Количество лифтов - всего' ||
          d.factor == 'Количество узлов, оборудованных лифтами любого уровня' ||
          d.factor == 'Количество пандусов - всего' ||
          d.factor == 'Количество узлов, оборудованных пандусами' ||
          d.factor == 'Количество пандусов, доступных для инвалидов-колясочников' ||
          d.factor == 'Количество узлов, оборудованных пандусами, доступными для инвалидов-колясочников' ||
          d.factor == 'Количество узлов, оборудованных эскалаторами' ||
          d.factor == 'Количество одиночных ступеней') {
        nodeInfoData.push(d);
      } else if (d.factor == 'Количество маршрутов на вход' ||
                 d.factor == 'Количество маршрутов на выход' ||
                 d.factor == 'Количество переходов' ||
                 d.factor == 'Количество узлов, на которых есть переходы') {
        targetInfoData.push(d);
      } else if (d.factor == 'Количество станций с маршрутами, доступными для инвалидов-колясочников' ||
                 d.factor == 'Количество маршрутов, доступных для инвалидов-колясочников' ||
                 d.factor == 'Количество станций с маршрутами, доступными для людей с затруднениями передвижения' ||
                 d.factor == 'Количество маршрутов, доступных для людей с затруднениями передвижения' ||
                 d.factor == 'Количество станций с маршрутами, доступными  для людей с детскими колясками' ||
                 d.factor == 'Количество маршрутов, доступных для людей с детскими колясками') {
        targetAccessData.push(d);
      };
    });

    nodeDescription.html('');
    nodeDescription.classed('hidden', true);
    targetDescription.html("по метро");

    deleteObjects(nodeInfo, "table");
    deleteObjects(targetInfo, "table");
    deleteObjects(targetAccess, "table");    

    tabulate(nodeInfo, nodeInfoData, ['factor', 'value']);
    tabulate(targetInfo, targetInfoData, ['factor', 'value']);
    tabulate(targetAccess, targetAccessData, ['factor', 'value']);
  };

  function splitId(fullId) {
    var id = fullId.split("_");
    return id[1];
  };

  function setStationsRadius(selection, r) {
    return selection.attr("r", r);
  };

  function unDim() {
    d3.selectAll(".dim").classed("dim", false);
  };

  function resetLabels(selection) {
    return selection.text("");
  };

  function deleteObjects(parentNode, object) {
    parentNode.selectAll(object).remove();
  };

  //Adding names for stations
  function namesForStations(labelsList, data, state) {
    labelsList.text(function() {
      if (state) {
        return data[splitId(this.id)].stationName;
      } else {return "";}
    });
  };

  function dropDownListing(dropDownList, data) {
    dropDownList.selectAll("option")
    .data(data)
    .enter()
    .append("option")
      .attr("value", d.value)
      .html(function(d) {return d.name;});

    return dropDownList;
  }

  function listing(parentNode, listType, data) {
    var list = parentNode.append(listType);
    list.selectAll("li")
    .data(data)
    .enter()
    .append("li")
      .html(function(d, i) {return d;});

    return list;
  }

  function tabulate(parentNode, data, columns) {
      var table = parentNode.append("table").attr("class", "tabulated"),
          //thead = table.append("thead"),
          tbody = table.append("tbody");
  
      // append the header row
      /*thead.append("tr")
          .selectAll("th")
          .data(header)
          .enter()
          .append("th")
              .text(function(column) { return column; });*/
  
      // create a row for each object in the data
      var rows = tbody.selectAll("tr")
          .data(data)
          .enter()
          .append("tr");
  
      // create a cell in each row for each column
      var cells = rows.selectAll("td")
          .data(function(row) {
              return columns.map(function(column) {
                  return {column: column, value: row[column]};
              });
          })
          .enter()
          .append("td")
              .text(function(d) { return d.value; });
      
      return table;
  }

  function resetShema() {
    //Reset dim elements
    d3.selectAll(".dim").classed("dim", false);
    //Reset Shema's header
    shemaHeader.html('').classed("hidden", true);
    //Reset station radius
    setStationsRadius(stations, 6.7);
    //Reset labels
    stationLabels.text("");
    transferLabels.text("");
    //Hide all icons
    d3.select("g#stations").selectAll("image").attr("xlink:href", "").classed("hidden", true);
    //Reset zoom and translate
    mainG.attr("transform", "");
    zoom.scale(1);
    zoom.translate([0,0]);
    //return false;
  };

  function resetMenu() {
    //Reset checkbox
    checkbox.property("checked", false);
    //Use filter
    radio.filter("input#allStations").property("checked", true);    
  };

  function resetStat() {
    nodeDescription.html("");
    nodeDescription.classed("hidden", true);
    //targetDescription.html("по метро");
    
    deleteObjects(nodeInfo, "table");
    deleteObjects(targetInfo, "table");
    deleteObjects(targetAccess, "table");
  };

  function scrollToFocus(target) {
    document.getElementById(target).scrollIntoView();
  };

  function zooming() {
    if (zoom.scale() === zoom.scaleExtent()[0]) { zoom.translate([155, 40]);} // make zoom.translate([0, 0]) if minimum scale equla to 1
    mainG.attr("transform", "translate(" + zoom.translate() + ")scale(" + zoom.scale() + ")");
    //svg.attr("transform", "translate(" + zoom.translate() + ")scale(" + zoom.scale() + ")");
  };

  function zoomEvent() {
    var //clicked = d3.event.target,
    //direction = 1,
    factor = 0.2,
    //target_zoom = 1,
    center = [svgWidth / 2, svgHeight / 2],
    extent = zoom.scaleExtent(),
    translate = zoom.translate(),
    currentFocus = [],
    targetFocus = [],
    view = {x: translate[0], y: translate[1], k: zoom.scale()};
  
    //d3.event.preventDefault();
    //console.log(center);
    factor = (this.name === 'zoomIn') ? factor : -factor;
    targetZoom = zoom.scale() + factor;// * (1 + factor);
    //console.log(targetZoom);

    if (targetZoom < extent[0]) {
      targetZoom = extent[0];
    } else if (targetZoom > extent[1]) {
      targetZoom = extent[1];
    };
  
    currentFocus = [(center[0] - view.x) / view.k, (center[1] - view.y) / view.k];
    view.k = targetZoom;
    targetFocus = [currentFocus[0] * view.k + view.x, currentFocus[1] * view.k + view.y];
    view.x += center[0] - targetFocus[0];
    view.y += center[1] - targetFocus[1];

    //interpolateZoom([view.x, view.y], view.k);
    //Default translate for default scale
    if (targetZoom === extent[0]) {
      interpolateZoom([0, 0], view.k);
    } else {
      interpolateZoom([view.x, view.y], view.k);
    };
  };

  function interpolateZoom (translate, scale) {
    //var self = this;
    return d3.transition().duration(750).tween("zoom", function () {
      var iTranslate = d3.interpolate(zoom.translate(), translate),
      iScale = d3.interpolate(zoom.scale(), scale);
      return function (t) {
        zoom
          .scale(iScale(t))
          .translate(iTranslate(t));
        zooming();
      };
    });
  };

};
