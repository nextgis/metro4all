## v. 2.7 (01.03.2015)
+ Added mobility requirements official help service info
+ Added additional mobility requirements info visibility
+ Fixed sometimes black controls appearance in toolbar
+ Bugfixing

+ UI changes:
	* Ascending city sort in select curent city dialog
	* Added location accuracy dialog to map screen
	* Added mobility requirements menu item to each screen
	* City autoexpanded in select station screen if there was selected one

## v. 2.6 (26.01.2015)
+ Added hints to show app features for users
+ Added meetcodes-strip to layout screen
+ Added meetcodes to map instead icons
+ Code refactoring

+ UI changes:
	* New design
	* Popup menu for stations on select station screen
	* Separate Limitations screen
	* City selecting moved to settings

+ Bugfixing:
	* Layout sometimes was not shown on API <=10
	* Recent stations now work correct
	* App crash from <2.4 update

## v. 2.5.1 (16.01.2015)
* Improved icons quality and markers position on map screen
* Fixed app crash on bringing foreground
* Fixed app crash on map screen for stations that doesn't have portals
* Fixed schemes visibility on some devices

## v. 2.5 (12.01.2015)
+ Added layout button to From / To panes on main screen
+ Added map button for source and destination stations on routing screen
+ Added acknowledgements dialog on about screen
+ All entrances and exits in all cities now have a meetcode (a number which can be used to explain a route, arrange a meeting, etc.)
+ Other small improvements
+ Code refactoring

+ UI changes:
	* Locate closest entrance moved to actionbar
	* A/B marker icons removed, added metro and arrow icon on main screen
	* Icons design changed - now they are flat
	* Items on select station screen redesigned
	* Items on routing screen redesigned
	* Limitations pane iconized and moved to bottom
	* Limitations info on select station screen is hidden when it's disabled
	
+ Bugfixing:
	* App crash on inflating preferences below Android 4.0
	* Search button stayed active on changing city
	* Selected station stays selected after select station screen closed and nothing was selected
	* Rare app crash when station was deselected

## v.2.4 (29.12.2014)
+ UI changes:
	* Added cross-reference layout/map buttons in map/layout screens
	* Added "locate nearest entrance" button to main screen
	* Added map buttons for From/To stations to main screen 
	* Added line-coloured icon for From/To stations to main screen
	* Added space for entrance/exit description to main screen
	
+ Bugfixes:
	* Cities ascending sorting in main screen menu list and downloading dialog
	* Cities are deselected in the first downloading dialog 
	* Percentage in download dialog shows remaining/total size in Kb
	* Fixed stations name filter - now it accepts CAPITALS
	* Fixed small pictures in layout screen - now it fits height/width

## v.2.3
* Belarussian language for interface and support of Belarussian language for data
* added information about number of escalators on the route
* many fixes for Moscow (layouts reworked, normalized exit names)
* complete overhaul of St. Petersburg data (new full set of layouts)
* changes in UI - city can be selected from the menu at the screen top, station layouts are now available while choosing entrances-exits

## v.2.2 
* legend for station layouts is now available 
* users can locate themselves on the map by pressing the button and see the nearest metro entrances
* map and portal icons are now displayed according to pixel density of the screen and system setup "Font Size" (Settings - Display)
* "zero coordinates" bug fixed
 
## v.2.1 
* it is now possible to choose entrances/exits on the map
* autodetection of location and movement direction on the map
* highlighting entrances that conflict with limitations (if set)
* there is now more space in station selector
* bug fixing
 
## v.2.0
* user interface is updated
* new cities (Warsaw, Minsk) are added
* bug fixing
  
## v.1.6
* list filtering using keyboard is added

## v.1.5
* sequential download of data for different cities is added

## v.1.4
* bug with activation/deactivation of search button when wrong parameters are entered is fixed

## v.1.3
* fix switching between cities

## v.1.2
* fix icon
* add bigger resolution support for tablets
