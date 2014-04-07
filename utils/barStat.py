# -*- encoding: utf-8 -*-

# example: python barStat.py stationsReport.csv transfersReport.csv

import os
import sys
import math
import pandas as pd
import numpy as np
 
filePath = sys.argv[1]
filePath2 = sys.argv[2]

stationsReportDf = pd.read_csv(filePath, sep=',', encoding='utf-8')
''' 
stationsDf = pd.read_csv(filePath2, sep=',', header=0, encoding='utf-8', names=['id', 'id2', 'station', 'line', 'entrance_name', 'station_id', 'line_id', '0_x', '0_y', 'direction', 'min_width', 'min_steps', 'min_rail_steps', 'lift', 'lift_minus_steps', 'min_lift_steps', 'min_rail_width', 'max_rail_width', 'max_angle', 'max_slope', 'stairways', 'pandusUnavailable', 'wheelchairFriendlyRoutes', 'handicappedFriendlyRoutes', 'luggageFriendlyRoutes', 'Creator', 'Closed', 'Comment'])

stationsDf['max_slope'] = np.tan(np.radians(stationsDf['max_angle'])) * 100
stationsDf['wheelchairFriendlyRoutes'] = ((stationsDf['min_lift_steps'] == 0 ) | (stationsDf['min_rail_steps'] == 0) | (stationsDf['min_rail_steps'] - stationsDf['lift_minus_steps'] <= 0) & (stationsDf['stairways'] == 0) & (stationsDf['min_width'] >= 740) & (stationsDf['max_slope'] <= 15) & (stationsDf['pandusUnavailable'] == 0)) * 1
stationsDf['handicappedFriendlyRoutes'] = ((stationsDf['min_lift_steps'] < 3) | (stationsDf['min_rail_steps'] < 3) | (stationsDf['min_rail_steps'] - stationsDf['lift_minus_steps'] <= 3) & (stationsDf['max_slope'] <= 15)) * 1
stationsDf['luggageFriendlyRoutes'] = ((stationsDf['min_lift_steps'] < 3) | (stationsDf['min_rail_steps'] < 3) | (stationsDf['min_rail_steps'] - stationsDf['lift_minus_steps'] <= 3) & (stationsDf['min_width'] >= 700)) * 1
'''

transfersReportDf = pd.read_csv(filePath2, sep=',', encoding='utf-8')

transfersByLine = pd.DataFrame({'transfersByLine' : transfersReportDf.groupby(by='line')['fromId'].count()})

stairwayAvailableTransfersDf = transfersReportDf[transfersReportDf['minStairways'] > 0]
stairwayUnavailableTransfersDf = transfersReportDf[transfersReportDf['minStairways'] == 0]

stairwayAvailableTransfersByLine = pd.DataFrame({'stairwayAvailableTransfersByLine' : stairwayAvailableTransfersDf.groupby(by='line')['minStairways'].count()})
stairwayUnavailableTransfersByLine = pd.DataFrame({'stairwayUnavailableTransfersByLine' : stairwayUnavailableTransfersDf.groupby(by='line')['minStairways'].count()})

wheelchairFriendlyTransfersDf = transfersReportDf[transfersReportDf['wheelchairFriendlyRoutes'] == 1]
handicappedFriendlyTransfersDf = transfersReportDf[transfersReportDf['handicappedFriendlyRoutes'] == 1]
luggageFriendlyTransfersDf = transfersReportDf[transfersReportDf['luggageFriendlyRoutes'] == 1]

wheelchairFriendlyTransfersByLine = pd.DataFrame({'wheelchairFriendlyTransfersByLine' : wheelchairFriendlyTransfersDf.groupby(by='line')['wheelchairFriendlyRoutes'].sum()})
handicappedFriendlyTransfersByLine = pd.DataFrame({'handicappedFriendlyTransfersByLine' : handicappedFriendlyTransfersDf.groupby(by='line')['handicappedFriendlyRoutes'].sum()})
luggageFriendlyTransfersByLine = pd.DataFrame({'luggageFriendlyTransfersByLine' : luggageFriendlyTransfersDf.groupby(by='line')['luggageFriendlyRoutes'].sum()})

# Calculating all stuff

'''
stairwayAvailableRoutesDf = stationsDf[stationsDf['stairways'] > 0]
stairwayUnavailableRoutesDf = stationsDf[stationsDf['stairways'] == 0]

stairwayAvailableRoutesByLine = pd.DataFrame({'stairwayAvailableRoutesByLine' : stairwayAvailableRoutesDf.groupby(by='line')['stairways'].count()})
stairwayUnavailableRoutesByLine = pd.DataFrame({'stairwayUnavailableRoutesByLine' : stairwayUnavailableRoutesDf.groupby(by='line')['stairways'].count()})

liftAvailableRoutesDf = stationsDf[stationsDf['lift'] > 0]
liftUnavailableRoutesDf = stationsDf[stationsDf['lift'] == 0]

liftAvailableRoutesByLine = pd.DataFrame({'liftAvailableRoutesByLine' : liftAvailableRoutesDf.groupby(by='line')['lift'].count()})
liftUnavailableRoutesByLine = pd.DataFrame({'liftUnavailableRoutesByLine' : liftUnavailableRoutesDf.groupby(by='line')['lift'].count()})
'''

stairwayAvailableRoutesByLine = pd.DataFrame({'stairwayAvailableRoutesByLine' : stationsReportDf.groupby(by='lineName')['stairwayAvailableRoutesAmount'].sum()})
liftAvailableRoutesByLine = pd.DataFrame({'liftAvailableRoutesByLine' : stationsReportDf.groupby(by='lineName')['liftAvailableRoutesAmount'].sum()})

routesInByLine = pd.DataFrame({'routesInByLine' : stationsReportDf.groupby(by='lineName').sum()['routesIn']})
routesOutByLine = pd.DataFrame({'routesOutByLine' : stationsReportDf.groupby(by='lineName').sum()['routesOut']})
'''
wheelchairFriendlyRoutesDf = stationsReportDf[stationsReportDf['wheelchairFriendlyRoutes'] == 1]
handicappedFriendlyRoutesDf = stationsReportDf[stationsReportDf['handicappedFriendlyRoutes'] == 1]
luggageFriendlyRoutesDf = stationsReportDf[stationsReportDf['luggageFriendlyRoutes'] == 1]

wheelchairFriendlyRoutesInByLine = pd.DataFrame({'wheelchairFriendlyRoutesInByLine' : wheelchairFriendlyRoutesDf.groupby(by='lineName')['wheelchairFriendlyRoutesIn'].sum()})
handicappedFriendlyRoutesInByLine = pd.DataFrame({'handicappedFriendlyRoutesInByLine' : handicappedFriendlyRoutesDf.groupby(by='lineName')['handicappedFriendlyRoutesIn'].sum()})
luggageFriendlyRoutesInByLine = pd.DataFrame({'luggageFriendlyRoutesInByLine' : luggageFriendlyRoutesDf.groupby(by='lineName')['luggageFriendlyRoutesIn'].sum()})

wheelchairFriendlyRoutesOutByLine = pd.DataFrame({'wheelchairFriendlyRoutesOutByLine' : wheelchairFriendlyRoutesDf.groupby(by='lineName')['wheelchairFriendlyRoutesOut'].sum()})
handicappedFriendlyRoutesOutByLine = pd.DataFrame({'handicappedFriendlyRoutesOutByLine' : handicappedFriendlyRoutesDf.groupby(by='lineName')['handicappedFriendlyRoutesOut'].sum()})
luggageFriendlyRoutesOutByLine = pd.DataFrame({'luggageFriendlyRoutesOutByLine' : luggageFriendlyRoutesDf.groupby(by='lineName')['luggageFriendlyRoutesOut'].sum()})
'''

wheelchairFriendlyRoutesInByLine = pd.DataFrame({'wheelchairFriendlyRoutesInByLine' : stationsReportDf.groupby(by='lineName')['wheelchairFriendlyRoutesIn'].sum()})
handicappedFriendlyRoutesInByLine = pd.DataFrame({'handicappedFriendlyRoutesInByLine' : stationsReportDf.groupby(by='lineName')['handicappedFriendlyRoutesIn'].sum()})
luggageFriendlyRoutesInByLine = pd.DataFrame({'luggageFriendlyRoutesInByLine' : stationsReportDf.groupby(by='lineName')['luggageFriendlyRoutesIn'].sum()})

wheelchairFriendlyRoutesOutByLine = pd.DataFrame({'wheelchairFriendlyRoutesOutByLine' : stationsReportDf.groupby(by='lineName')['wheelchairFriendlyRoutesOut'].sum()})
handicappedFriendlyRoutesOutByLine = pd.DataFrame({'handicappedFriendlyRoutesOutByLine' : stationsReportDf.groupby(by='lineName')['handicappedFriendlyRoutesOut'].sum()})
luggageFriendlyRoutesOutByLine = pd.DataFrame({'luggageFriendlyRoutesOutByLine' : stationsReportDf.groupby(by='lineName')['luggageFriendlyRoutesOut'].sum()})


#liftAvailableDf = stationsReportDf[stationsReportDf[lift] == 1]

# Saving results

def saveResult(filePath, fileName, result):
	resultPath = os.path.join(os.path.dirname(filePath), fileName)
	result.to_csv(resultPath, encoding='utf-8', na_rep=0, float_format='%d', index=True, header=True)

result = routesInByLine.join(routesOutByLine, how='left', sort=False)
result['routesAllByLine'] = result['routesInByLine'] + result['routesOutByLine']

'''
result = result.join(stairwayAvailableRoutesByLine, how='left', sort=False)
result = result.join(stairwayUnavailableRoutesByLine, how='left', sort=False)
result = result.join(liftAvailableRoutesByLine, how='left', sort=False)
result = result.join(liftUnavailableRoutesByLine, how='left', sort=False)
'''

result = result.join(stairwayAvailableRoutesByLine, how='left', sort=False)
result['stairwayUnavailableRoutesByLine'] = result['routesAllByLine'] - result['stairwayAvailableRoutesByLine']
result = result.join(liftAvailableRoutesByLine, how='left', sort=False)
result['liftUnavailableRoutesByLine'] = result['routesAllByLine'] - result['liftAvailableRoutesByLine']

result = result.join(wheelchairFriendlyRoutesInByLine, how='left', sort=False)
result = result.join(wheelchairFriendlyRoutesOutByLine, how='left', sort=False)
result = result.join(handicappedFriendlyRoutesInByLine, how='left', sort=False)
result = result.join(handicappedFriendlyRoutesOutByLine, how='left', sort=False)
result = result.join(luggageFriendlyRoutesInByLine, how='left', sort=False)
result = result.join(luggageFriendlyRoutesOutByLine, how='left', sort=False)

result['wheelchairNotFriendlyRoutesInByLine'] = result['routesInByLine'] - result['wheelchairFriendlyRoutesInByLine'].fillna(0)
result['wheelchairNotFriendlyRoutesOutByLine'] = result['routesOutByLine'] - result['wheelchairFriendlyRoutesOutByLine'].fillna(0)
result['handicappedNotFriendlyRoutesInByLine'] = result['routesInByLine'] - result['handicappedFriendlyRoutesInByLine'].fillna(0)
result['handicappedNotFriendlyRoutesOutByLine'] = result['routesOutByLine'] - result['handicappedFriendlyRoutesOutByLine'].fillna(0)
result['luggageNotFriendlyRoutesInByLine'] = result['routesInByLine'] - result['luggageFriendlyRoutesInByLine'].fillna(0)
result['luggageNotFriendlyRoutesOutByLine'] = result['routesOutByLine'] - result['luggageFriendlyRoutesOutByLine'].fillna(0)

result = result.join(transfersByLine, how='left', sort=False)
result = result.join(stairwayAvailableTransfersByLine, how='left', sort=False)
result = result.join(stairwayUnavailableTransfersByLine, how='left', sort=False)
result = result.join(wheelchairFriendlyTransfersByLine, how='left', sort=False)
result = result.join(handicappedFriendlyTransfersByLine, how='left', sort=False)
result = result.join(luggageFriendlyTransfersByLine, how='left', sort=False)

result['wheelchairNotFriendlyTransfersByLine'] = result['transfersByLine'] - result['wheelchairFriendlyTransfersByLine'].fillna(0)
result['handicappedNotFriendlyTransfersByLine'] = result['transfersByLine'] - result['handicappedFriendlyTransfersByLine'].fillna(0)
result['luggageNotFriendlyTransfersByLine'] = result['transfersByLine'] - result['luggageFriendlyTransfersByLine'].fillna(0)

result['Null'] = 0
saveResult(filePath, 'routesByLine.csv', result)
