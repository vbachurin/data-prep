describe('DatasetColumnHeader controller', function () {
    'use strict';

    var createController, scope;

    beforeEach(module('data-prep-dataset'));

    beforeEach(inject(function ($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('DatasetColumnHeaderCtrl', {
                $scope: scope
            });
            return ctrl;
        };
    }));

    it('should init grouped and divided transformation menu', inject(function($timeout) {
        //given
        var ctrl = createController();

        //when
        ctrl.initTransformations();
        $timeout.flush();

        //then
        expect(ctrl.transformations.length).toBe(5);
        expect(ctrl.transformations[0].name).toBe('uppercase');
        expect(ctrl.transformations[1].name).toBe('lowercase');
        expect(ctrl.transformations[2].name).toBe('withParam');
        expect(ctrl.transformations[3].isDivider).toBe(true);
        expect(ctrl.transformations[4].name).toBe('split');
    }));

    it('should not get transformations is transformations are already initiated', inject(function($timeout) {
        //given
        var ctrl = createController();
        ctrl.initTransformations();
        $timeout.flush();

        //when
        ctrl.initTransformations();
        try{
            $timeout.flush();
        }
        catch(e) {
            return;
        }

        //then
        throw new Error('Timeout flush should have thrown error because there is nothing to flush');
    }));
});
