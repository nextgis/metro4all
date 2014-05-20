# -*- encoding: utf-8 -*-

# example: python stations.py stations.csv portals.csv

import os
import sys
import math
import pandas as pd
import numpy as np

vocabPath = sys.argv[1]
filePath = sys.argv[2]

vocabDf = pd.read_csv(vocabPath, sep=',', header=0, encoding='utf-8', index_col=0, names=['stationId', 'lineId', 'nodeId', 'stationName', 'stationName_en', 'lon', 'lat', 'lineName'])
#['stationName', 'stationName_en', 'lineName', 'stationId', 'lineId', 'nodeId', 'lat', 'lon']
del vocabDf['lat']
del vocabDf['lon']
sourceDf = pd.read_csv(filePath, sep=',', header=0, encoding='utf-8', names=['id', 'id2', 'station', 'line', 'portalName_ru', 'portalName_en', 'station_id', 'line_id', '0_x', '0_y', 'direction', 'min_width', 'min_steps', 'min_rail_steps', 'lift', 'lift_minus_steps', 'min_lift_steps', 'min_rail_width', 'max_rail_width', 'max_angle', 'max_slope', 'stairways', 'pandusUnavailable', 'wheelchairFriendlyRoutes', 'handicappedFriendlyRoutes', 'luggageFriendlyRoutes', 'Creator', 'Closed', 'Comment'])

''' Calculated columns
sourceDf['max_slope'] = np.tan(np.radians(sourceDf['max_angle'])) * 100
sourceDf['wheelchairFriendlyRoutes'] = ((sourceDf['min_lift_steps'] == 0 ) | (sourceDf['min_rail_steps'] == 0) | (sourceDf['min_rail_steps'] - sourceDf['lift_minus_steps'] <= 0) & (sourceDf['stairways'] == 0) & (sourceDf['min_width'] >= 740) & (sourceDf['max_slope'] <= 15) & (sourceDf['pandusUnavailable'] == 0)) * 1
sourceDf['handicappedFriendlyRoutes'] = ((sourceDf['min_lift_steps'] < 3) | (sourceDf['min_rail_steps'] < 3) | (sourceDf['min_rail_steps'] - sourceDf['lift_minus_steps'] <= 3) & (sourceDf['max_slope'] <= 15)) * 1
sourceDf['luggageFriendlyRoutes'] = ((sourceDf['min_lift_steps'] < 3) | (sourceDf['min_rail_steps'] < 3) | (sourceDf['min_rail_steps'] - sourceDf['lift_minus_steps'] <= 3) & (sourceDf['min_width'] >= 700)) * 1
'''
sourceDf['stairwayAvailableRoutes'] =  (sourceDf['stairways'] > 0) * 1
sourceDf['liftAvailableRoutes'] =  (sourceDf['lift'] > 0) * 1
# stations
# ['id', 'id2', 'station', 'line', 'entrance_name', 'station_id', 'line_id', '0_x', '0_y', 'direction', 'min_width', 'min_steps', 'min_rail_steps', 'lift', 'lift_minus_steps', 'min_lift_steps', 'min_rail_width', 'max_rail_width', 'max_angle', 'max_slope', 'stairways', 'wheelchairFriendlyRoutes', 'handicappedFriendlyRoutes', 'luggageFriendlyRoutes', 'Creator', 'Closed', 'Comment']
# ['stationName', 'stationName_en', 'lineName', 'stationId', 'lineId', 'nodeId', 'lat', 'lon']
# Calculating all stuff

#stationId
#stationName
#lineName
#lineId

routes = pd.pivot_table(sourceDf, values='station', rows='station_id', cols='direction', fill_value=0, aggfunc = 'count')
routesIn = pd.DataFrame({'routesIn' : routes['in'] + routes['both']})
routesOut = pd.DataFrame({'routesOut' : routes['out'] + routes['both']})

minTaper = pd.DataFrame({'minTaper' : sourceDf.groupby(by='station_id')['min_width'].min()})
maxTaper = pd.DataFrame({'maxTaper' : sourceDf.groupby(by='station_id').max()['min_width']})
minStairs = pd.DataFrame({'minStairs' : sourceDf.groupby(by='station_id').min()['min_steps']})
avStairs = pd.DataFrame({'avStairs' : sourceDf.groupby(by='station_id').mean()['min_steps']})
maxStairs = pd.DataFrame({'maxStairs' : sourceDf.groupby(by='station_id').max()['min_steps']})
minRailsStairs = pd.DataFrame({'minRailsStairs' : sourceDf.groupby(by='station_id').min()['min_rail_steps']})
avRailsStairs = pd.DataFrame({'avRailsStairs' : sourceDf.groupby(by='station_id').mean()['min_rail_steps']})
maxRailsStairs = pd.DataFrame({'maxRailsStairs' : sourceDf.groupby(by='station_id').max()['min_rail_steps']})
lift = pd.DataFrame({'lift' : sourceDf.groupby(by='station_id').sum()['lift']})
minLiftAmount = pd.DataFrame({'minLiftAmount' : sourceDf.groupby(by='station_id').min()['lift']})
maxLiftAmount = pd.DataFrame({'maxLiftAmount' : sourceDf.groupby(by='station_id').max()['lift']})
minLiftStairs = pd.DataFrame({'minLiftStairs' : sourceDf.groupby(by='station_id')['min_lift_steps'].min()})
avLiftStairs = pd.DataFrame({'avLiftStairs' : sourceDf.groupby(by='station_id')['min_lift_steps'].mean()})
maxLiftStairs = pd.DataFrame({'maxLiftStairs' : sourceDf.groupby(by='station_id')['min_lift_steps'].max()})
minStairways = pd.DataFrame({'minStairways' : sourceDf.groupby(by='station_id').min()['stairways']})
maxStairways = pd.DataFrame({'maxStairways' : sourceDf.groupby(by='station_id').max()['stairways']})


stairwayAvailableRoutesAll = pd.pivot_table(sourceDf, values='stairwayAvailableRoutes', rows='station_id', cols='direction', fill_value=0, aggfunc = 'sum')
stairwayAvailableRoutesIn = pd.DataFrame({'stairwayAvailableRoutesIn' : stairwayAvailableRoutesAll['in'] + stairwayAvailableRoutesAll['both']})
stairwayAvailableRoutesOut = pd.DataFrame({'stairwayAvailableRoutesOut' : stairwayAvailableRoutesAll['out'] + stairwayAvailableRoutesAll['both']})


liftAvailableRoutesAll = pd.pivot_table(sourceDf, values='liftAvailableRoutes', rows='station_id', cols='direction', fill_value=0, aggfunc = 'sum')
liftAvailableRoutesIn = pd.DataFrame({'liftAvailableRoutesIn' : liftAvailableRoutesAll['in'] + liftAvailableRoutesAll['both']})
liftAvailableRoutesOut = pd.DataFrame({'liftAvailableRoutesOut' : liftAvailableRoutesAll['out'] + liftAvailableRoutesAll['both']})

wheelchairFriendlyRoutesAmount = pd.DataFrame({'wheelchairFriendlyRoutesAmount' : sourceDf.groupby(by='station_id')['wheelchairFriendlyRoutes'].sum()})
wheelchairFriendlyRoutesAll = pd.pivot_table(sourceDf, values='wheelchairFriendlyRoutes', rows='station_id', cols='direction', fill_value=0, aggfunc = 'sum')
wheelchairFriendlyRoutesIn = pd.DataFrame({'wheelchairFriendlyRoutesIn' : wheelchairFriendlyRoutesAll['in'] + wheelchairFriendlyRoutesAll['both']})
wheelchairFriendlyRoutesOut = pd.DataFrame({'wheelchairFriendlyRoutesOut' : wheelchairFriendlyRoutesAll['out'] + wheelchairFriendlyRoutesAll['both']})

handicappedFriendlyRoutesAmount = pd.DataFrame({'handicappedFriendlyRoutesAmount' : sourceDf.groupby(by='station_id')['handicappedFriendlyRoutes'].sum()})
handicappedFriendlyRoutesAll = pd.pivot_table(sourceDf, values='handicappedFriendlyRoutes', rows='station_id', cols='direction', fill_value=0, aggfunc = 'sum')
handicappedFriendlyRoutesIn = pd.DataFrame({'handicappedFriendlyRoutesIn' : handicappedFriendlyRoutesAll['in'] + handicappedFriendlyRoutesAll['both']})
handicappedFriendlyRoutesOut = pd.DataFrame({'handicappedFriendlyRoutesOut' : handicappedFriendlyRoutesAll['out'] + handicappedFriendlyRoutesAll['both']})

luggageFriendlyRoutesAmount = pd.DataFrame({'luggageFriendlyRoutesAmount' : sourceDf.groupby(by='station_id')['luggageFriendlyRoutes'].sum()})
luggageFriendlyRoutesAll = pd.pivot_table(sourceDf, values='luggageFriendlyRoutes', rows='station_id', cols='direction', fill_value=0, aggfunc = 'sum')
luggageFriendlyRoutesIn = pd.DataFrame({'luggageFriendlyRoutesIn' : luggageFriendlyRoutesAll['in'] + luggageFriendlyRoutesAll['both']})
luggageFriendlyRoutesOut = pd.DataFrame({'luggageFriendlyRoutesOut' : luggageFriendlyRoutesAll['out'] + luggageFriendlyRoutesAll['both']})

# Merging all the results
#DataFrame.merge(right, how='inner', on=None, left_on=None, right_on=None, left_index=False, right_index=False, sort=False, suffixes=('_x', '_y'), copy=True)

result = vocabDf.join(routesIn, how='inner', sort=False)
result = result.join(routesOut, how='inner', sort=False)

result = result.join(minTaper, how='inner', sort=False)
result = result.join(maxTaper, how='inner', sort=False)
result = result.join(minStairs, how='inner', sort=False)
result = result.join(np.round(avStairs, decimals=2), how='inner', sort=False)
result = result.join(maxStairs, how='inner', sort=False)
result = result.join(minRailsStairs, how='inner', sort=False)
result = result.join(np.round(avRailsStairs, decimals=2), how='inner', sort=False)
result = result.join(maxRailsStairs, how='inner', sort=False)
result = result.join(lift, how='inner', sort=False)
result = result.join(minLiftAmount, how='inner', sort=False)
result = result.join(maxLiftAmount, how='inner', sort=False)
result = result.join(minLiftStairs, how='inner', sort=False)
result = result.join(np.round(avLiftStairs, decimals=2), how='inner', sort=False)
result = result.join(maxLiftStairs, how='inner', sort=False)
result = result.join(minStairways, how='inner', sort=False)
result = result.join(maxStairways, how='inner', sort=False)

result = result.join(stairwayAvailableRoutesIn, how='inner', sort=False)
result = result.join(stairwayAvailableRoutesOut, how='inner', sort=False)
result['stairwayAvailableRoutesAmount'] = result['stairwayAvailableRoutesIn'] + result['stairwayAvailableRoutesOut']

result = result.join(liftAvailableRoutesIn, how='inner', sort=False)
result = result.join(liftAvailableRoutesOut, how='inner', sort=False)
result['liftAvailableRoutesAmount'] = result['liftAvailableRoutesIn'] + result['liftAvailableRoutesOut']

result['wheelchairFriendlyRoutes'] = (wheelchairFriendlyRoutesAmount > 0) * 1
#result = result.join(wheelchairFriendlyRoutesAmount, how='inner', sort=False)
result = result.join(wheelchairFriendlyRoutesIn, how='inner', sort=False)
result = result.join(wheelchairFriendlyRoutesOut, how='inner', sort=False)
result['wheelchairFriendlyRoutesAmount'] = result['wheelchairFriendlyRoutesIn'] + result['wheelchairFriendlyRoutesOut']
result['handicappedFriendlyRoutes'] = (handicappedFriendlyRoutesAmount > 0) * 1
#result = result.join(handicappedFriendlyRoutesAmount, how='inner', sort=False)
result = result.join(handicappedFriendlyRoutesIn, how='inner', sort=False)
result = result.join(handicappedFriendlyRoutesOut, how='inner', sort=False)
result['handicappedFriendlyRoutesAmount'] = result['handicappedFriendlyRoutesIn'] + result['handicappedFriendlyRoutesOut']
result['luggageFriendlyRoutes'] = (luggageFriendlyRoutesAmount > 0) * 1
#result = result.join(luggageFriendlyRoutesAmount, how='inner', sort=False)
result = result.join(luggageFriendlyRoutesIn, how='inner', sort=False)
result = result.join(luggageFriendlyRoutesOut, how='inner', sort=False)
result['luggageFriendlyRoutesAmount'] = result['luggageFriendlyRoutesIn'] + result['luggageFriendlyRoutesOut']

resultPath = os.path.join(os.path.dirname(filePath), 'stationsReport.csv')
result.to_csv(resultPath, encoding='utf-8', index_label='stationId')
