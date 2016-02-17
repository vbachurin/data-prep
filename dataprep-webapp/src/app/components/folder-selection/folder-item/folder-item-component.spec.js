/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('folder Item Component', function(){

    beforeEach(angular.mock.module('data-prep.folder-selection'));
    beforeEach(angular.mock.module('htmlTemplates'));

    let createElement, scope, element;

    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function () {
            element = angular.element(

                `<folder-item
                    item="item"
                    on-toggle="toggle(folder)"
                    on-select="chooseFolder(dest)"
                ></folder-item>`);

            $compile(element)(scope);
            scope.$digest();
            return element;
        };
    }));

    afterEach(function () {
        scope.$destroy();
        element.remove();
    });

    it('should show the name of the item', function(){
        //given
        scope.item = {
            name: '1-folder1',
            path: '1-folder1',
            level: 1,
            searchResult: false
        };

        //when
        createElement();

        //then
        expect(element.find('span').eq(1).find('span').eq(0).text().trim()).toBe(scope.item.name);
    });

    describe('render a folder', function(){
        it('should highlight selected folder', function(){
            //given
            scope.item = {
                name: '1-folder1',
                path: '1-folder1',
                level: 1,
                selected: true
            };

            //when
            createElement();

            //then
            expect(element.find('li').eq(0).hasClass('folder-selected')).toBe(true);
        });

        it('should not highlight a non-selected folder', function(){
            //given
            scope.item = {
                name: '1-folder1',
                path: '1-folder1',
                level: 1,
                selected: false
            };

            //when
            createElement();

            //then
            expect(element.find('li').eq(0).hasClass('folder-selected')).toBe(false);
        });

        it('should render a collapsed folder', function(){
            //given
            scope.item = {
                name: '1-folder1',
                path: '1-folder1',
                level: 1,
                collapsed: true
            };

            //when
            createElement();

            //then
            expect(element.find('a > span > i[data-icon="J"]').length).toBe(0);
            expect(element.find('a > span > i[data-icon="I"]').length).toBe(1);
            expect(element.find('li > a > span > img').eq(0).attr('src')).toBe('assets/images/folder/folder_close_small-icon.png');
        });

        it('should render a not collapsed folder', function(){
            //given
            scope.item = {
                name: '1-folder1',
                path: '1-folder1',
                level: 1,
                collapsed: false
            };

            //when
            createElement();

            //then
            expect(element.find('a > span > i[data-icon="I"]').length).toBe(0);
            expect(element.find('a > span > i[data-icon="J"]').length).toBe(1);
            expect(element.find('li > a > span > img').eq(0).attr('src')).toBe('assets/images/folder/folder_open_small-icon.png');
        });


        it('should render a folder with children', function(){
            //given
            scope.item = {
                name: '1-folder1',
                path: '1-folder1',
                level: 1,
                hasNoChildren: false
            };

            //when
            createElement();

            //then
            expect(element.find('.empty-caret').length).toBe(0);
        });



        it('should render the folder with the right margin-left', function(){
            //given
            scope.item = {
                name: '5-folder1',
                path: '///5-folder1',
                level: 5,
                showFolder: true
            };

            //when
            createElement();

            //then
            expect(element.find('li').eq(0).css('margin-left')).toBe(20 * scope.item.level + 'px');
        });
    });

    describe('render a caret', function(){

        it('should render a folder with no children', function(){
            //given
            scope.item = {
                name: '1-folder1',
                path: '1-folder1',
                level: 1,
                hasNoChildren: true
            };

            //when
            createElement();

            //then
            expect(element.find('.empty-caret').length).toBe(1);
        });

        it('should render a folder with children', function(){
            //given
            scope.item = {
                name: '1-folder1',
                path: '1-folder1',
                level: 1,
                hasNoChildren: false
            };

            //when
            createElement();

            //then
            expect(element.find('.empty-caret').length).toBe(0);
        });
    });

    describe('render folder item with right level', function(){
        it('should render the folder with the right margin-left', function(){
            //given
            scope.item = {
                name: '5-folder1',
                path: '///5-folder1',
                level: 5,
                showFolder: true
            };

            //when
            createElement();

            //then
            expect(element.find('li').eq(0).css('margin-left')).toBe(20 * scope.item.level + 'px');
        });
    });
    describe('clicks', function(){
        var controller;
        beforeEach(() => {
            createElement();
            controller = element.controller('folderItem');
            controller.onToggle = jasmine.createSpy('onToggle');
            controller.onSelect = jasmine.createSpy('onSelect');
        });

        describe('toggle', function(){
            it('should trigger callback on closed caret simple click', inject(function($rootScope){
                //given
                controller.item = {
                    name: '1-folder1',
                    path: '1-folder1',
                    level: 1,
                    collapsed: true
                };
                $rootScope.$digest();

                //when
                element.find('a > span').eq(0).click();

                //then
                expect(controller.onToggle).toHaveBeenCalledWith({folder: controller.item});
            }));

            it('should trigger callback on opened caret simple click', inject(function($rootScope){
                //given
                controller.item = {
                    name: '1-folder1',
                    path: '1-folder1',
                    level: 1,
                    collapsed: false
                };
                $rootScope.$digest();

                //when
                element.find('a > span').eq(0).click();

                //then
                expect(controller.onToggle).toHaveBeenCalledWith({folder: controller.item});
            }));

            it('should trigger callback on line double click', inject(function($rootScope){
                //given
                controller.item = {
                    name: '1-folder1',
                    path: '1-folder1',
                    level: 1,
                    collapsed: true
                };
                $rootScope.$digest();

                //when
                element.find('li').eq(0).dblclick();

                //then
                expect(controller.onToggle).toHaveBeenCalledWith({folder: controller.item});
            }));
        });

        describe('select', function(){
            it('should trigger callback on name click', inject(function($rootScope){
                //given
                controller.item = {
                    name: '1-folder1',
                    path: '1-folder1',
                    level: 1,
                    collapsed: true
                };
                $rootScope.$digest();

                //when
                element.find('span').eq(1).click();

                //then
                expect(controller.onSelect).toHaveBeenCalledWith({dest: controller.item});
            }));

            it('should trigger callback on folder image simple click', inject(function($rootScope){
                //given
                controller.item = {
                    name: '1-folder1',
                    path: '1-folder1',
                    level: 1,
                    collapsed: true
                };
                $rootScope.$digest();

                //when
                element.find('li > a > span > img').eq(0).click();

                //then
                expect(controller.onSelect).toHaveBeenCalledWith({dest: controller.item});
            }));
        });
    });
});