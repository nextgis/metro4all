L.PolylineOffset = {

  pointsToJSTSCoordinates: function(pts) {
    var coords = [];
    for(var i=0, l=pts.length; i<l; i++) {
      coords[i] = new jsts.geom.Coordinate(pts[i].x, pts[i].y);
    }
    return coords;
  },

  JSTSCoordinatesToPoints: function(coords) {
    var pts = [];
    for(var i=0, l=coords.length; i<l; i++) {
      pts[i] = L.point(coords[i].x, coords[i].y);
    }
    return pts;
  },

  offsetPoints: function(pts, offset) {
    var offsetPolyline,
        ls = new jsts.geom.LineString(this.pointsToJSTSCoordinates(pts));

    if (offset != 0) {
      // Parameters which describe how a buffer should be constructed
      var bufferParameters = new jsts.operation.buffer.BufferParameters();

      // Sets whether the computed buffer should be single-sided
      bufferParameters.setSingleSided(true);

      var precisionModel = new jsts.geom.PrecisionModel();
      var offsetCurveBuilder = new jsts.operation.buffer.OffsetCurveBuilder(precisionModel, bufferParameters);

      var offsetCurve = offsetCurveBuilder.getOffsetCurve(ls.points, offset);
      var offsetBuffer = jsts.operation.buffer.BufferOp.bufferOp2(ls, offset, bufferParameters);

      var offsetPointsList = [];
      for (var i=0, l=offsetCurve.length; i<l; i++) {
        var offsetCurveNode = new jsts.geom.Point(offsetCurve[i]);
        if (offsetBuffer.touches(offsetCurveNode)) {
          var offsetPoint = offsetCurve[i];
          if (!(isNaN(offsetPoint.x) || isNaN(offsetPoint.y))) {
            offsetPointsList.push(offsetPoint);
          }
        }
      }

      offsetPolyline = offsetPointsList;

    } else {
      offsetPolyline = ls.points;
    }

    return this.JSTSCoordinatesToPoints(offsetPolyline);
  }

}

// Modify the L.Polyline class by overwriting the projection function,
// to add offset related code
// Versions < 0.8
if(L.version.charAt(0) == '0' && parseInt(L.version.charAt(2)) < 8) {
  L.Polyline.include({
    projectLatlngs: function() {
      this._originalPoints = [];

      for (var i = 0, len = this._latlngs.length; i < len; i++) {
        this._originalPoints[i] = this._map.latLngToLayerPoint(this._latlngs[i]);
      }
      // Offset management hack ---
      if(this.options.offset) {
        this._originalPoints = L.PolylineOffset.offsetPoints(this._originalPoints, this.options.offset);
      }
      // Offset management hack END ---
    }
  });
} else {
// Versions >= 0.8
  L.Polyline.include({
    _projectLatlngs: function (latlngs, result) {
      var flat = latlngs[0] instanceof L.LatLng,
          len = latlngs.length,
          i, ring;

      if (flat) {
        ring = [];
        for (i = 0; i < len; i++) {
          ring[i] = this._map.latLngToLayerPoint(latlngs[i]);
        }
        // Offset management hack ---
        if(this.options.offset) {
          ring = L.PolylineOffset.offsetPoints(ring, this.options.offset);
        }
        // Offset management hack END ---
        result.push(ring);
      } else {
        for (i = 0; i < len; i++) {
          this._projectLatlngs(latlngs[i], result);
        }
      }
    }
  });
}

L.Polyline.include({
  setOffset: function(offset) {
    this.options.offset = offset;
    this.redraw();
    return this;
  }
});
