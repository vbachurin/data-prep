/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('DatasetCopyMove controller', function () {
    var  createController, scope, ctrl, $element;

    beforeEach(angular.mock.module('data-prep.dataset-copy-move'));

    beforeEach(inject(function ($rootScope, $componentController) {
        scope = $rootScope.$new();
        $element = {};
        createController = function () {
            return $componentController('datasetCopyMove', {
                $scope: scope,
                $element: $element
            }, {
                dataset : {name: 'my ds name'},
                initialFolder : {
                    path: 'folder1',
                    name: 'my folder name'
                }
            });
        };
    }));

    describe('clone', function () {
        beforeEach(inject(function () {
            ctrl = createController();
            ctrl.destinationFolder = {name: 'my folder name', path:'/parent/child'};
            ctrl.copyMoveForm = {};
            ctrl.copyMoveForm.$commitViewValue = jasmine.createSpy('$commitViewValue');
        }));

        describe('when success', function () {
            beforeEach(inject(function ($q) {
                ctrl.onCopy = jasmine.createSpy('onCopy').and.returnValue($q.when(true));
            }));

            it('should call clone service', inject(function () {
                //when
                ctrl.clone();
                expect(ctrl.isCloningDs).toBeTruthy();
                expect(ctrl.copyMoveForm.$commitViewValue).toHaveBeenCalled();

                //then
                expect(ctrl.onCopy).toHaveBeenCalledWith({
                    dataset: ctrl.dataset,
                    destination: ctrl.destinationFolder,
                    name: ctrl.newDsName
                });
            }));
        });

        describe('when failure due to server error', function () {
            beforeEach(inject(function ($q) {
                ctrl.onCopy = jasmine.createSpy('onCopy').and.returnValue($q.reject());
                ctrl._focusOnNameInput = jasmine.createSpy('_focusOnNameInput');
            }));

            it('should fail on clone service call', inject(function ($timeout) {
                //when
                ctrl.clone();
                expect(ctrl.isCloningDs).toBeTruthy();
                scope.$digest();
                $timeout.flush();

                //then
                expect(ctrl.isCloningDs).toBeFalsy();
                expect(ctrl._focusOnNameInput).toHaveBeenCalled();
            }));
        });
    });

    describe('move', function () {
        beforeEach(inject(function () {
            ctrl = createController();
            ctrl.destinationFolder = {name: 'my folder name', path:'/parent/child'};
            ctrl.copyMoveForm = {};
            ctrl.copyMoveForm.$commitViewValue = jasmine.createSpy('$commitViewValue');
        }));

        describe('when success', function () {
            beforeEach(inject(function ($q) {
                ctrl.onMove = jasmine.createSpy('onMove').and.returnValue($q.when(true));
            }));

            it('should call move service', inject(function () {
                //when
                ctrl.move();
                expect(ctrl.isMovingDs).toBeTruthy();
                expect(ctrl.copyMoveForm.$commitViewValue).toHaveBeenCalled();

                //then
                expect(ctrl.onMove).toHaveBeenCalledWith({
                    dataset: ctrl.dataset,
                    destination: ctrl.destinationFolder,
                    name: ctrl.newDsName
                });
            }));
        });

        describe('when failure due to server error', function () {
            beforeEach(inject(function ($q) {
                ctrl.onMove = jasmine.createSpy('onMove').and.returnValue($q.reject());
                ctrl._focusOnNameInput = jasmine.createSpy('_focusOnNameInput');
            }));

            it('should fail on move service call', inject(function ($timeout) {
                //when
                ctrl.move();
                expect(ctrl.isMovingDs).toBeTruthy();
                scope.$digest();
                $timeout.flush();

                //then
                expect(ctrl.isMovingDs).toBeFalsy();
                expect(ctrl._focusOnNameInput).toHaveBeenCalled();
            }));
        });
    });
});
