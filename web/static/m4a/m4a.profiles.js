(function ($, m4a) {
    m4a.profiles = {};

    $.extend(m4a.viewmodel, {
        profile: {
            name: null,
            values: {
                current: null,
                selected: null
            }
        }
    });

    $.extend(m4a.profiles, {
        view: {
            $profilesContainer: null,
            $profileDescription: null,
            $commandControls: null
        },


        init: function () {
            this.setDomOptions();
            this.bindEvents();
            this.setDefaultProfile();
        },


        setDomOptions: function () {
            this.view.$profilesContainer = $('#mainform div.profiles');
            this.view.$profileDescription = $('div.profile-descr');
        },


        bindEvents: function () {
            var context = this;

            this.view.$profilesContainer.find('label.profile').off('click').on('click', function () {
                var $this = $(this),
                    profile = $this.data('profile'),
                    type = $this.data('type');
                m4a.viewmodel.profile.name = profile;
                context.buildProfileControl(profile, type, $this);
                m4a.stations.updateLocalPortals();
                $('.pagination li.active').first().trigger('click', [true]);
            });
        },

        profileControls: {
            man: {
                description: m4a.resources.profiles.man_d
            },
            wheelchair: {
                html: '<input type="input" class="profile-value" value="60" title="' + m4a.resources.profiles.wch_html + '">' +
                    '<label class="btn btn-default apply command" data-action="apply" title="Применить новое значение"><input type="radio" name="options"></label>' +
                    '<label class="btn btn-default undo command" data-action="undo" title="Сбросить новое значение"><input type="radio" name="options"></label>',
                description: function (value) {
                    if (value === '') {
                        return m4a.resources.profiles.wch_d;
                    }
                    return m4a.resources.profiles.wch_d2 + value + m4a.resources.profiles.wch_d3;
                },
                values: {current: 60, selected: 60}
            },
            trolley: {
                html: '<input type="input" class="profile-value" value="60" title="' + m4a.resources.profiles.trl_html + '">' +
                    '<label class="btn btn-default apply command" data-action="apply" title="Применить новое значение"><input type="radio" name="options"></label>' +
                    '<label class="btn btn-default undo command" data-action="undo"  title="Сбросить новое значение"><input type="radio" name="options"></label>',
                description: function (value) {
                    if (value === '') {
                        return m4a.resources.profiles.trl_d;
                    }
                    return m4a.resources.profiles.trl_d2 + value + m4a.resources.profiles.trl_d3;
                },
                values: {current: 60, selected: 60}
            }
        },

        barriersIndicatorsByProfile: {
            man: {
                visible: ['escal', 'lift', 'min_step'],
                hidden: ['max_width', 'retrench_steps', 'min_step_ramp-lift_minus_step', 'step_for_walking', 'min_max_rail_width', 'max_angle']
            },
            wheelchair: {
                visible: ['max_width', 'escal', 'lift', 'retrench_steps', 'min_step_ramp-lift_minus_step', 'step_for_walking', 'min_max_rail_width', 'max_angle'],
                hidden: ['min_step']
            },
            trolley: {
                visible: ['max_width', 'escal', 'lift', 'retrench_steps', 'step_for_walking', 'min_max_rail_width'],
                hidden: ['min_step', 'max_angle']
            }
        },


        profileBarriersRestrictions: {
            man: {
            },
            wheelchair: {
                max_width: function (barriers) {
                    return barriers.max_width ?
                        barriers.max_width >= m4a.viewmodel.profile.values.selected :
                        true;
                }
            },
            trolley: {
                min_max_rail_width: function (barriers) {
                    if (barriers.max_rail_width && barriers.min_rail_width) {
                        return ( barriers.max_rail_width >= m4a.viewmodel.profile.values.selected) &&
                            (barriers.min_rail_width <= m4a.viewmodel.profile.values.selected);
                    }
                    return true;
                },
                max_width: function (barriers) {
                    return barriers.max_width ?
                        barriers.max_width >= m4a.viewmodel.profile.values.selected :
                        true;
                }
            }
        },

        lastProfileControl: null,
        intRegex: /^\d+$/,

        buildProfileControl: function (profile, type, $element) {
            var context = this,
                intValue;

            if (this.lastProfileControl) {
                this.lastProfileControl.remove();
                if (this.view.$commandButtons) {
                    this.view.$commandButtons.remove();
                }
                this.lastProfileControl = null;
            }
            if (this.profileControls[profile].html) {
                $element.after($(this.profileControls[profile].html));
                m4a.viewmodel.profile.values = $.extend(true, {}, this.profileControls[profile].values);
                this.view.$commandButtons = $element.parent().find('label.command');
            }
            if (type === 'input') {
                this.lastProfileControl = $element.parent().find('input.profile-value');
                this.lastProfileControl.off('input').on('input', function () {
                    if (!context.intRegex.test(this.value) && this.value !== '') {
                        this.value = m4a.viewmodel.profile.values.current;
                        return false;
                    } else {
                        intValue = parseInt(this.value);
                        if (intValue === m4a.viewmodel.profile.values.selected) {
                            context.view.$commandButtons.removeClass('active');
                        } else {
                            context.view.$commandButtons.addClass('active');
                            m4a.viewmodel.profile.values.current = intValue;
                        }
                    }
                });

                this.bindCommandEvents();
                this.updateDescription(this.profileControls[m4a.viewmodel.profile.name].description(m4a.viewmodel.profile.values.selected));
            } else {
                this.updateDescription(this.profileControls[profile].description)
            }
        },


        updateDescription: function (value) {
            this.view.$profileDescription.text(value);
        },


        setDefaultProfile: function () {
            this.selectProfile('man');
        },


        selectProfile: function (profile) {
            $('#profile_' + profile).click();
        },


        bindCommandEvents: function () {
            var context = this,
                actionName;

            this.view.$commandButtons.off('click').on('click', function (e) {
                actionName = $(this).data('action');
                if (context[actionName + 'ActionHandler']) {
                    context[actionName + 'ActionHandler'](e);
                }
            });
        },

        applyActionHandler: function (e) {
            e.stopPropagation();
            var values = m4a.viewmodel.profile.values;
            this.lastProfileControl.value = values.current;
            values.selected = values.current;
            this.view.$commandButtons.removeClass('active');
            this.updateDescription(this.profileControls[m4a.viewmodel.profile.name].description(values.selected));

            m4a.stations.updateLocalPortals();
            $('.pagination li.active').first().trigger('click', [true]);
        },

        undoActionHandler: function (e) {
            e.stopPropagation();
            this.lastProfileControl[0].value = m4a.viewmodel.profile.values.selected;
            this.lastProfileControl.current = m4a.viewmodel.profile.values.selected;
            this.view.$commandButtons.removeClass('active');
        },

        validateStation: function (station) {
            var barriersParameters = station.properties.barriers,
                profileName = m4a.viewmodel.profile.name,
                indicatorsRestrictions = m4a.profiles.profileBarriersRestrictions[profileName];

            if (profileName === 'man') {
                return true;
            }

            for (var indicator in indicatorsRestrictions) {
                if (indicatorsRestrictions.hasOwnProperty(indicator)) {
                    if (!indicatorsRestrictions[indicator](barriersParameters)) {
                        return false;
                    }
                }
            }
            return true;
        }
    })
})(jQuery, m4a)