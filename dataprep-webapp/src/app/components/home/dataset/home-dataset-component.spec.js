/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Home Dataset Component', () => {
    'use strict';

    let scope, createElement, element, StateMock;
    beforeEach(angular.mock.module('htmlTemplates'));

    beforeEach(angular.mock.module('data-prep.home', ($provide) => {
        StateMock = {
            dataset: { uploadingDatasets: [] },
            inventory: {},
            import: {importTypes : [] }
        };
        $provide.constant('state', StateMock);
    }));

    beforeEach(inject(($rootScope, $compile, StateService, DatasetService, PreparationService) => {
        scope = $rootScope.$new();
        createElement = () => {
            element = angular.element('<home-dataset></home-dataset>');
            $compile(element)(scope);
            scope.$digest();
            return element;
        };

        spyOn(StateService, 'setFetchingInventoryDatasets').and.returnValue();
        spyOn(DatasetService, 'init').and.returnValue();
        spyOn(PreparationService, 'refreshPreparations').and.returnValue();

    }));

    afterEach(() => {
        scope.$destroy();
        element.remove();
    });


    it('should NOT render dataset-upload-list', inject(() => {
        //when
        createElement();

        //then
        expect(element.find('dataset-upload-list').length).toBe(0);
    }));

    it('should render dataset-upload-list', inject(() => {
        //given
        StateMock.dataset.uploadingDatasets = [{id: 'datasetId'}];

        //when
        createElement();

        //then
        expect(element.find('dataset-upload-list').length).toBe(1);
    }));

    it('should render dataset-header', inject(() => {
        //when
        createElement();

        //then
        expect(element.find('dataset-header').length).toBe(1);
    }));

    it('should NOT render dataset-header', inject(() => {
        //given
        StateMock.inventory.isFetchingDatasets = true;

        //when
        createElement();

        //then
        expect(element.find('dataset-header').length).toBe(0);
    }));

    it('should render dataset-list', inject(() => {
        //when
        createElement();

        //then
        expect(element.find('dataset-list').length).toBe(1);
    }));

});
