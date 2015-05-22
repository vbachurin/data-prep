describe('Transform choice params controller', function () {
    'use strict';

    var createController, scope, extractedParams, choices;

    beforeEach(module('data-prep.transformation-params'));

    beforeEach(inject(function ($rootScope, $controller) {
        extractedParams = null;
        scope = $rootScope.$new();

        createController = function () {
            var ctrlFn = $controller('TransformChoiceParamsCtrl', {
                $scope: scope
            }, true);
            ctrlFn.instance.choices = choices;
            return ctrlFn();
        };
    }));

    it('should init choice default value', function() {
        //given
        choices = [{
            name: 'mode',
            values: [
                {name: 'regex'},
                {name: 'index', default: true}
            ]
        }];

        //when
        var ctrl = createController();

        //then
        expect(ctrl.choices[0].selectedValue).toEqual({name: 'index', default: true});
    });
});