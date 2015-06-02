describe('Converter service', function() {
    'use strict';

    beforeEach(module('data-prep.services.utils'));

    it('should return number when type is numeric', inject(function(ConverterService) {
        //given
        var type = 'numeric';

        //when
        var inputType = ConverterService.toInputType(type);

        //then
        expect(inputType).toBe('number');
    }));

    it('should return number when type is integer', inject(function(ConverterService) {
        //given
        var type = 'integer';

        //when
        var inputType = ConverterService.toInputType(type);

        //then
        expect(inputType).toBe('number');
    }));

    it('should return number when type is double', inject(function(ConverterService) {
        //given
        var type = 'double';

        //when
        var inputType = ConverterService.toInputType(type);

        //then
        expect(inputType).toBe('number');
    }));

    it('should return number when type is float', inject(function(ConverterService) {
        //given
        var type = 'float';

        //when
        var inputType = ConverterService.toInputType(type);

        //then
        expect(inputType).toBe('number');
    }));

    it('should return text', inject(function(ConverterService) {
        //given
        var type = 'string';

        //when
        var inputType = ConverterService.toInputType(type);

        //then
        expect(inputType).toBe('text');
    }));
});