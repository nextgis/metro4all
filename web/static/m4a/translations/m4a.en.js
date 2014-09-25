(function ($, m4a) {
    m4a.resources = {};

    $.extend(m4a.resources, {
        profiles: {
            "man_d": "No limitations",
            "wch_html": "Wheelchair width, cm",
            "wch_d": "I'm in a wheelchair",
            "wch_d2": "I'm in a ",
            "wch_d3": " cm wide wheelchair",
            "trl_html": "Stroller width, cm",
            "trl_d": "I am with a stroller",
            "trl_d2": "I am with a ",
            "trl_d3": " cm wide stroller"
        },
        routes: {
            "wch_w": "Min passway width",
            "wch_w1": "",
            "wch_w2": " cm",
            "n_str": "No stairs",
            "stps": "Steps total",
            "n_ramp": "Out of those, not covered by ramps or rails",
            "elev_y": "Elevator: yes",
            "elev_n": "No elevator",
            "elev_y_1": ", saves ",
            "elev_y_2": " steps",
            "min_max": "Distance between wheels",
            "cm": " cm",
            "no_r": "No rails",
            "slope": "Max slope",
            "no_lev_surf": "No leveled surfaces",
            "entr": "Entrance",
            "obt_arent_sh_en": "Obstacles aren't shown as the entrance is not selected",
            "exit": "Exit",
            "obt_arent_sh_ex": "Obstacles aren't shown as the exit is not selected",
            "show": "More...",
            "hide": "Hide",
            "help": "Looking for official assistance service?",
            "help_link": "http://metro4all.org/official-assistance/",
            "from": "From",
            "to": "To"
        },
        inline: {
            "start_st": "Please select starting or destination station",
            "eq_st": "From station is equal to destination station",
            "st_st": "Choose start station",
            "end_st": "Choose destination station"
        },
        map: {
            "extent": "Show the whole city"
        }
    })
})(jQuery, m4a)
