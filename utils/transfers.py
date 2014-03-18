# -*- encoding: utf-8 -*-

# example: python transfers.py transfers.csv

import os
import sys
import math
import pandas as pd
import numpy as np

filePath = sys.argv[1]

sourceDf = pd.read_csv(filePath, sep=',', header=0, encoding='utf-8', names=['fromStation', 'toStation', 'transferId', 'fromId', 'toId', 'nodeId', 'minWidth', 'minStairs', 'minRailsStairs', 'lift', 'liftStairsEconomy', 'minRailsWidth', 'maxRailsWidth', 'maxAngle', 'maxSlope', 'minStairways','wheelchairFriendlyRoutes', 'handicappedFriendlyRoutes', 'luggageFriendlyRoutes'])

''' Calculated columns
sourceDf['maxSlope'] = np.tan(np.radians(sourceDf['maxAngle'])) * 100
sourceDf['wheelchairFriendlyRoutes'] = ((sourceDf['minLiftStairs'] == 0 ) | (sourceDf['minRailsStairs'] == 0) | (sourceDf['minRailsStairs'] - sourceDf['liftStairsEconomy'] <= 0) & (sourceDf['minStairways'] == 0) & (sourceDf['minWidth'] >= 740) & (sourceDf['maxSlope'] <= 15)) * 1
sourceDf['handicappedFriendlyRoutes'] = ((sourceDf['minLiftStairs'] < 3) | (sourceDf['minRailsStairs'] < 3) | (sourceDf['minRailsStairs'] - sourceDf['liftStairsEconomy'] <= 3) & (sourceDf['maxSlope'] <= 15)) * 1
sourceDf['luggageFriendlyRoutes'] = ((sourceDf['minLiftStairs'] < 3) | (sourceDf['minRailsStairs'] < 3) | (sourceDf['minRailsStairs'] - sourceDf['liftStairsEconomy'] <= 3) & (sourceDf['minWidth'] >= 700)) * 1
'''
# transfers
#['fromStation', 'toStation', 'transferId', 'fromId', 'toId', 'nodeId', 'minWidth', 'minStairs', 'minRailsStairs', 'lift', 'liftStairsEconomy', 'minRailsWidth', 'maxRailsWidth', 'maxAngle', 'maxSlope', 'minStairways']

resultPath = os.path.join(os.path.dirname(filePath), 'transfersReport.csv')
sourceDf.to_csv(resultPath, encoding='utf-8', index=False)
