describe('Converter service', function() {
    'use strict';

    beforeEach(module('data-prep.services.utils'));

    it('should return number when input type is numeric', inject(function(ConverterService) {
        //given
        var type = 'numeric';

        //when
        var inputType = ConverterService.toInputType(type);

        //then
        expect(inputType).toBe('number');
    }));

    it('should return number when input type is integer', inject(function(ConverterService) {
        //given
        var type = 'integer';

        //when
        var inputType = ConverterService.toInputType(type);

        //then
        expect(inputType).toBe('number');
    }));

    it('should return number when input type is double', inject(function(ConverterService) {
        //given
        var type = 'double';

        //when
        var inputType = ConverterService.toInputType(type);

        //then
        expect(inputType).toBe('number');
    }));

    it('should return number when input type is float', inject(function(ConverterService) {
        //given
        var type = 'float';

        //when
        var inputType = ConverterService.toInputType(type);

        //then
        expect(inputType).toBe('number');
    }));

    it('should return text when input type is string', inject(function(ConverterService) {
        //given
        var type = 'string';

        //when
        var inputType = ConverterService.toInputType(type);

        //then
        expect(inputType).toBe('text');
    }));

    it('should return number when column type is numeric, integer, double or float', inject(function(ConverterService) {
        checkSimplifiedTypes(ConverterService, ['numeric', 'integer', 'double', 'float'], 'number');
    }));

    it('should return text when column type is string or char', inject(function(ConverterService) {
        checkSimplifiedTypes(ConverterService, ['string', 'char'], 'text');
    }));

    it('should return boolean when column type is boolean', inject(function(ConverterService) {
        checkSimplifiedTypes(ConverterService, ['boolean'], 'boolean');
    }));

    it('should return date when column type is date', inject(function(ConverterService) {
        checkSimplifiedTypes(ConverterService, ['date'], 'date');
    }));

    it('should return test when column type is unknown', inject(function(ConverterService) {
        checkSimplifiedTypes(ConverterService, ['toto', 'titi', 'tata', ''], 'text');
    }));

    /**
     * @ngdoc method
     * @name checkSimplifiedTypes
     * @methodOf data-prep.services.utils.service:ConverterServiceSpec
     * @param {Object} service - the converter service
     * @param {string[]} types - the types to convert
     * @param {string} expectedType - the expected type
     * @description Convert the given types and check against the expected one
     */
    var checkSimplifiedTypes = function(service, types, expectedType) {
        for (var i = 0; i < types.length; i++) {

            //when
            var type = service.simplifyType(types[i]);

            // then
            expect(type).toBe(expectedType);
        }
    };

});