describe('Editable regex widget controller', function() {
    'use strict';

    var scope, createController;

    beforeEach(module('talend.widget'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();
        createController = function() {
            return $controller('TalendEditableRegexCtrl', {
                $scope: scope
            });
        };
    }));

    describe('init', function() {
        it('should init types array', function() {
            //when
            var ctrl = createController();

            //then
            expect(ctrl.types.length).toBe(4);
            expect(ctrl.types[0].label).toBe('Contains');
            expect(ctrl.types[1].label).toBe('Starts With');
            expect(ctrl.types[2].label).toBe('Ends With');
            expect(ctrl.types[3].label).toBe('RegEx');
        });

        it('should init selected type with "contains" type', function() {
            //when
            var ctrl = createController();

            //then
            expect(ctrl.selectedType.label).toBe('Contains');
        });


        it('should init entered text with empty string', function() {
            //when
            var ctrl = createController();

            //then
            expect(ctrl.regex).toBe('');
        });
    });

    describe('update model', function() {
        it('should not adapt regex value if entered value is falsy', function() {
            //given
            var ctrl = createController();
            ctrl.selectedType = _.find(ctrl.types, {label: 'Contains'});
            ctrl.regex = '';

            ctrl.value = '';

            //when
            ctrl.updateModel();

            //then
            expect(ctrl.value).toBe(''); // not .*(empty).*
        });

        it('should escape and adapt "contains" regex', function() {
            //given
            var ctrl = createController();
            ctrl.selectedType = _.find(ctrl.types, {label: 'Contains'});
            ctrl.regex = 'aze?rty';

            expect(ctrl.value).toBeFalsy();

            //when
            ctrl.updateModel();

            //then
            expect(ctrl.value).toBe('.*aze[?]rty.*');
        });

        it('should escape and adapt "starts with" regex', function() {
            //given
            var ctrl = createController();
            ctrl.selectedType = _.find(ctrl.types, {label: 'Starts With'});
            ctrl.regex = 'aze?rty';

            expect(ctrl.value).toBeFalsy();

            //when
            ctrl.updateModel();

            //then
            expect(ctrl.value).toBe('^aze[?]rty.*');
        });

        it('should escape and adapt "ends with" regex', function() {
            //given
            var ctrl = createController();
            ctrl.selectedType = _.find(ctrl.types, {label: 'Ends With'});
            ctrl.regex = 'aze?rty';

            expect(ctrl.value).toBeFalsy();

            //when
            ctrl.updateModel();

            //then
            expect(ctrl.value).toBe('.*aze[?]rty$');
        });

        it('should return entered text on "regex" type adaptation', function() {
            //given
            var ctrl = createController();
            ctrl.selectedType = _.find(ctrl.types, {label: 'RegEx'});
            ctrl.regex = 'aze?rty';

            expect(ctrl.value).toBeFalsy();

            //when
            ctrl.updateModel();

            //then
            expect(ctrl.value).toBe('aze?rty');
        });
    });

    describe('regex type change', function() {
        it('should set selected type', function() {
            //given
            var ctrl = createController();
            var nextType = _.find(ctrl.types, {label: 'Starts With'});
            ctrl.selectedType = _.find(ctrl.types, {label: 'Contains'});

            //when
            ctrl.setSelectedType(nextType);

            //then
            expect(ctrl.selectedType).toBe(nextType);
        });

        it('should update model', function() {
            //given
            var ctrl = createController();
            var nextType = _.find(ctrl.types, {label: 'Starts With'});
            ctrl.selectedType = _.find(ctrl.types, {label: 'Contains'});
            ctrl.regex = 'aze?rty';
            ctrl.value = '';

            //when
            ctrl.setSelectedType(nextType);

            //then
            expect(ctrl.value).toBe('^aze[?]rty.*');
        });
    });
});