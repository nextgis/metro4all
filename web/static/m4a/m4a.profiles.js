(function ($, m4a) {
    m4a.profiles = {};

    $.extend(m4a.viewmodel, {
        profile: {
            name: null,
            values: null
        }
    });

    $.extend(m4a.profiles, {
        view: {
            $profilesContainer: null,
            $profileDescription: null
        },


        init: function() {
            this.setDomOptions();
            this.bindEvents();
            this.setDefaultProfile();
        },


        setDomOptions: function() {
            this.view.$profilesContainer = $('#mainform div.profiles');
            this.view.$profileDescription = $('div.profile-descr');
        },


        bindEvents: function() {
            var context = this;

            this.view.$profilesContainer.find('label.profile').off('click').on('click', function() {
                var $this = $(this),
                    profile = $this.data('profile'),
                    type = $this.data('type');
                m4a.url.updateUrl('profile', profile);
                m4a.viewmodel.profile.name = profile;
                m4a.viewmodel.profile.values = null;
                context.buildProfileControl(profile, type, $this);
                m4a.stations.updateLocalPortals();
            });
        },

        profileControls: {
            man: {
                description: 'Я просто иду'
            },
            wheelchair: {
                html: '<input type="input" value="60" title="Ширина коляски в см">',
                description: function (value) {return 'Я на коляске шириной ' + value + ' см'},
                values: {width: 0}
            },
            trolley : {
                html: '<input type="input" value="60" title="Ширина тележки в см">',
                description: function (value) {return 'Я c тележкой шириной ' + value + ' см'},
                values: {width: 0}
            }
        },

        lastProfileControl: null,
        intRegex: /^\d+$/,

        buildProfileControl: function(profile, type, $element) {
            var context = this;

            if (this.lastProfileControl) {
                this.lastProfileControl.remove();
                this.lastProfileControl = null;
            }
            if (this.profileControls[profile].html) {
                this.lastProfileControl = $(this.profileControls[profile].html);
                $element.after(this.lastProfileControl);
                m4a.viewmodel.profile.values = this.profileControls[profile].values;
            }
            if (type === 'input') {
                this.lastProfileControl.off('input').on('input', function() {
                    if (!context.intRegex.test(this.value) && this.value !== '') {
                        this.value = m4a.viewmodel.profile.values.width;
                        return false;
                    }
                    context.updateDescription(context.profileControls[profile].description(this.value));
                    m4a.viewmodel.profile.values.width = this.value;
                    m4a.stations.updateLocalPortals();
                });
                this.lastProfileControl.trigger('input');
            } else {
                this.updateDescription(this.profileControls[profile].description)
            }
        },


        updateDescription: function(value) {
            this.view.$profileDescription.text(value);
        },


        setDefaultProfile: function() {
            this.selectProfile('man');
        },


        selectProfile: function(profile) {
            $('#profile_' + profile).click();
        },


        validateStation: function(station) {
            if (m4a.viewmodel.profile.name === 'man') {
                return true;
            }

            var barriersParameters = station.properties.barriers;

            if (m4a.viewmodel.profile.name === 'wheelchair') {
                if (barriersParameters.max_width) {
                    return m4a.viewmodel.profile.values.width <= barriersParameters.max_width;
                }
                return true;
            }

            if (m4a.viewmodel.profile.name === 'trolley') {
                if (barriersParameters.min_rail_width && barriersParameters.max_rail_width) {
                    return (m4a.viewmodel.profile.values.width <= barriersParameters.max_rail_width) &&
                        (m4a.viewmodel.profile.values.width >= barriersParameters.min_rail_width);
                }
                if (barriersParameters.min_step && barriersParameters.min_step_ramp) {
                    return (barriersParameters.min_step > 0) &&
                        (barriersParameters.min_step_ramp < barriersParameters.min_step);
                }
                return true;
            }
        }
    })
}) (jQuery, m4a)