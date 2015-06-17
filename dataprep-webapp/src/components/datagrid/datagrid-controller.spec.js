describe('Datagrid controller', function () {
    'use strict';

    var createController, scope;

    beforeEach(module('data-prep.datagrid'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('DatagridCtrl', {
                $scope: scope
            });
            return ctrl;
        };
    }));

    it('should set tooltip infos and display it after a 300ms delay', inject(function($rootScope, $timeout) {
        //given
        var ctrl = createController();
        ctrl.showTooltip = false;

        var record = {id: '345bc63a5cd5928', name: 'toto'};
        var colId = 'name';
        var position = {x: 123, y: 456};

        //when
        ctrl.updateTooltip(record, colId, position);

        //then
        expect(ctrl.record).toBeFalsy();
        expect(ctrl.colId).toBeFalsy();
        expect(ctrl.position).toBeFalsy();
        expect(ctrl.showTooltip).toBeFalsy();

        //when
        $timeout.flush(300);

        //then
        expect(ctrl.record).toBe(record);
        expect(ctrl.colId).toBe(colId);
        expect(ctrl.position).toBe(position);
        expect(ctrl.showTooltip).toBe(true);
    }));

    it('should hide tooltip after a 300ms delay', inject(function($rootScope, $timeout) {
        //given
        var ctrl = createController();
        ctrl.showTooltip = true;

        //when
        ctrl.hideTooltip();

        //then
        expect(ctrl.showTooltip).toBe(true);

        //when
        $timeout.flush(300);

        //then
        expect(ctrl.showTooltip).toBe(false);
    }));

    it('should cancel existing update promise on new hide promise', inject(function($rootScope, $timeout) {
        //given
        var ctrl = createController();
        ctrl.showTooltip = false;

        var record = {id: '345bc63a5cd5928', name: 'toto'};
        var colId = 'name';
        var position = {x: 123, y: 456};

        ctrl.updateTooltip(record, colId, position);
        $timeout.flush(299);
        expect(ctrl.showTooltip).toBe(false);

        //when
        ctrl.hideTooltip();
        $timeout.flush(1);

        //then
        expect(ctrl.record).toBeFalsy();
        expect(ctrl.colId).toBeFalsy();
        expect(ctrl.position).toBeFalsy();
        expect(ctrl.showTooltip).toBe(false);
    }));

    it('should compute hidden chars html code', inject(function () {
        //given
        var ctrl = createController();

        expect(ctrl.computeHTMLForLeadingOrTrailingHiddenChars('')).toBe('');
        expect(ctrl.computeHTMLForLeadingOrTrailingHiddenChars('AL')).toBe('AL');
        expect(ctrl.computeHTMLForLeadingOrTrailingHiddenChars(' AL')).toBe('<span class=\"hiddenChars\"> </span>AL');
        expect(ctrl.computeHTMLForLeadingOrTrailingHiddenChars(' AL ')).toBe('<span class=\"hiddenChars\"> </span>AL<span class=\"hiddenChars\"> </span>');
        expect(ctrl.computeHTMLForLeadingOrTrailingHiddenChars('AL ')).toBe('AL<span class=\"hiddenChars\"> </span>');
        expect(ctrl.computeHTMLForLeadingOrTrailingHiddenChars('\tAL\n')).toBe('<span class=\"hiddenChars\">\t</span>AL<span class=\"hiddenChars\">\n</span>');
        expect(ctrl.computeHTMLForLeadingOrTrailingHiddenChars('  AL\nfoo')).toBe('<span class=\"hiddenChars\">  </span>AL\nfoo');
        expect(ctrl.computeHTMLForLeadingOrTrailingHiddenChars('AL\n\rbar   ')).toBe('AL\n\rbar<span class=\"hiddenChars\">   </span>');
        expect(ctrl.computeHTMLForLeadingOrTrailingHiddenChars('    AL\n\rbar   ')).toBe('<span class=\"hiddenChars\">    </span>AL\n\rbar<span class=\"hiddenChars\">   </span>');

    }));



});
