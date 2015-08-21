(function () {
	'use strict';

	/**
	 * @ngdoc controller
	 * @name data-prep.rangeSlider.controller:RangeSliderCtrl
	 * @description The rangeSlider controller
	 */
	function RangeSliderCtrl() {
		var vm = this;

		/**
		 * @ngdoc method
		 * @name toNumber
		 * @propertyOf data-prep.rangeSlider.controller:RangeSliderCtrl
		 * @description check if the entered value is a number
		 */
		vm.toNumber = function toNumber (value) {
			value = value.trim();
			if(/^[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?$/.test(value)){
				return Number(value);
			}
			return null;
		};

		/**
		 * @ngdoc method
		 * @name checkCommaExistence
		 * @propertyOf data-prep.rangeSlider.controller:RangeSliderCtrl
		 * @description check if the entered values contain a comma
		 */
		vm.checkCommaExistence = function checkCommaExistence (minMaxStr){
			return minMaxStr.indexOf(',') !== -1 ? true : false;
		};

		/**
		 * @ngdoc method
		 * @name setCenterValue
		 * @propertyOf data-prep.rangeSlider.controller:RangeSliderCtrl
		 * @description set the centerValue property to the middle of the existent brush extent
		 */
		vm.setCenterValue = function setCenterValue (minBrush, maxBrush){
			vm.centerValue = (minBrush + maxBrush)/2;
		};

		/**
		 * @ngdoc method
		 * @name initBrush
		 * @propertyOf data-prep.rangeSlider.controller:RangeSliderCtrl
		 * @description initializes the rangeSlider horizontal Brush
		 */
		vm.initBrush = function initBrush (scale){
			vm.brush = d3.svg.brush()
				.x(scale)
				.extent([vm.centerValue, vm.centerValue]);
		};
	}

	angular.module('data-prep.rangeSlider')
		.controller('RangeSliderCtrl', RangeSliderCtrl);
})();