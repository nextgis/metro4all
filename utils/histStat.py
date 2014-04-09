# -*- encoding: utf-8 -*-

# example: python histStat.py elements.csv transfersReport.csv entrances.csv elevators.csv

import os
import sys
import math
import pandas as pd
import numpy as np
 
filePath = sys.argv[1]
filePath2 = sys.argv[2]
filePath3 = sys.argv[3]
elevPath = sys.argv[4]
# , dtype={'stairs': np.int32}
elementsDf = pd.read_csv(filePath, sep=',', header=0, encoding='utf-8', names=['node_id', 'transfer_id', 'element_id', 'node_name', 'element', 'stairs', 'stairs_with_railing', 'couple_stairs', 'railing', 'width', 'min_width', 'max_width', 'angle', 'slope', 'lift_class', 'working_status', 'pandusAvailability'])

elementsDf['surfaceLevelLifts'] = ((elementsDf['lift_class'] == 3) | (elementsDf['lift_class'] == 1)) * 1
elementsDf['hallLevelLifts'] = ((elementsDf['lift_class'] == 2) | (elementsDf['lift_class'] == 1)) * 1
elementsDf['trainLevelLifts'] = ((elementsDf['lift_class'] == 2) | (elementsDf['lift_class'] == 3)) * 1


''' Calculated columns
elementsDf['couple_stairs] = (elementsDf['stairs] <= 3) * 1 ##Check NA values!
elementsDf['stairs_with_railing'] = elementsDf['stairs'] * (elementsDf['railing] == 1)
elementsDf['slope'] = np.tan(np.radians(elementsDf['max_angle'])) * 100
'''

transfersReportDf = pd.read_csv(filePath2, sep=',', encoding='utf-8')

stationsDf = pd.read_csv(filePath3,  sep=',', header=0, encoding='utf-8', names=['id', 'id2', 'station', 'line', 'entrance_name', 'station_id', 'line_id', '0_x', '0_y', 'direction', 'min_width', 'min_steps', 'min_rail_steps', 'lift', 'lift_minus_steps', 'min_lift_steps', 'min_rail_width', 'max_rail_width', 'max_angle', 'max_slope', 'stairways', 'pandusUnavailable', 'wheelchairFriendlyRoutes', 'handicappedFriendlyRoutes', 'luggageFriendlyRoutes', 'Creator', 'Closed', 'Comment'])

''' Calculated columns
stationsDf['max_slope'] = np.tan(np.radians(stationsDf['max_angle'])) * 100
stationsDf['wheelchairFriendlyRoutes'] = ((stationsDf['min_lift_steps'] == 0 ) | (stationsDf['min_rail_steps'] == 0) | (stationsDf['min_rail_steps'] - stationsDf['lift_minus_steps'] <= 0) & (stationsDf['stairways'] == 0) & (stationsDf['min_width'] >= 740) & (stationsDf['max_slope'] <= 15) & (stationsDf['pandusUnavailable'] == 0)) * 1
stationsDf['handicappedFriendlyRoutes'] = ((stationsDf['min_lift_steps'] < 3) | (stationsDf['min_rail_steps'] < 3) | (stationsDf['min_rail_steps'] - stationsDf['lift_minus_steps'] <= 3) & (stationsDf['max_slope'] <= 15)) * 1
stationsDf['luggageFriendlyRoutes'] = ((stationsDf['min_lift_steps'] < 3) | (stationsDf['min_rail_steps'] < 3) | (stationsDf['min_rail_steps'] - stationsDf['lift_minus_steps'] <= 3) & (stationsDf['min_width'] >= 700)) * 1
'''

elevatorsDf = pd.read_csv(elevPath, sep=',', header=0, encoding='utf-8', names=['node_id', 'element_id', 'node', 'year', 'doorWidth', 'cabinWidth', 'cabinDepth', 'destinationType', 'position', 'accessType', 'workingStatus', 'lastCheck', 'inspector', 'photo'])

# nodes
##['node_id', 'transfer_id', 'element_id', 'node_name', 'element', 'stairs', 'stairs_with_railing', 'couple_stairs', 'railing', 'width', 'min_width', 'max_width', 'angle', 'slope', 'lift_class', 'working_status', 'pandusAvailability']

# Calculating all stuff

def changeIdxType(DataFrame, idxType):
	DataFrame.index = DataFrame.index.astype(idxType)

#Переходы

##Самое узкое место на переходе
minTaperTransfers = transfersReportDf[transfersReportDf['minWidth'] > 0]['minWidth']
minTaperTransfers.name = 'value'

##Длина лестниц на переходе
minStepsTransfers = transfersReportDf[transfersReportDf['minStairs'] > 0]['minStairs']
minStepsTransfers.name = 'value'

##Длина лестниц на переходе c учетом рельс и пандусов
minRailStepsTransfers = transfersReportDf[transfersReportDf['minRailsStairs'] > 0]['minRailsStairs']
minRailStepsTransfers.name = 'value'

##Длина лестниц на переходе c учетом лифтов
minLiftStepsTransfers = transfersReportDf[transfersReportDf['minLiftStairs'] > 0]['minLiftStairs']
minLiftStepsTransfers.name = 'value'

#Маршруты станций

##Самое узкое место на маршрутах
minTaperStations = stationsDf[stationsDf['min_width'] > 0]['min_width']
minTaperStations.name = 'value'

##Длина лестниц на маршрутах
minStepsStations = stationsDf[stationsDf['min_steps'] > 0]['min_steps']
minStepsStations.name = 'value'

##Длина лестниц на маршрутах c учетом рельс и пандусов
minRailStepsStations = stationsDf[stationsDf['min_rail_steps'] > 0]['min_rail_steps']
minRailStepsStations.name = 'value'

##Длина лестниц на маршрутах c учетом лифтов
minLiftStepsStations = stationsDf[stationsDf['min_lift_steps'] > 0]['min_lift_steps']
minLiftStepsStations.name = 'value'

#Элементы по узлам

## Длина лестниц по частоте
stairsLength = elementsDf[elementsDf['stairs'] > 0]['stairs']
stairsLength.name = 'value'

## Ширина рельс min по частоте
minRailsWidth = elementsDf[elementsDf['min_width'] > 0]['min_width']
minRailsWidth.name = 'value'

## Ширина рельс max по частоте
maxRailsWidth = elementsDf[elementsDf['max_width'] > 0]['max_width']
maxRailsWidth.name = 'value'

## Уклон рельс по частоте
railsSlope = elementsDf[elementsDf['element'] == u'лестница с аппарелью']['slope']
railsSlope = railsSlope[railsSlope > 0]
railsSlope.name = 'value'

## Уклон пандусов по частоте
pandusSlope = elementsDf[elementsDf['element'] == u'пандус']['slope']
pandusSlope = pandusSlope[pandusSlope > 0]
pandusSlope.name = 'value'

## Ширина турникетов по частоте
turnstileWidth = elementsDf[elementsDf['element'] == u'турникет']['width']
turnstileWidth = turnstileWidth[turnstileWidth > 0]
turnstileWidth.name = 'value'

## Ширина дверей по частоте
doorWidth = elementsDf[elementsDf['element'] == u'дверь']['width']
doorWidth = doorWidth[doorWidth > 0]
doorWidth.name = 'value'

## Ширина сужений по частоте
taperWidth = elementsDf[elementsDf['element'] == u'сужение']['width']
taperWidth = taperWidth[taperWidth > 0]
taperWidth.name = 'value'

## Ширина дверей и сужений по частоте
doorAndTaperWidth = elementsDf[(elementsDf['element'] == u'дверь') | (elementsDf['element'] == u'сужение')]['width']
doorAndTaperWidth = doorAndTaperWidth[doorAndTaperWidth > 0]
doorAndTaperWidth.name = 'value'

## Ширина эскалаторов по частоте
stairwayWidth = elementsDf[elementsDf['element'] == u'эскалатор']['width']
stairwayWidth = stairwayWidth[stairwayWidth > 0]
stairwayWidth.name = 'value'

'''
## Ширина лифтов по частоте
liftWidth = elementsDf[elementsDf['element'] == u'лифт']['width']
liftWidth = liftWidth[liftWidth > 0]
liftWidth.name = 'value'
'''

## Лифты
## Ширина лифтов по частоте
liftWidth = elevatorsDf[elevatorsDf['doorWidth'] > 0]['doorWidth']
liftWidth.name = 'value'

## Ширина кабины лифтов по частоте
cabinWidth = elevatorsDf[elevatorsDf['cabinWidth'] > 0]['cabinWidth']
cabinWidth.name = 'value'

## Глубина кабины лифтов по частоте
cabinDepth = elevatorsDf[elevatorsDf['cabinDepth'] > 0]['cabinDepth']
cabinDepth.name = 'value'

# Saving results

def saveResult(filePath, fileName, result):
	resultPath = os.path.join(os.path.dirname(filePath), fileName)
	result.to_csv(resultPath, encoding='utf-8', index=False, float_format='%d', header=True)
#Переходы

##Самое узкое место на перходе
saveResult(filePath, 'minTaperTransfers.csv', minTaperTransfers)

##Длина лестниц на перходе
saveResult(filePath, 'minStepsTransfers.csv', minStepsTransfers)

##Длина лестниц на перходе c учетом рельс и пандусов
saveResult(filePath, 'minRailStepsTransfers.csv', minRailStepsTransfers)

##Длина лестниц на перходе c учетом лифтов
saveResult(filePath, 'minLiftStepsTransfers.csv', minLiftStepsTransfers)

#Маршруты станций

##Самое узкое место на маршрутах
saveResult(filePath, 'minTaperStations.csv', minTaperStations)

##Длина лестниц на маршрутах
saveResult(filePath, 'minStepsStations.csv', minStepsStations)

##Длина лестниц на маршрутах c учетом рельс и пандусов
saveResult(filePath, 'minRailStepsStations.csv', minRailStepsStations)

##Длина лестниц на маршрутах c учетом лифтов
saveResult(filePath, 'minLiftStepsStations.csv', minLiftStepsStations)

#Элементы по узлам

## Длина лестниц по частоте
saveResult(filePath, 'stairsLength.csv', stairsLength)

## Ширина рельс min по частоте
saveResult(filePath, 'minRailsWidth.csv', minRailsWidth)

## Ширина рельс max по частоте
saveResult(filePath, 'maxRailsWidth.csv', maxRailsWidth)

## Уклон рельс по частоте
saveResult(filePath, 'railsSlope.csv', railsSlope)

## Уклон пандусов по частоте
saveResult(filePath, 'pandusSlope.csv', pandusSlope)

## Ширина турникетов по частоте
saveResult(filePath, 'turnstileWidth.csv', turnstileWidth)

## Ширина дверей по частоте
saveResult(filePath, 'doorWidth.csv', doorWidth)
'''
## Ширина сужений по частоте
saveResult(filePath, 'taperWidth.csv', taperWidth)

## Ширина дверей и сужений по частоте
saveResult(filePath, 'doorAndTaperWidth.csv', doorAndTaperWidth)
'''
## Ширина эскалаторов по частоте
saveResult(filePath, 'stairwayWidth.csv', stairwayWidth)

## Ширина лифтов по частоте
saveResult(filePath, 'liftWidth.csv', liftWidth)

## Ширина кабины лифтов по частоте
saveResult(filePath, 'cabinWidth.csv', cabinWidth)

## Глубина кабины лифтов по частоте
saveResult(filePath, 'cabinDepth.csv', cabinDepth)
