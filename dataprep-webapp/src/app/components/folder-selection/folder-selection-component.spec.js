/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('folder selection component', function() {

    beforeEach(angular.mock.module('data-prep.folder-selection'));
    beforeEach(angular.mock.module('htmlTemplates'));

    var createElement, scope, element, controller;


    var folders = [// the json objects are in a tree way
        {
            id: '',
            path: '',
            level: 0,
            alreadyToggled: true,
            display: true,
            collapsed: false,
            name: 'Home'
        },
        {
            name: '1-folder1',
            path: '1-folder1',
            level: 1,
            display: true
        },
        {
            path: '1-folder1/2-folder11',
            name: '2-folder11',
            level: 2,
            display: true
        },
        {
            path: '1-folder1/2-folder12',
            name: '2-folder12',
            level: 2,
            display: true
        },
        {
            path: '1-folder1/2-folder13',
            name: '2-folder13',
            level: 2,
            display: true
        },
        {
            name: '1-folder2',
            path: '1-folder2',
            level: 1,
            display: false
        }
    ];

    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function () {
            element = angular.element(
                `<folder-selection
                    ng-model="destinationFolder"
                ></folder-selection>`);

            $compile(element)(scope);
            scope.$digest();
            controller = element.controller('folderSelection');
            return element;
        };
    }));

    afterEach(function () {
        scope.$destroy();
        element.remove();
    });

    describe('display folder selection content', function(){
        it('should render input search', function(){
            //when
            createElement();

            //then
            expect(element.find('input').length).toBe(1);
        });

        it('should render folders tree', inject(function($rootScope){
            //given
            createElement();
            controller.folderItems = folders;

            //when
            $rootScope.$digest();

            //then
            expect(element.find('folder-item').length).toBe(folders.length - 1);
        }));
    });
});