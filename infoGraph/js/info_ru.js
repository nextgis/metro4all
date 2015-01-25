var tr = {
	'language': 'rus',
	'shemaHeader': {
		'wheelchairFriendly': 'Станции с маршрутами, доступными для инвалидов-колясочников',
		'handicappedFriendly': 'Станции с маршрутами, доступными для людей с затруднениями предвижения',
		'luggageFriendly': 'Станции с маршрутами, доступными для людей с детскими колясками'
	},
	'stationHeader': {
		'routesIn': 'Количество маршрутов на вход',
		'routesOut': 'Количество маршрутов на выход',
		'minTaper': 'Самое узкое место на маршрутах, мм',
		'minStairways': 'Минимальное количество эскалаторов на маршрутах',
		'maxLiftAmount': 'Максимальное количество лифтов на маршрутах',
		'minStairs': 'Минимальная протяжённость лестниц на маршрутах, ступенек',
		'avStairs': 'Средняя протяжённость лестниц на маршрутах, ступенек',
		'maxStairs': 'Максимальная протяжённость лестниц на маршрутах, ступенек',
		'minRailsStairs': 'Минимальная протяжённость лестниц на маршрутах с учетом рельс и пандусов, ступенек',
		'avRailsStairs': 'Средняя протяжённость лестниц на маршрутах с учетом рельс и пандусов, ступенек',
		'maxRailsStairs': 'Максимальная протяжённость лестниц на маршрутах с учетом рельс и пандусов, ступенек',
		'minLiftStairs': 'Минимальная протяжённость лестниц на маршрутах с учетом лифтов, ступенек',
		'avLiftStairs': 'Средняя протяжённость лестниц на маршрутах с учетом лифтов, ступенек',
		'maxLiftStairs': 'Максимальная протяжённость лестниц на маршрутах с учетом лифтов, ступенек',
	},
	'nodeHeader': {
		'totalElements': 'Количество измеренных элементов инфраструктуры узла',
		'stairwaysAmount': 'Количество эскалаторов',
		'doorsAndTapersAmount': 'Количество дверей',
		'minDoorAndTaperWidth': 'Минимальная ширина дверей, мм',
		'turnstilesAmount': 'Количество турникетов',
		'minturnstileWidth': 'Минимальная ширина турникетов, мм',
		'liftAmount': 'Количество лифтов',
		'hallLevelLiftsAmount': 'Количество лифтов на уровень перехода',
		'trainLevelLiftsAmount': 'Количество лифтов на уровень платформы',
		'surfaceLevelLiftsAmount': 'Количество лифтов на уровень поверхности',
		'minLiftWidth': 'Минимальная ширина дверей лифтов, мм',
		'pandusAmount': 'Количество пандусов',
		'pandusMaxSlope': 'Максимальный уклон пандусов, %',
		'pandusAvailableAmount': 'Количество пандусов, доступных для инвалидов-колясочников',
		'stairsAmount': 'Количество лестниц',
		'noRailingAmount': 'Количество лестниц без перил',
		'coupleStairsAmount': 'Количество одиночных ступеней',
		'railsStairsAmount': 'Количество лестниц с рельсами',
		'minRailsWidth': 'Минимальная ширина рельс, мм',
		'maxRailsWidth': 'Максимальная ширина рельс, мм',
		'maxRailsSlope': 'Максимальный уклон рельс, %'
	},
	'accessibilityHeader': {
		'wheelchairFriendlyRoutesIn': 'Количество маршрутов на вход, доступных для инвалидов-колясочников',
		'wheelchairFriendlyRoutesOut': 'Количество маршрутов на выход, доступных для инвалидов-колясочников',
		'handicappedFriendlyRoutesIn': 'Количество маршрутов на вход, доступных для людей с затруднениями передвижения',
		'handicappedFriendlyRoutesOut': 'Количество маршрутов на выход, доступных для людей с затруднениями передвижения',
		'luggageFriendlyRoutesIn': 'Количество маршрутов на вход, доступных для людей с детскими колясками',
		'luggageFriendlyRoutesOut': 'Количество маршрутов на выход, доступных для людей с детскими колясками'
	},
	'transferHeader': {
		'minWidth': 'Самое узкое место, мм',
		'minStairs': 'Общая протяжённость лестниц, ступенек',
		'minRailsStairs': 'Протяжённость лестниц с учетом рельс и пандусов, ступенек',
		'minLiftStairs': 'Протяжённость лестниц с учетом лифтов, ступенек',
		'stairwaysAmount': 'Количество эскалаторов',
		'liftAmount': 'Количество лифтов',
		'wheelchairFriendlyRoutes': 'Доступен ли переход для инвалидов-колясочников',
		'handicappedFriendlyRoutes': 'Доступен ли переход для людей с затруднениями передвижения',
		'luggageFriendlyRoutes': 'Доступен ли переход для людей с детскими колясками'
	},
	'target': {
		'station': 'по станции ',
		'node': 'Входит в состав узла ',
		'transfer': 'по переходу ',
		'metro': 'по метро '
	},
	'booleanWords': {
		'yes': 'да',
		'no': 'нет'
	},
	'metroStatHeader': {
		'totalElements': 'Количество измеренных элементов инфраструктуры',
		'totalRoutesIn': 'Количество маршрутов на вход',
		'totalRoutesOut': 'Количество маршрутов на выход',
		'totalTransfers': 'Количество переходов',
		'totalNodesWithTransfers': 'Количество узлов, на которых есть переходы',
		'totalLifts': 'Количество лифтов - всего',
		'totalHallLevelLifts': 'Количество лифтов на уровень перехода',
		'totalTrainLevelLifts': 'Количество лифтов на уровень платформы',
		'totalSurfaceLevelLifts': 'Количество лифтов на уровень поверхности',
		'totalNodesWithLifts': 'Количество узлов, оборудованных лифтами любого уровня',
		'totalNodesWithHallLevelLifts': 'Количество узлов, оборудованных лифтами до уровня перехода',
		'totalNodesWithTrainLevelLifts': 'Количество узлов, оборудованных лифтами до уровня платформы',
		'totalNodesWithSurfaceLevelLifts': 'Количество узлов, оборудованных лифтами до уровня поверхности',
		'totalPanduses': 'Количество пандусов - всего',
		'totalPandusesAvailable': 'Количество пандусов, доступных для инвалидов-колясочников',
		'totalNodesWithPanduses': 'Количество узлов, оборудованных пандусами',
		'totalNodesWithPandusesAvailable': 'Количество узлов, оборудованных пандусами, доступными для инвалидов-колясочников',
		'totalNodesWithStairway': 'Количество узлов, оборудованных эскалаторами',
		'totalCoupleStairs': 'Количество одиночных ступеней',
		'totalStationsWithWheelchairFriendlyRoutes': 'Количество станций с маршрутами, доступными для инвалидов-колясочников',
		'totalWheelchairFriendlyRoutes': 'Количество маршрутов, доступных для инвалидов-колясочников',
		'totalStationsWithHandicappedFriendlyRoutes': 'Количество станций с маршрутами, доступными для людей с затруднениями передвижения',
		'totalHandicappedFriendlyRoutes': 'Количество маршрутов, доступных для людей с затруднениями передвижения',
		'totalStationsWithLuggageFriendlyRoutes': 'Количество станций с маршрутами, доступными  для людей с детскими колясками',
		'totalLuggageFriendlyRoutes': 'Количество маршрутов, доступных для людей с детскими колясками'
	}
}
