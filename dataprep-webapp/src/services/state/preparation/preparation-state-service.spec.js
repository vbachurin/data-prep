describe('preparation state service', function(){
    'use strict';

    var preparations = [{id: 'toto'}, {id: 'titi'}];

    beforeEach(module('data-prep.services.state'));

    afterEach(inject(function (preparationState) {
        preparationState.preparationsList = null;
    }));


    it('should update preparations list', inject(function (PreparationStateService, preparationState) {
        //given
        expect(preparationState.preparationsList).toBeNull();

        //when
        PreparationStateService.updatePreparationsList(preparations);


        //then
        expect(preparationState.preparationsList.length).toBe(2);
    }));

    it('should remove a preparation from preparations list', inject(function (PreparationStateService, preparationState) {
        //given
        expect(preparationState.preparationsList).toBeNull();
        PreparationStateService.updatePreparationsList(preparations);

        //when
        PreparationStateService.deletePreparationFromPreparationsList(0);

        //then
        expect(preparationState.preparationsList.length).toBe(1);
        expect(preparationState.preparationsList[0].id).toBe('titi');
    }));
});