L.Control.Extent = L.Control.extend({
    options: {
        position: 'topright',
        maxWidth: 100
    },

    onAdd: function (map) {
        var className = 'leaflet-control-extent',
            container = L.DomUtil.create('div', className);

        L.DomEvent
            .on(container, 'mousedown dblclick', L.DomEvent.stopPropagation)
            .on(container, 'click', L.DomEvent.stop)
            .on(container, 'click', this._extent, this);

        L.DomUtil.create('div', 'extent-earth', container);
        return container;
    },

    onRemove: function (map) {

    },

    _extent: function () {
        m4a.viewmodel.mainMap.setView(global_config.mainmap.center, global_config.mainmap.zoom)
    }
});

L.control.scale = function (options) {
    return new L.Control.Extent(options);
};