# -*- encoding: utf-8 -*-

# example: python nodes.py elements.csv

import os
import sys
import math
import pandas as pd
import numpy as np

filePath = sys.argv[1]

sourceDf = pd.read_csv(filePath, sep=',', header=0, encoding='utf-8', names=['node_id', 'transfer_id', 'element_id', 'node_name', 'element', 'stairs', 'stairs_with_railing', 'couple_stairs', 'railing', 'width', 'min_width', 'max_width', 'angle', 'slope', 'lift_class', 'working_status', 'pandusAvailability'])
'''
sourceDf['liftSurfaceToTrain'] = (sourceDf['lift_class'] == 3) * 1
sourceDf['liftHallToTrain'] = (sourceDf['lift_class'] == 2) * 1
sourceDf['liftSurfaceToHall'] = (sourceDf['lift_class'] == 1) * 1
'''
sourceDf['surfaceLevelLifts'] = ((sourceDf['lift_class'] == 3) | (sourceDf['lift_class'] == 1)) * 1
sourceDf['hallLevelLifts'] = ((sourceDf['lift_class'] == 2) | (sourceDf['lift_class'] == 1)) * 1
sourceDf['trainLevelLifts'] = ((sourceDf['lift_class'] == 2) | (sourceDf['lift_class'] == 3)) * 1


''' Calculated columns
sourceDf['couple_stairs] = (sourceDf['stairs] <= 3) * 1 #Check NA values!
sourceDf['stairs_with_railing'] = sourceDf['stairs'] * (sourceDf['railing] == 1)
sourceDf['slope'] = np.tan(np.radians(sourceDf['max_angle'])) * 100
'''
# nodes
#['node_id', 'transfer_id', 'element_id', 'node_name', 'element', 'stairs', 'stairs_with_railing', 'couple_stairs', 'railing', 'width', 'min_width', 'max_width', 'angle', 'slope', 'lift_class', 'working_status', 'pandusAvailability']

# Create index == nodeId
nodeId = sourceDf['node_id'].unique()
nodeName = pd.DataFrame({'nodeName' : sourceDf['node_name'].unique()}, index=nodeId)

# Pivots
elementsAmount = pd.pivot_table(sourceDf, values='element_id', rows='node_id', cols='element', fill_value=0, aggfunc = 'count')
elementsMinSlope = pd.pivot_table(sourceDf, values='slope', rows='node_id', cols='element', fill_value=None, aggfunc = 'min')
elementsMaxSlope = pd.pivot_table(sourceDf, values='slope', rows='node_id', cols='element', fill_value=None, aggfunc = 'max')
elementsStairsSum = pd.pivot_table(sourceDf, values='stairs', rows='node_id', cols='element', fill_value=0, aggfunc = 'sum')
elementsMinWidth = pd.pivot_table(sourceDf, values='min_width', rows='node_id', cols='element', fill_value=None, aggfunc = 'min')
elementsMaxWidth = pd.pivot_table(sourceDf, values='max_width', rows='node_id', cols='element', fill_value=None, aggfunc = 'max')
elementsWidthMin = pd.pivot_table(sourceDf, values='width', rows='node_id', cols='element', fill_value=None, aggfunc = 'min')
elementsWidthMax = pd.pivot_table(sourceDf, values='width', rows='node_id', cols='element', fill_value=None, aggfunc = 'max')
# If railing inicator use sum else remove zeros and use count
elementsRailingAmount = pd.pivot_table(sourceDf, values='railing', rows='node_id', cols='element', fill_value=0, aggfunc = 'sum')
elementsRailingStairsSum = pd.pivot_table(sourceDf, values='stairs_with_railing', rows='node_id', cols='element', fill_value=0, aggfunc = 'sum')

# Calculating all stuff

#nodeStations
#nodeTransfers
totalElements = pd.DataFrame({'totalElements' : sourceDf.groupby(by='node_id')['element_id'].count()})
stairwaysAmount = pd.DataFrame({'stairwaysAmount' : elementsAmount[u'эскалатор']})
#stairway = pd.DataFrame({'stairway' : (stairwaysAmount > 0) * 1})
minStairwayWidth = pd.DataFrame({'minStairwayWidth' : elementsWidthMin[u'эскалатор']})
maxStairwayWidth = pd.DataFrame({'maxStairwayWidth' : elementsWidthMax[u'эскалатор']})

doorsAmount = pd.DataFrame({'doorsAmount' : elementsAmount[u'дверь']})
tapersAmount = pd.DataFrame({'tapersAmount' : elementsAmount[u'сужение']})
doorsAndTapersAmount = pd.DataFrame({'doorsAndTapersAmount' : elementsAmount[u'дверь'] + elementsAmount[u'сужение']})
minDoorWidth = pd.DataFrame({'minDoorWidth' : elementsWidthMin[u'дверь']})
minTaperWidth = pd.DataFrame({'minTaperWidth' : elementsWidthMin[u'сужение']})
maxDoorWidth = pd.DataFrame({'maxDoorWidth' : elementsWidthMax[u'дверь']})
maxTaperWidth = pd.DataFrame({'maxTaperWidth' : elementsWidthMax[u'сужение']})

turnstilesAmount = pd.DataFrame({'turnstilesAmount' : elementsAmount[u'турникет']})
minturnstileWidth = pd.DataFrame({'minturnstileWidth' : elementsWidthMin[u'турникет']})
maxturnstileWidth = pd.DataFrame({'maxturnstileWidth' : elementsWidthMax[u'турникет']})

liftAmount = pd.DataFrame({'liftAmount' : elementsAmount[u'лифт']})
#lift = (liftAmount > 0) * 1

surfaceLevelLiftsAmount = pd.DataFrame({'surfaceLevelLiftsAmount' : sourceDf.groupby(by='node_id')['surfaceLevelLifts'].sum()})
hallLevelLiftsAmount = pd.DataFrame({'hallLevelLiftsAmount' : sourceDf.groupby(by='node_id')['hallLevelLifts'].sum()})
trainLevelLiftsAmount = pd.DataFrame({'trainLevelLiftsAmount' : sourceDf.groupby(by='node_id')['trainLevelLifts'].sum()})

minLiftWidth = pd.DataFrame({'minLiftWidth' : elementsWidthMin[u'лифт']})
maxLiftWidth = pd.DataFrame({'maxLiftWidth' : elementsWidthMax[u'лифт']})
pandusAmount = pd.DataFrame({'pandusAmount' : elementsAmount[u'пандус']})
#pandus = (pandusAmount > 0) * 1
pandusAvailableAmount = pd.DataFrame({'pandusAvailableAmount' : sourceDf.groupby(by='node_id')['pandusAvailability'].sum()})
#pandusAvailable = (pandusAvailableAmount > 0) * 1
pandusRailing = pd.DataFrame({'pandusRailing' : elementsRailingAmount[u'пандус']})
pandusMinSlope = pd.DataFrame({'pandusMinSlope' : elementsMinSlope[u'пандус']})
pandusMaxSlope = pd.DataFrame({'pandusMaxSlope' : elementsMaxSlope[u'пандус']})
stairsAmount = pd.DataFrame({'stairsAmount' : elementsAmount[u'лестница'] + elementsAmount[u'лестница с аппарелью']})
#stairs = (stairsAmount > 0) * 1
coupleStairsAmount = pd.DataFrame({'coupleStairsAmount' : sourceDf.groupby(by='node_id')['couple_stairs'].sum()})
#coupleStairs = (coupleStairsAmount > 0) * 1
stairsLength = pd.DataFrame({'stairsLength' : elementsStairsSum[u'лестница'] + elementsStairsSum[u'лестница с аппарелью']})
railsStairsAmount = pd.DataFrame({'railsStairsAmount' : elementsAmount[u'лестница с аппарелью']})
#rails = (railsStairsAmount > 0) * 1
railsStairsLength = pd.DataFrame({'railsStairsLength' : elementsStairsSum[u'лестница с аппарелью']})
minRailsWidth = pd.DataFrame({'minRailsWidth' : elementsMinWidth[u'лестница с аппарелью']})
maxRailsWidth = pd.DataFrame({'maxRailsWidth' : elementsMaxWidth[u'лестница с аппарелью']})
minRailsSlope = pd.DataFrame({'minRailsSlope' : elementsMinSlope[u'лестница с аппарелью']})
maxRailsSlope = pd.DataFrame({'maxRailsSlope' : elementsMaxSlope[u'лестница с аппарелью']})
railingAmount = pd.DataFrame({'railingAmount' : elementsRailingAmount[u'лестница'] + elementsRailingAmount[u'лестница с аппарелью']})
#railing = (railingAmount > 0) * 1
railingStairsLength = pd.DataFrame({'railingStairsLength' : elementsRailingStairsSum[u'лестница'] + elementsRailingStairsSum[u'лестница с аппарелью']})
noRailingStairsLength = pd.DataFrame({'noRailingStairsLength' : elementsStairsSum[u'лестница'] + elementsStairsSum[u'лестница с аппарелью'] - elementsRailingStairsSum[u'лестница'] - elementsRailingStairsSum[u'лестница с аппарелью']})

# Merging all the results
#DataFrame.merge(right, how='inner', on=None, left_on=None, right_on=None, left_index=False, right_index=False, sort=False, suffixes=('_x', '_y'), copy=True)

result = nodeName.join(totalElements, how='inner', sort=False)

result = result.join(stairwaysAmount, how='inner', sort=False)
#result = result.join(stairway, how='inner', sort=False)
result['stairway'] = (stairwaysAmount > 0) * 1
result = result.join(minStairwayWidth, how='inner', sort=False)
result = result.join(maxStairwayWidth, how='inner', sort=False)

result = result.join(doorsAmount, how='inner', sort=False)
result = result.join(minDoorWidth, how='inner', sort=False)
result = result.join(maxDoorWidth, how='inner', sort=False)

result = result.join(tapersAmount, how='inner', sort=False)
result = result.join(minTaperWidth, how='inner', sort=False)
result = result.join(maxTaperWidth, how='inner', sort=False)

result = result.join(doorsAndTapersAmount, how='inner', sort=False)
result['minDoorAndTaperWidth'] = result[['minDoorWidth', 'minTaperWidth']].min(1)
result['maxDoorAndTaperWidth'] = result[['maxDoorWidth', 'maxTaperWidth']].max(1)

result = result.join(turnstilesAmount, how='inner', sort=False)
result = result.join(minturnstileWidth, how='inner', sort=False)
result = result.join(maxturnstileWidth, how='inner', sort=False)

result = result.join(liftAmount, how='inner', sort=False)
#result = result.join(lift, how='inner', sort=False)
result['lift'] = (liftAmount > 0) * 1

result = result.join(surfaceLevelLiftsAmount, how='inner', sort=False)
result = result.join(hallLevelLiftsAmount, how='inner', sort=False)
result = result.join(trainLevelLiftsAmount, how='inner', sort=False)

result['surfaceLevelLifts'] = (surfaceLevelLiftsAmount > 0) * 1
result['hallLevelLifts'] = (hallLevelLiftsAmount > 0) * 1
result['trainLevelLifts'] = (trainLevelLiftsAmount > 0) * 1

result = result.join(minLiftWidth, how='inner', sort=False)
result = result.join(maxLiftWidth, how='inner', sort=False)
result = result.join(pandusAmount, how='inner', sort=False)
#result = result.join(pandus, how='inner', sort=False)
result['pandus'] = (pandusAmount > 0) * 1
result = result.join(pandusAvailableAmount, how='inner', sort=False)
result['pandusAvailable'] = (pandusAvailableAmount > 0) * 1
result = result.join(pandusRailing, how='inner', sort=False)
result = result.join(pandusMinSlope, how='inner', sort=False)
result = result.join(pandusMaxSlope, how='inner', sort=False)

result = result.join(stairsAmount, how='inner', sort=False)
#result = result.join(stairs, how='inner', sort=False)
result['stairs'] = (stairsAmount > 0) * 1
result = result.join(coupleStairsAmount, how='inner', sort=False)
#result = result.join(coupleStairs, how='inner', sort=False)
result['coupleStairs'] = (coupleStairsAmount > 0) * 1
result = result.join(stairsLength, how='inner', sort=False)
result = result.join(railsStairsAmount, how='inner', sort=False)
#result = result.join(rails, how='inner', sort=False)
result['rails'] = (railsStairsAmount > 0) * 1
result = result.join(railsStairsLength, how='inner', sort=False)
result = result.join(minRailsWidth, how='inner', sort=False)
result = result.join(maxRailsWidth, how='inner', sort=False)
result = result.join(minRailsSlope, how='inner', sort=False)
result = result.join(maxRailsSlope, how='inner', sort=False)
result = result.join(railingAmount, how='inner', sort=False)
#result = result.join(railing, how='inner', sort=False)
result['noRailing'] = (noRailingStairsLength > 0) * 1
result['noRailingAmount'] = result['stairsAmount'] - result['railingAmount']
result = result.join(railingStairsLength, how='inner', sort=False)
result = result.join(noRailingStairsLength, how='inner', sort=False)

# Saving results

resultPath = os.path.join(os.path.dirname(filePath), 'nodesReport.csv')
result.to_csv(resultPath, encoding='utf-8', index_label='nodeId')
