Metro4all: infographics
======

How to update data
------

1. Download up to date source files from google docs
2. Install [pandas](http://pandas.pydata.org) and all dependencies from [SciPy Stack](http://www.scipy.org):
  - `sudo apt-get install python-numpy python-scipy python-matplotlib ipython ipython-notebook python-pandas`
3. Use python scripts from `metro4all/utils` for recalculating statistics:
  - `python nodes.py nodes.csv elements.csv`
  - `python stations.py stations.csv portals.csv`
  - `python transfers.py interchanges.csv stations.csv`
  - `python metro.py nodesReport.csv stationsReport.csv transfersReport.csv`
  
How to update SVG schema
------

1. Edit `metro_source.svg` with ***Inkscape*** or other vector image editor
2. Use [SVGO](https://github.com/svg/svgo) for optimizing: `svgo --config=svgo_cfg.yml -i metro_source.svg -o metro_WebOptimized.svg`
