describe('Transform date params controller', function () {
    'use strict';

    var createController, scope, parameters;

    beforeEach(module('data-prep.transformation-params'));

    beforeEach(inject(function ($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrlFn = $controller('TransformDateParamsCtrl', {
                $scope: scope
            }, true);
            ctrlFn.instance.parameters = parameters;
            return ctrlFn();
        };
    }));

    it('should set default', function() {
        //given
        parameters = [
            {name: 'param2', type: 'date', default: '02/01/2012 09:42:22'}
        ];

        //when
        var ctrl = createController();

        //then
        expect(ctrl.parameters[0].default).toBe('02/01/2012 09:42:22');
    });
});