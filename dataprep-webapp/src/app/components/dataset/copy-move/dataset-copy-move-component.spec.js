/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('dataset copy move component', function(){
    var scope, createElement, element, controller;

    beforeEach(angular.mock.module('data-prep.dataset-copy-move'));
    beforeEach(angular.mock.module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();

        createElement = function() {
            element = angular.element(
                `<dataset-copy-move
                    initial-folder="initialFolder"
                    dataset="dataset"
                    on-copy="clone(dataset, destination, name)"
                    on-move="move(dataset, destination, name)"
                ></dataset-copy-move>`
            );

            angular.element('body').append(element);

            scope.dataset = {name:'my dataset'};
            scope.initialFolder = {path:'0-folder1/1-folder11'};

            $compile(element)(scope);
            scope.$digest();

            controller = element.controller('datasetCopyMove');
        };
    }));

    afterEach(function () {
        scope.$destroy();
        element.remove();
    });

    describe('modal display', function(){

        beforeEach(inject(function($q, FolderService){
            spyOn(FolderService, 'children').and.returnValue($q.when({data:[]}));
        }));

        it('should display modal content', function(){
            //when
            createElement();
            scope.$digest();

            //then
            expect(element.find('folder-selection').length).toBe(1);
            expect(element.find('.clone-name > input').length).toBe(1);
            expect(element.find('button').length).toBe(3);
            expect(element.find('talend-button-loader').length).toBe(2);
        });

        it('should focus on the name input field', function(){
            //given
            createElement();
            scope.$digest();

            //when
            controller._focusOnNameInput();

            //then
            var activeEl = document.activeElement;
            expect(angular.element(activeEl).attr('id')).toBe('new-name-input-id');
        });
    });

    describe('submit form', function(){

        beforeEach(inject(function($q, FolderService){
            spyOn(FolderService, 'children').and.returnValue($q.when({data:[]}));

            createElement();
            //enable submit buttons
            controller.move = jasmine.createSpy('move');
            controller.clone = jasmine.createSpy('clone');
            scope.$digest();
        }));

        it('should submit move query on move button click', function(){
            //given
            var moveBtn = element.find('#move-ds-btn').eq(0);

            //when
            moveBtn.click();

            //then
            expect(controller.move).toHaveBeenCalled();
        });

        it('should submit clone query on clone button click', function(){
            //given
            var cloneBtn = element.find('#clone-ds-btn').eq(0);

            //when
            cloneBtn.click();

            //then
            expect(controller.clone).toHaveBeenCalled();
        });

        it('should disable submit buttons while moving', function(){
            //given
            controller.isMovingDs = true;
            var cancelBtn = element.find('#cancel-move-copy-btn').eq(0);
            var cloneBtn = element.find('#clone-ds-btn').eq(0);
            var moveBtn = element.find('#move-ds-btn').eq(0);

            //when
            scope.$digest();

            //then
            expect(cancelBtn.attr('disabled')).toBe('disabled');
            expect(cloneBtn.find('button').attr('disabled')).toBe('disabled');
            expect(moveBtn.find('button').attr('disabled')).toBe('disabled');
        });

        it('should disable submit buttons while cloning', function(){
            //given
            controller.isCloningDs = true;
            var cancelBtn = element.find('#cancel-move-copy-btn').eq(0);
            var cloneBtn = element.find('#clone-ds-btn').eq(0);
            var moveBtn = element.find('#move-ds-btn').eq(0);

            //when
            scope.$digest();

            //then
            expect(cancelBtn.attr('disabled')).toBe('disabled');
            expect(cloneBtn.find('button').attr('disabled')).toBe('disabled');
            expect(moveBtn.find('button').attr('disabled')).toBe('disabled');
        });

        it('should disable submit buttons when form is invalid', function(){
            //given
            var cancelBtn = element.find('#cancel-move-copy-btn').eq(0);
            var cloneBtn = element.find('#clone-ds-btn').eq(0);
            var moveBtn = element.find('#move-ds-btn').eq(0);
            controller.newDsName = '';

            //when
            scope.$digest();

            //then
            expect(controller.copyMoveForm.$invalid).toBe(true);
            expect(cancelBtn.attr('disabled')).toBe(undefined);
            expect(cloneBtn.find('button').attr('disabled')).toBe('disabled');
            expect(moveBtn.find('button').attr('disabled')).toBe('disabled');
        });
    });
});