# -*- encoding: utf-8 -*-

# example: python metro.py nodesReport.csv stationsReport.csv transfersReport.csv

import os
import sys
import math
import pandas as pd
import numpy as np

nodesReportPath = sys.argv[1]
stationsReportPath = sys.argv[2]
transfersReportPath = sys.argv[3]

nodesDf = pd.read_csv(nodesReportPath, sep=',', encoding='utf-8')
stationsDf = pd.read_csv(stationsReportPath, sep=',', encoding='utf-8')
transfersDf = pd.read_csv(transfersReportPath, sep=',', encoding='utf-8')


value = [nodesDf['totalElements'].sum(), stationsDf['routesIn'].sum(), stationsDf['routesOut'].sum(), transfersDf['transferId'].count(), transfersDf['nodeId'].unique().size, nodesDf['liftAmount'].sum(), nodesDf['hallLevelLiftsAmount'].sum(), nodesDf['trainLevelLiftsAmount'].sum(), nodesDf['surfaceLevelLiftsAmount'].sum(), nodesDf['lift'].sum(), nodesDf['hallLevelLifts'].sum(), nodesDf['trainLevelLifts'].sum(), nodesDf['surfaceLevelLifts'].sum(), nodesDf['pandusAmount'].sum(), nodesDf['pandusAvailableAmount'].sum(), nodesDf['pandus'].sum(), nodesDf['pandusAvailable'].sum(), nodesDf['stairway'].sum(), nodesDf['coupleStairsAmount'].sum(), stationsDf['wheelchairFriendlyRoutes'].sum(), stationsDf['wheelchairFriendlyRoutesAmount'].sum(), stationsDf['handicappedFriendlyRoutes'].sum(), stationsDf['handicappedFriendlyRoutesAmount'].sum(), stationsDf['luggageFriendlyRoutes'].sum(), stationsDf['luggageFriendlyRoutesAmount'].sum()]
index = ['totalElements', 'totalRoutesIn', 'totalRoutesOut', 'totalTransfers', 'totalNodesWithTransfers', 'totalLifts', 'totalHallLevelLifts', 'totalTrainLevelLifts', 'totalSurfaceLevelLifts', 'totalNodesWithLifts', 'totalNodesWithHallLevelLifts', 'totalNodesWithTrainLevelLifts', 'totalNodesWithSurfaceLevelLifts', 'totalPanduses', 'totalPandusesAvailable', 'totalNodesWithPanduses', 'totalNodesWithPandusesAvailable', 'totalNodesWithStairway', 'totalCoupleStairs', 'totalStationsWithWheelchairFriendlyRoutes', 'totalWheelchairFriendlyRoutes', 'totalStationsWithHandicappedFriendlyRoutes', 'totalHandicappedFriendlyRoutes', 'totalStationsWithLuggageFriendlyRoutes', 'totalLuggageFriendlyRoutes']
#index = ['Количество элементов инфраструктуры', 'Количество маршрутов на вход', 'Количество маршрутов на выход', 'Количество переходов', 'Количество узлов, на которых есть переходы', 'Количество лифтов - всего', 'Количество лифтов на уровень перехода', 'Количество лифтов на уровень платформы', 'Количество лифтов на уровень поверхности', 'Количество узлов, оборудованных лифтами любого уровня', 'Количество узлов, оборудованных лифтами до уровня перехода', 'Количество узлов, оборудованных лифтами до уровня платформы', 'Количество узлов, оборудованных лифтами до уровня поверхности', 'Количество пандусов - всего', 'Количество пандусов, доступных для инвалидов-колясочников', 'Количество узлов, оборудованных пандусами', 'Количество узлов, оборудованных пандусами, доступными для инвалидов-колясочников', 'Количество узлов, оборудованных эскалаторами', 'Количество одиночных ступеней', 'Количество станций с маршрутами, доступными для инвалидов-колясочников', 'Количество маршрутов, доступных для инвалидов-колясочников', 'Количество станций с маршрутами, доступными для людей с затруднениями передвижения', 'Количество маршрутов, доступных для людей с затруднениями передвижения', 'Количество станций с маршрутами, доступными  для людей с детскими колясками', 'Количество маршрутов, доступных для людей с детскими колясками']
type = ['nodeInfo', 'targetInfo', 'targetInfo', 'targetInfo', 'targetInfo', 'nodeInfo', 'otherInfo', 'otherInfo', 'otherInfo', 'nodeInfo', 'otherInfo', 'otherInfo', 'otherInfo', 'nodeInfo', 'otherInfo', 'nodeInfo', 'nodeInfo', 'nodeInfo', 'nodeInfo', 'targetAccess', 'targetAccess', 'targetAccess', 'targetAccess', 'targetAccess', 'targetAccess']
data = {'value': value, 'type': type}
result = pd.DataFrame(data=data, index=index, dtype=np.int32)
#result = pd.Series(values, index=index, name='value', dtype=np.int32)

'''
nodesDf['totalElements'].sum()
stationsDf['routesIn'].sum()
stationsDf['routesOut'].sum()
transfersDf['transferId'].count()
transfersDf['nodeId'].unique().size()
nodesDf['liftAmount'].sum()
nodesDf['hallLevelLiftsAmount'].sum()
nodesDf['trainLevelLiftsAmount'].sum()
nodesDf['surfaceLevelLiftsAmount'].sum()
nodesDf['lift'].sum()
nodesDf['hallLevelLifts'].sum()
nodesDf['trainLevelLifts'].sum()
nodesDf['surfaceLevelLifts'].sum()
nodesDf['pandusAmount'].sum()
nodesDf['pandusAvailableAmount'].sum()
nodesDf['pandus'].sum()
nodesDf['pandusAvailable'].sum()
nodesDf['stairway'].sum()
nodesDf['coupleStairsAmount'].sum()
stationsDf['wheelchairFriendlyRoutes'].sum()
stationsDf['wheelchairFriendlyRoutesAmount'].sum()
stationsDf['handicappedFriendlyRoutes'].sum()
stationsDf['handicappedFriendlyRoutesAmount'].sum()
stationsDf['luggageFriendlyRoutes'].sum()
stationsDf['luggageFriendlyRoutesAmount'].sum()

nodeInfo	totalElements	Количество элементов инфраструктуры
targetInfo	totalRoutesIn	Количество маршрутов на вход
targetInfo	totalRoutesOut	Количество маршрутов на выход
targetInfo	totalTransfers	Количество переходов
targetInfo	totalNodesWithTransfers	Количество узлов, на которых есть переходы
nodeInfo	totalLifts	Количество лифтов - всего
otherInfo	totalHallLevelLifts	Количество лифтов на уровень перехода
otherInfo	totalTrainLevelLifts	Количество лифтов на уровень платформы
otherInfo	totalSurfaceLevelLifts	Количество лифтов на уровень поверхности
nodeInfo	totalNodesWithLifts	Количество узлов, оборудованных лифтами любого уровня
otherInfo	totalNodesWithHallLevelLifts	Количество узлов, оборудованных лифтами до уровня перехода
otherInfo	totalNodesWithTrainLevelLifts	Количество узлов, оборудованных лифтами до уровня платформы
otherInfo	totalNodesWithSurfaceLevelLifts	Количество узлов, оборудованных лифтами до уровня поверхности
nodeInfo	totalPanduses	Количество пандусов - всего
nodeInfo	totalPandusesAvailable	Количество пандусов, доступных для инвалидов-колясочников
nodeInfo	totalNodesWithPanduses	Количество узлов, оборудованных пандусами
nodeInfo	totalNodesWithPandusesAvailable	Количество узлов, оборудованных пандусами, доступными для инвалидов-колясочников
nodeInfo	totalNodesWithStairway	Количество узлов, оборудованных эскалаторами
nodeInfo	totalCoupleStairs	Количество одиночных ступеней
targetAccess	totalStationsWithWheelchairFriendlyRoutes	Количество станций с маршрутами, доступными для инвалидов-колясочников
targetAccess	totalWheelchairFriendlyRoutes	Количество маршрутов, доступных для инвалидов-колясочников
targetAccess	totalStationsWithHandicappedFriendlyRoutes	Количество станций с маршрутами, доступными для людей с затруднениями передвижения
targetAccess	totalHandicappedFriendlyRoutes	Количество маршрутов, доступных для людей с затруднениями передвижения
targetAccess	totalStationsWithLuggageFriendlyRoutes	Количество станций с маршрутами, доступными  для людей с детскими колясками
targetAccess	totalLuggageFriendlyRoutes	Количество маршрутов, доступных для людей с детскими колясками
'''

resultPath = os.path.join(os.path.dirname(nodesReportPath), 'metroReport.csv')
result.to_csv(resultPath, encoding='utf-8', index_label='factor', header=True)
