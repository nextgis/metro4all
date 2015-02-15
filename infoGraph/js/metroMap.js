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

  window.onhashchange = function() {
 	var stationId = hash.get('station');
 		transferId = hash.get('transfer');
 	if (typeof stationId != 'undefined') { composeStationReport(stationId); }
 	else if (typeof transferId != 'undefined') { composeTransferReport(transferId); };
 };

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

  metroData.forEach(function(d) {
    d.factor = tr.metroStatHeader[d.factor];
  });

  stationsData.forEach(function(d) {
    stationDataById[d.stationId] = {
      lineId: parseInt(d.lineId),
      nodeId: parseInt(d.nodeId),
      stationName: d.stationName,
      stationName_en: d.stationName_en,
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
      nodeName_en: d.nodeName_en,
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
      fromStation_en: d.fromStation_en,
      toStation_en: d.toStation_en,      
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
    hash.clear();
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
                           shemaHeader.html(tr.shemaHeader.wheelchairFriendly).classed("hidden", false);
                           break;

      case "handicappedFriendly": lines.classed("dim", true);
                            transfers.classed("dim", true);
                            stations.filter(function() {
                              if (stationDataById[splitId(this.id)].handicappedFriendlyRoutes == 0) {return this.id;}
                            }).classed("dim", true);
                            stationIcons.filter(function() {
                              if (stationDataById[splitId(this.id)].handicappedFriendlyRoutes != 0) {return this.id;}
                            }).attr("xlink:href", "img/aged_icon.svg").classed("hidden", false);
                            shemaHeader.html(tr.shemaHeader.handicappedFriendly).classed("hidden", false);
                            break;

      case "luggageFriendly": lines.classed("dim", true);
                        transfers.classed("dim", true);
                        stations.filter(function() {
                          if (stationDataById[splitId(this.id)].luggageFriendlyRoutes == 0) {return this.id;}
                        }).classed("dim", true);
                        stationIcons.filter(function() {
                          if (stationDataById[splitId(this.id)].luggageFriendlyRoutes != 0) {return this.id;}
                        }).attr("xlink:href", "img/luggage_icon.svg").classed("hidden", false);
                        shemaHeader.html(tr.shemaHeader.luggageFriendly).classed("hidden", false);
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
    t.append("span").text(function() {
      return tr.language == 'eng' ? stationDataById[id].stationName_en : stationDataById[id].stationName;
    })
    //console.log(d3.event.pageX);
  })
  .on("mouseout", function() {
    stationTooltip.style("display", "none");
  })
  .on("click", function() {
  	var stationId = splitId(this.id);
  	//Change hash
  	hash.clear();
  	hash.add({station: stationId});  	
  	composeStationReport(stationId);
  });

  function composeStationReport(stationId) {
    //var targetInfo = d3.select("div#targetInfo");
    //var nodeInfo = d3.select("div#nodeInfo");
    nodeInfoData = [];
    targetInfoData = [];
    targetAccessData = [];

    //Routes by stations (station's data)
    var stationData = stationDataById[stationId];
    var tableData = [];
    tableData.push(
      {factor: tr.stationHeader.routesIn, value: stationData.routesIn},
      {factor: tr.stationHeader.routesOut, value: stationData.routesOut},
      {factor: tr.stationHeader.minTaper, value: stationData.minTaper},
      {factor: tr.stationHeader.minStairways, value: stationData.minStairways},
      {factor: tr.stationHeader.maxLiftAmount, value: stationData.maxLiftAmount},
      {factor: tr.stationHeader.minStairs, value: stationData.minStairs},
      {factor: tr.stationHeader.avStairs, value: stationData.avStairs},
      {factor: tr.stationHeader.maxStairs, value: stationData.maxStairs},
      {factor: tr.stationHeader.minRailsStairs, value: stationData.minRailsStairs},
      {factor: tr.stationHeader.avRailsStairs, value: stationData.avRailsStairs},
      {factor: tr.stationHeader.maxRailsStairs, value: stationData.maxRailsStairs}
    );

    if (stationData.maxLiftAmount > 0) {
      tableData.push(
        {factor: tr.stationHeader.minLiftStairs, value: stationData.minLiftStairs},
        {factor: tr.stationHeader.avLiftStairs, value: stationData.avLiftStairs},
        {factor: tr.stationHeader.maxLiftStairs, value: stationData.maxLiftStairs}
      );      
    };  

    targetInfoData = tableData;
    targetDescription.html(tr.target.station +  "‹‹" + (tr.language == 'eng' ? stationData.stationName_en : stationData.stationName) + "››");

    //Infrastructure by stations (node's data)
    var nodeData = nodeDataById[stationData.nodeId];
    var tableData = [];
    tableData.push(
      {factor: tr.nodeHeader.totalElements, value: nodeData.totalElements},
      {factor: tr.nodeHeader.stairwaysAmount, value: nodeData.stairwaysAmount},
      {factor: tr.nodeHeader.doorsAndTapersAmount, value: nodeData.doorsAndTapersAmount},
      {factor: tr.nodeHeader.minDoorAndTaperWidth, value: nodeData.minDoorAndTaperWidth},
      {factor: tr.nodeHeader.turnstilesAmount, value: nodeData.turnstilesAmount},
      {factor: tr.nodeHeader.minturnstileWidth, value: nodeData.minturnstileWidth}
    );

    if (nodeData.liftAmount > 0) {
      tableData.push(
        {factor: tr.nodeHeader.liftAmount, value: nodeData.liftAmount},
        {factor: tr.nodeHeader.hallLevelLiftsAmount, value: nodeData.hallLevelLiftsAmount},
        {factor: tr.nodeHeader.trainLevelLiftsAmount, value: nodeData.trainLevelLiftsAmount},
        {factor: tr.nodeHeader.surfaceLevelLiftsAmount, value: nodeData.surfaceLevelLiftsAmount},
        {factor: tr.nodeHeader.minLiftWidth, value: nodeData.minLiftWidth}
      );      
    };

    if (nodeData.pandusAmount > 0) {
      tableData.push(
        {factor: tr.nodeHeader.pandusAmount, value: nodeData.pandusAmount},
        {factor: tr.nodeHeader.pandusMaxSlope, value: nodeData.pandusMaxSlope},
        {factor: tr.nodeHeader.pandusAvailableAmount, value: nodeData.pandusAvailableAmount}
      );      
    };

    if (nodeData.stairsAmount > 0) {
      tableData.push(
        {factor: tr.nodeHeader.stairsAmount, value: nodeData.stairsAmount},
        {factor: tr.nodeHeader.noRailingAmount, value: nodeData.noRailingAmount},
        {factor: tr.nodeHeader.coupleStairsAmount, value: nodeData.coupleStairsAmount}
      );      
    };
      
    if (nodeData.railsStairsAmount > 0) {
      tableData.push(
        {factor: tr.nodeHeader.railsStairsAmount, value: nodeData.railsStairsAmount},
        {factor: tr.nodeHeader.minRailsWidth, value: nodeData.minRailsWidth},
        {factor: tr.nodeHeader.maxRailsWidth, value: nodeData.maxRailsWidth},
        {factor: tr.nodeHeader.maxRailsSlope, value: nodeData.maxRailsSlope}
      );      
    };

    nodeInfoData = tableData;

    nodeDescription.html(tr.target.node + "‹‹" + (tr.language == 'eng' ? nodeData.nodeName_en : nodeData.nodeName) + "››");
    nodeDescription.classed('hidden', false);

    //Accessibility by stations (station's data)
    var tableData = [];
    tableData.push(
      {factor: tr.accessibilityHeader.wheelchairFriendlyRoutesIn, value: stationData.wheelchairFriendlyRoutesIn},
      {factor: tr.accessibilityHeader.wheelchairFriendlyRoutesOut, value: stationData.wheelchairFriendlyRoutesOut},
      {factor: tr.accessibilityHeader.handicappedFriendlyRoutesIn, value: stationData.handicappedFriendlyRoutesIn},
      {factor: tr.accessibilityHeader.handicappedFriendlyRoutesOut, value: stationData.handicappedFriendlyRoutesOut},
      {factor: tr.accessibilityHeader.luggageFriendlyRoutesIn, value: stationData.luggageFriendlyRoutesIn},
      {factor: tr.accessibilityHeader.luggageFriendlyRoutesOut, value: stationData.luggageFriendlyRoutesOut}
    );

    targetAccessData = tableData;

    deleteObjects(nodeInfo, "table");
    deleteObjects(targetInfo, "table");
    deleteObjects(targetAccess, "table");

    tabulate(nodeInfo, nodeInfoData, ['factor', 'value']);
    tabulate(targetInfo, targetInfoData, ['factor', 'value']);
    tabulate(targetAccess, targetAccessData, ['factor', 'value']);

    scrollToFocus("infoPanel");
  };

  //Transfers (rectangles) events
  transfers.on("click", function() {
  	var transferId = splitId(this.id);
  	//Change hash
  	hash.clear();
  	hash.add({transfer: transferId});   	
  	composeTransferReport(transferId);
  });

  function composeTransferReport(transferId) {
    //var targetInfo = d3.select("div#targetInfo");
    //var nodeInfo = d3.select("div#nodeInfo");
    nodeInfoData = [];
    targetInfoData = [];
    targetAccessData = [];
    //Routes by transfers (transfer's data)
    var transferData = transferDataById[transferId];
    //console.log(transferData);    
    var tableData = [
      {factor: tr.transferHeader.minWidth, value: transferData[0].minWidth},
      {factor: tr.transferHeader.minStairs, value: transferData[0].minStairs},
      {factor: tr.transferHeader.minRailsStairs, value: transferData[0].minRailsStairs}
    ];

    if (transfersData.lift > 0) {
      tableData.push({factor: tr.transferHeader.minLiftStairs, value: transferData.minLiftStairs});
    };

    targetInfoData = tableData;

    targetDescription.html(function() {
      if (tr.language == 'eng') {
        return tr.target.transfer + "‹‹" + transferData[0].fromStation_en + " - " + transferData[0].toStation_en + "››";
      } else {
        return tr.target.transfer + "‹‹" + transferData[0].fromStation + " - " + transferData[0].toStation + "››";
      }
    });

    //Infrastructure by transfers (node's data)
    var nodeData = nodeDataById[transferData[0].nodeId];
    var tableData = [
      {factor: tr.transferHeader.stairwaysAmount, value: nodeData.stairwaysAmount},
      {factor: tr.transferHeader.liftAmount, value: nodeData.liftAmount}
      ];

    nodeInfoData = tableData;

    nodeDescription.html(tr.target.node + "‹‹" + (tr.language == 'eng' ? nodeData.nodeName_en : nodeData.nodeName) + "››");
    nodeDescription.classed('hidden', false);

    //Accessibility by transfers (transfer's data)
    var tableData = [];
    tableData.push(
      {factor: tr.transferHeader.wheelchairFriendlyRoutes, value: transferData[0].wheelchairFriendlyRoutes == 0 ? tr.booleanWords.no : tr.booleanWords.yes},
      {factor: tr.transferHeader.handicappedFriendlyRoutes, value: transferData[0].handicappedFriendlyRoutes == 0 ? tr.booleanWords.no : tr.booleanWords.yes},
      {factor: tr.transferHeader.luggageFriendlyRoutes, value: transferData[0].luggageFriendlyRoutes == 0 ? tr.booleanWords.no : tr.booleanWords.yes}
    );

    targetAccessData = tableData;

    deleteObjects(nodeInfo, "table");
    deleteObjects(targetInfo, "table");
    deleteObjects(targetAccess, "table");

    tabulate(nodeInfo, nodeInfoData, ['factor', 'value']);
    tabulate(targetInfo, targetInfoData, ['factor', 'value']);
    tabulate(targetAccess, targetAccessData, ['factor', 'value']);

    scrollToFocus("infoPanel");
  };


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
      switch(d.type) {
        case "nodeInfo": nodeInfoData.push(d); break;
        case "targetInfo": targetInfoData.push(d); break;
        case "targetAccess": targetAccessData.push(d); break;
      }
    });

    nodeDescription.html('');
    nodeDescription.classed('hidden', true);
    targetDescription.html(tr.target.metro);

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
