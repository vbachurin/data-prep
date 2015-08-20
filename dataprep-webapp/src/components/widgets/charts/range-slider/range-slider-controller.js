(function () {
	'use strict';

	/**
	 * @ngdoc controller
	 * @name data-prep.rangeSlider.controller:RangeSliderCtrl
	 * @description The rangeSlider controller
	 */
	function RangeSliderCtrl() {
		var vm = this;

		vm.toNumber = function toNumber (value) {
			if(/^[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?$/.test(value)){
				return Number(value.trim());
			}
			return null;
		};

		vm.checkCommaExistence = function checkCommaExistence (minMaxStr){
			return minMaxStr.indexOf(',') !== -1 ? true : false;
		};

		vm.showMsgErr = function showMsgErr(minMaxStr){
			var msgErr = 'Invalid Entered Value';
			var finalMsgErr = vm.checkCommaExistence(minMaxStr)? msgErr + ': Use "." instead of ","' : msgErr;
			d3.select('text.invalid-value-msg')
				.text(finalMsgErr);
		};

		vm.hideMsgErr = function hideMsgErr(textMsgEle){
			textMsgEle.text('');
		};

		vm.setCenterValue = function setCenterValue (minBrush, maxBrush){
			vm.centerValue = (minBrush + maxBrush)/2;
		};

		vm.initBrush = function initBrush (scale){
			vm.brush = d3.svg.brush()
				.x(scale)
				.extent([vm.centerValue, vm.centerValue]);
		}
	}

	angular.module('data-prep.rangeSlider')
		.controller('RangeSliderCtrl', RangeSliderCtrl);
})();