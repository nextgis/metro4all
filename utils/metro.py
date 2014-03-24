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


values = [nodesDf['totalElements'].sum(), stationsDf['routesIn'].sum(), stationsDf['routesOut'].sum(), transfersDf['transferId'].count(), nodesDf['liftAmount'].sum(), nodesDf['hallLevelLiftsAmount'].sum(), nodesDf['trainLevelLiftsAmount'].sum(), nodesDf['surfaceLevelLiftsAmount'].sum(), nodesDf['lift'].sum(), nodesDf['hallLevelLifts'].sum(), nodesDf['trainLevelLifts'].sum(), nodesDf['surfaceLevelLifts'].sum(), nodesDf['pandusAmount'].sum(), nodesDf['pandusAvailableAmount'].sum(), nodesDf['pandus'].sum(), nodesDf['pandusAvailable'].sum(), nodesDf['stairway'].sum(), nodesDf['coupleStairsAmount'].sum(), stationsDf['wheelchairFriendlyRoutes'].sum(), stationsDf['wheelchairFriendlyRoutesAmount'].sum(), stationsDf['handicappedFriendlyRoutes'].sum(), stationsDf['handicappedFriendlyRoutesAmount'].sum(), stationsDf['luggageFriendlyRoutes'].sum(), stationsDf['luggageFriendlyRoutesAmount'].sum()]
index = ['количество элементов инфраструктуры', 'количество маршрутов на вход', 'количество маршрутов на выход', 'количество переходов', 'количество лифтов - всего', 'количество лифтов на уровень перехода', 'количество лифтов на уровень платформы', 'количество лифтов на уровень поверхности', 'количество узлов, оборудованных лифтами любого уровня', 'количество узлов, оборудованных лифтами до уровня перехода', 'количество узлов, оборудованных лифтами до уровня платформы', 'количество узлов, оборудованных лифтами до уровня поверхности', 'количество пандусов - всего', 'количество пандусов, доступных для инвалидов-колясочников', 'количество узлов, оборудованных пандусами', 'количество узлов, оборудованных пандусами, доступными для инвалидов-колясочников', 'количество узлов, оборудованных эскалаторами', 'количество одиночных ступеней', 'количество станций с маршрутами, доступными для инвалидов-колясочников', 'количество маршрутов, доступных для инвалидов-колясочников', 'количество станций с маршрутами, доступными для людей с затруднениями передвижения', 'количество маршрутов, доступных для людей с затруднениями передвижения', 'количество станций с маршрутами, доступными  для людей с детскими колясками и габаритным багажом', 'количество маршрутов, доступных для людей с детскими колясками и габаритным багажом'] 
result = pd.Series(values, index=index)

'''
nodesDf['totalElements'].sum()
stationsDf['routesIn'].sum()
stationsDf['routesOut'].sum()
transfersDf['transferId'].count()
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

количество элементов инфраструктуры
количество маршрутов на вход
количество маршрутов на выход
количество переходов
количество лифтов - всего
количество лифтов на уровень перехода
количество лифтов на уровень платформы
количество лифтов на уровень поверхности
количество узлов, оборудованных лифтами любого уровня
количество узлов, оборудованных лифтами до уровня перехода
количество узлов, оборудованных лифтами до уровня платформы
количество узлов, оборудованных лифтами до уровня поверхности
количество пандусов - всего
количество пандусов, доступных для инвалидов-колясочников
количество узлов, оборудованных пандусами
количество узлов, оборудованных пандусами, доступными для инвалидов-колясочников
количество узлов, оборудованных эскалаторами
количество одиночных ступеней
количество станций с маршрутами, доступными для инвалидов-колясочников
количество маршрутов, доступных для инвалидов-колясочников
количество станций с маршрутами, доступными для людей с затруднениями передвижения
количество маршрутов, доступных для людей с затруднениями передвижения
количество станций с маршрутами, доступными  для людей с детскими колясками и габаритным багажом
количество маршрутов, доступных для людей с детскими колясками и габаритным багажом
'''

resultPath = os.path.join(os.path.dirname(nodesReportPath), 'metroReport.csv')
result.to_csv(resultPath, encoding='utf-8', index_label='Показатель')
