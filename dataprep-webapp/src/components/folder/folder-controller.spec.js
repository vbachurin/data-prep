describe('Folder controller', function () {
    'use strict';

    var createController, scope;

    beforeEach(module('data-prep.folder'));

    beforeEach(inject(function($rootScope, $controller, $q, FolderService) {
        scope = $rootScope.$new();

        createController = function () {
            return $controller('FolderCtrl', {
                $scope: scope
            });
        };
        spyOn(FolderService, 'getFolderContent').and.returnValue($q.when(true));
        spyOn(FolderService, 'populateMenuChildren').and.returnValue($q.when(true));
    }));

    it('should call goToFolder service', inject(function (FolderService) {
        //when
        var ctrl = createController();
        ctrl.goToFolder();
        scope.$digest();

        //then
        expect(FolderService.getFolderContent).toHaveBeenCalled();
    }));

    it('should call populateMenuChildren service', inject(function (FolderService) {

        //when
        var ctrl = createController();
        expect(ctrl.loadingChildren).toBe(true);
        ctrl.initMenuChildren();
        scope.$digest();

        //then
        expect(FolderService.populateMenuChildren).toHaveBeenCalled();
        expect(ctrl.loadingChildren).toBe(false);
    }));

});
