/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Home Preparation Component', () => {
    'use strict';

    let scope;
    let createElement;
    let element;
    let StateMock;
    

    beforeEach(angular.mock.module('data-prep.home', ($provide) => {
        StateMock = {
            inventory: {}
        };
        $provide.constant('state', StateMock);
    }));

    beforeEach(inject(($q, $rootScope, $compile, StateService, FolderService) => {
        scope = $rootScope.$new();
        createElement = () => {
            element = angular.element('<home-preparation></home-preparation>');
            $compile(element)(scope);
            scope.$digest();
            return element;
        };

        spyOn(StateService, 'setFetchingInventoryPreparations').and.returnValue();
        spyOn(FolderService, 'init').and.returnValue($q.when());
    }));

    afterEach(() => {
        scope.$destroy();
        element.remove();
    });


    it('should render preparation-header', () => {
        //when
        createElement();

        //then
        expect(element.find('preparation-header').length).toBe(1);
    });

    it('should NOT render preparation-header', () => {
        //given
        StateMock.inventory.isFetchingPreparations = true;

        //when
        createElement();

        //then
        expect(element.find('preparation-header').length).toBe(0);
    });

    it('should render preparation-list', () => {
        //when
        createElement();

        //then
        expect(element.find('preparation-list').length).toBe(1);
    });
});
