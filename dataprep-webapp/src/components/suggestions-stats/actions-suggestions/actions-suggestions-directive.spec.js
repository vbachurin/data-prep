describe('Actions suggestions-stats directive', function() {
    'use strict';

    var scope, element, createElement, stateMock;
    var body = angular.element('body');
    beforeEach(module('data-prep.actions-suggestions', function($provide) {
        stateMock = {playground: {
            grid: {},
            suggestions: {}
        }};
        $provide.constant('state', stateMock);
    }));
    beforeEach(module('htmlTemplates'));

    beforeEach(module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en', {
            'COLON': ': ',
            'REFRESHING_WAIT': 'Fetching, please wait...'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function() {
            element = angular.element('<actions-suggestions></actions-suggestions>');
            body.append(element);
            $compile(element)(scope);
            scope.$digest();
        };
    }));

    afterEach(function() {
        scope.$destroy();
        element.remove();
    });

    it('should set column name in title', function() {
        //given
        stateMock.playground.grid.selectedColumn = {name: 'Col 1'};

        //when
        createElement();

        //then
        expect(element.find('.title').text().trim()).toBe('Col 1');
    });

    it('should render ghost when actions are being fetched', inject(function() {
        //given
        stateMock.playground.suggestions.isLoading = true;

        //when
        createElement();

        //then
        expect(element.find('#actions-ghost').length).toBe(1);
        expect(element.find('#actions-ghost').text().trim()).toBe('Fetching, please wait...');
    }));
});
