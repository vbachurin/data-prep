describe('Badge controller', function () {
    'use strict';

    var createController, scope, filterType;
    var obj = {value: 'toto'};
    var fns = {
        change: function() {},
        close: function() {}
    };

    beforeEach(module('talend.widget'));

    beforeEach(inject(function ($rootScope, $controller) {
        spyOn(fns, 'change').and.returnValue();
        spyOn(fns, 'close').and.returnValue();

        scope = $rootScope.$new();

        createController = function () {
            var ctrlFn = $controller('BadgeCtrl', {
                $scope: scope
            }, true);
            ctrlFn.instance.type = filterType;
            ctrlFn.instance.obj = obj;
            ctrlFn.instance.onChange = fns.change;
            ctrlFn.instance.onClose = fns.close;

            return ctrlFn();
        };
    }));

    it('should set the sign caracter to : in', function () {
        //given
        filterType = 'inside_range';

        //when
        var ctrl = createController();

        //then
        expect(ctrl.sign).toEqual('in');
    });

    it('should set the sign caracter to : ":"', function () {
        //given
        filterType = 'valid_records';

        //when
        var ctrl = createController();

        //then
        expect(ctrl.sign).toEqual(':');
    });

    it('should set the sign caracter to : "≅"', function () {
        //given
        filterType = 'contains';

        //when
        var ctrl = createController();

        //then
        expect(ctrl.sign).toEqual('≅');
    });

    it('should set the sign caracter to : "=" ', function () {
        //given
        filterType = 'exact';

        //when
        var ctrl = createController();

        //then
        expect(ctrl.sign).toEqual('=');
    });

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