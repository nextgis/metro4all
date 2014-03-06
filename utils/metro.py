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


values = [nodesDf['totalElements'].sum(), stationsDf['routesIn'].sum(), stationsDf['routesOut'].sum(), transfersDf['transferId'].count(), nodesDf['liftAmount'].sum(), nodesDf['surfaceLevelLiftsAmount'].sum(), nodesDf['hallLevelLiftsAmount'].sum(), nodesDf['trainLevelLiftsAmount'].sum(), nodesDf['lift'].sum(), nodesDf['surfaceLevelLifts'].sum(), nodesDf['hallLevelLifts'].sum(), nodesDf['trainLevelLifts'].sum(), nodesDf['pandusAmount'].sum(), nodesDf['pandus'].sum(), nodesDf['stairway'].sum(), nodesDf['coupleStairs'].sum()]
index = ['количество элементов инфраструктуры', 'количество маршрутов на вход', 'количество маршрутов на выход', 'количество переходов', 'количество лифтов - всего', 'количество лифтов на уровень перехода', 'количество лифтов на уровень платформы', 'количество лифтов на уровень поверхности', 'количество узлов, оборудованных лифтами любого уровня', 'количество узлов, оборудованных лифтами до уровня перехода', 'количество узлов, оборудованных лифтами до уровня платформы', 'количество узлов, оборудованных лифтами до уровня поверхности', 'количество пандусов - всего', 'количество узлов, оборудованных пандусами', 'количество узлов, оборудованных эскалаторами', 'количество одиночных ступеней']
result = pd.Series(values, index=index)

'''
nodesDf['totalElements'].sum()})
nodesDf['totalElements'].sum()
stationsDf['routesIn'].sum()
stationsDf['routesOut'].sum()
transfersDf['transferId'].count()
nodesDf['liftAmount'].sum()
nodesDf['surfaceLevelLiftsAmount'].sum()
nodesDf['hallLevelLiftsAmount'].sum()
nodesDf['trainLevelLiftsAmount'].sum()
nodesDf['lift'].sum()
nodesDf['surfaceLevelLifts'].sum()
nodesDf['hallLevelLifts'].sum()
nodesDf['trainLevelLifts'].sum()
nodesDf['pandusAmount'].sum()
nodesDf['pandus'].sum()
nodesDf['stairway'].sum()
nodesDf['coupleStairs'].sum()

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
количество узлов, оборудованных пандусами
количество узлов, оборудованных эскалаторами
количество одиночных ступеней
'''

resultPath = os.path.join(os.path.dirname(nodesReportPath), 'metroReport.csv')
result.to_csv(resultPath, encoding='utf-8', index_label='Показатель')
