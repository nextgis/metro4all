L.Control.Extent = L.Control.extend({
    options: {
        position: 'topright',
        maxWidth: 100
    },

    onAdd: function (map) {
        var className = 'leaflet-control-extent',
            container = L.DomUtil.create('div', className),
            extentElement;

        L.DomEvent
            .on(container, 'click', L.DomEvent.stop)
            .on(container, 'click', this._extent, this);

        L.DomEvent
            .on(container, 'dblclick', L.DomEvent.stop)
            .on(container, 'dblclick', this._extent, this);

        extentElement = L.DomUtil.create('div', 'extent-earth', container);
        extentElement.setAttribute('title', m4a.resources.map.extent);
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