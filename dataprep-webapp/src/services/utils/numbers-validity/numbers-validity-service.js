(function() {
	'use strict';

	/**
	 * @ngdoc service
	 * @name data-prep.services.utils.service:NumbersValidityService
	 * @description checks the numbers validity especially for floats, exponential, ...
	 */
	function NumbersValidityService() {

		/**
		 * @ngdoc method
		 * @name toNumber
		 * @methodOf data-prep.services.utils.service:NumbersValidityService
		 * @param {String} type - the type of the given value
		 * @description [PRIVATE] trims and checks if the entered string is valid
		 * @return Number or null
		 */
		this.toNumber = function toNumber (value) {
			value = value.trim();
			if(/^[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?$/.test(value)){
				return Number(value);
			}
			return undefined;//no null because null>=0 is true
		};

	}

	angular.module('data-prep.services.utils')
		.service('NumbersValidityService', NumbersValidityService);

})();