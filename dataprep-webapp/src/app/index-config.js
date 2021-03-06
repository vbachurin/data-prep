/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

import angular from 'angular';

function getConfig(url) {
	const initInjector = angular.injector(['ng']);
	const $http = initInjector.get('$http');
	return $http.get(url).then(response => response.data);
}

function getAppConfig() {
	const url = '/assets/config/config.json';
	return getConfig(url);
}

function getAppSettings(config) {
	const url = `${config.serverUrl}/api/settings`;
	return getConfig(url);
}

export default function getAppConfiguration() {
	let config;
	return getAppConfig()
		.then((appConfig) => {
			config = appConfig;
			return getAppSettings(config);
		})
		.then(appSettings => ({ config, appSettings }));
}
