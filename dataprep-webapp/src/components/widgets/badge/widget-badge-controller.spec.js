describe('Badge controller', function () {
    'use strict';

    var createController, scope;
    var obj = {value: 'toto'};
    var fns = {
        change: function() {},
        close: function() {}
    };

    beforeEach(module('talend.widget'));

    beforeEach(inject(function ($rootScope, $controller) {
        spyOn(fns, 'change').and.callThrough();
        spyOn(fns, 'close').and.callThrough();

        scope = $rootScope.$new();

        createController = function () {
            var ctrlFn = $controller('BadgeCtrl', {
                $scope: scope
            }, true);
            ctrlFn.instance.obj = obj;
            ctrlFn.instance.onChange = fns.change;
            ctrlFn.instance.onClose = fns.close;

            return ctrlFn();
        };
    }));

    it('should call onChange callback if the editable value has changed', function () {
        //given
        var ctrl = createController();
        ctrl.value = 'tata';

        //when
        ctrl.manageChange();

        //then
        expect(fns.change).toHaveBeenCalledWith({obj: obj, newValue: 'tata'});
    });

    it('should do nothing if the editable value has not changed', function () {
        //given
        var ctrl = createController();
        ctrl.value = 'toto';

        //when
        ctrl.manageChange();

        //then
        expect(fns.change).not.toHaveBeenCalled();
    });

    it('should call onClose callback', function () {
        //given
        var ctrl = createController();

        //when
        ctrl.close();

        //then
        expect(fns.close).toHaveBeenCalled();
    });
});