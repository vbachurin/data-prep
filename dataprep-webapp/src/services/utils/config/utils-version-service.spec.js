describe('Version service', function() {
    'use strict';

    beforeEach(module('data-prep.services.utils'));

    it('should set unknown version as value', inject(function(version) {
        //then
        expect(version).toBe('Unknown version');
    }));
});