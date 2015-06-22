describe('Tabs widget controller', function () {
    'use strict';

    var createController, scope;

    beforeEach(module('talend.widget'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('TalendTabsCtrl', {
                $scope: scope
            });
            return ctrl;
        };
    }));

    it('should save tab in tabs list', function() {
        //given
        var ctrl = createController();
        var tab = {active : false, title: 'my tab'};

        //when
        ctrl.register(tab);

        //then
        expect(ctrl.tabs).toContain(tab);
    });

    it('should set tab active flag on first element', function() {
        //given
        var ctrl = createController();
        var tab = {active : false, title: 'my tab'};

        //when
        ctrl.register(tab);

        //then
        expect(tab.active).toBe(true);
    });

    it('should NOT set tab active flag on second element', function() {
        //given
        var ctrl = createController();
        var tab = {active : false, title: 'my tab'};
        var tab2 = {active : false, title: 'my tab'};

        //when
        ctrl.register(tab);
        ctrl.register(tab2);

        //then
        expect(tab2.active).toBe(false);
    });

    it('should update active flag on tab selection', function() {
        //given
        var ctrl = createController();
        var tab = {active : false, title: 'my tab'};
        var tab2 = {active : false, title: 'my tab'};
        var tab3 = {active : false, title: 'my tab'};
        var tab4 = {active : false, title: 'my tab'};

        ctrl.register(tab);
        ctrl.register(tab2);
        ctrl.register(tab3);
        ctrl.register(tab4);

        expect(tab.active).toBe(true);
        expect(tab2.active).toBe(false);
        expect(tab3.active).toBe(false);
        expect(tab4.active).toBe(false);

        //when
        ctrl.select(tab3);

        //then
        expect(tab.active).toBe(false);
        expect(tab2.active).toBe(false);
        expect(tab3.active).toBe(true);
        expect(tab4.active).toBe(false);
    });
});
