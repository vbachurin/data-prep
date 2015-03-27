describe('Preparation list directive', function() {
    'use strict';

    var scope, createElement, element;
    var allPreparations = [
        {
            'id': 'ab136cbf0923a7f11bea713adb74ecf919e05cfa',
            'dataSetId': 'ddb74c89-6d23-4528-9f37-7a9860bb468e',
            'author': 'anonymousUser',
            'creationDate': 1427447300300,
            'steps': [
                '35890aabcf9115e4309d4ce93367bf5e4e77b82a',
                '4ff5d9a6ca2e75ebe3579740a4297fbdb9b7894f',
                '8a1c49d1b64270482e8db8232357c6815615b7cf',
                '599725f0e1331d5f8aae24f22cd1ec768b10348d'
            ],
            'actions': [
                {
                    'action': 'lowercase',
                    'parameters': {
                        'column_name': 'birth'
                    }
                },
                {
                    'action': 'uppercase',
                    'parameters': {
                        'column_name': 'country'
                    }
                },
                {
                    'action': 'cut',
                    'parameters': {
                        'pattern': '.',
                        'column_name': 'first_item'
                    }
                }
            ]
        },
        {
            'id': 'fbaa18e82e913e97e5f0e9d40f04413412be1126',
            'dataSetId': '8ec053b1-7870-4bc6-af54-523be91dc774',
            'author': 'anonymousUser',
            'creationDate': 1427447330693,
            'steps': [
                '47e2444dd1301120b539804507fd307072294048',
                'ae1aebf4b3fa9b983c895486612c02c766305410',
                '24dcd68f2117b9f93662cb58cc31bf36d6e2867a',
                '599725f0e1331d5f8aae24f22cd1ec768b10348d'
            ],
            'actions': [
                {
                    'action': 'cut',
                    'parameters': {
                        'pattern': '-',
                        'column_name': 'birth'
                    }
                },
                {
                    'action': 'fillemptywithdefault',
                    'parameters': {
                        'default_value': 'N/A',
                        'column_name': 'state'
                    }
                },
                {
                    'action': 'uppercase',
                    'parameters': {
                        'column_name': 'lastname'
                    }
                }
            ]
        }
    ];

    beforeEach(module('data-prep.preparation-list'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile, $q, PreparationService) {
        scope = $rootScope.$new();
        createElement = function() {
            element = angular.element('<preparation-list></preparation-list>');
            $compile(element)(scope);
            scope.$digest();
        };

        spyOn(PreparationService, 'getPreparations').and.returnValue($q.when({data: allPreparations}));
    }));

    afterEach(function() {
        scope.$destroy();
        element.remove();
    });

    it('should render preparations tiles', inject(function() {
        //given

        //when
        createElement();

        //then
        var preparationTiles = element.find('.preparation');
        expect(preparationTiles.length).toBe(2);

        var description = preparationTiles.eq(0).find('.description').eq(0).text();
        expect(description).toContain('anonymousUser'); //owner
        expect(description).toContain('4'); //steps
        expect(description).toContain('27'); //creation date day
        expect(description).toContain('2015'); //creation date year
    }));
});