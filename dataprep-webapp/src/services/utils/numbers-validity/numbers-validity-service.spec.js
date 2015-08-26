describe('NumbersValidityService service', function () {
	'use strict';

	beforeEach(module('data-prep.services.utils'));
	it('should check numbers validity', inject(function (NumbersValidityService) {
		//when
		var amIaNumber = NumbersValidityService.toNumber('dqsfds10010');
		var amIaNumber2 = NumbersValidityService.toNumber(' 88');

		//then
		expect(amIaNumber).toBe(undefined);
		expect(amIaNumber2).toBe(88);
	}));
});