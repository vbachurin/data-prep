describe('playground state service', function () {
	'use strict';

	var originalData = {
		records: [
			{tdpId: 0, firstname: 'Tata'},
			{tdpId: 1, firstname: 'Tetggggge'},
			{tdpId: 2, firstname: 'Titi'},
			{tdpId: 3, firstname: 'Toto'},
			{tdpId: 4, name: 'AMC Gremlin'},
			{tdpId: 5, firstname: 'Tyty'},
			{tdpId: 6, firstname: 'Papa'},
			{tdpId: 7, firstname: 'Pepe'},
			{tdpId: 8, firstname: 'Pipi'},
			{tdpId: 9, firstname: 'Popo'},
			{tdpId: 10, firstname: 'Pupu'},
			{tdpId: 11, firstname: 'Pypy'}
		]
	};

	beforeEach(module('data-prep.services.state'));
	beforeEach(module('data-prep.services.playground'));

	it('should keep in memory the right numbers of the shown & all lines', inject(function (PlaygroundStateService, playgroundState) {
		//when
		PlaygroundStateService.setData(originalData);
		expect(playgroundState.allLinesLength).toBe(12);
		expect(playgroundState.shownLinesLength).toBe(12);
	}));

});
