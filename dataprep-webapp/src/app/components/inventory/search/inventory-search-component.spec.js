describe('Inventory Search component', () => {
    let scope, createElement, element;

    beforeEach(angular.mock.module('data-prep.inventory-search'));
    beforeEach(angular.mock.module('htmlTemplates'));

    beforeEach(inject(($rootScope, $compile) => {
        scope = $rootScope.$new();

        createElement = () => {
            const template =
                `<inventory-search>
                 </inventory-search>`;
            element = $compile(template)(scope);
            scope.$digest();
        };
    }));

    afterEach(() => {
        scope.$destroy();
        element.remove();
    });

    it('should render', () => {
        //when
        createElement();

        //then
        expect(element.find('typeahead').length).toBe(1);
    })
});