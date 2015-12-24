(function() {
    'use strict';

    var preparationState = {
        preparationsList: null
    };

    function PreparationStateService() {
        return {
            //update preparations list
            updatePreparationsList: updatePreparationsList,
            deletePreparationFromPreparationsList: deletePreparationFromPreparationsList
        };

        function updatePreparationsList (preparations){
            preparationState.preparationsList = preparations;
        }

        function deletePreparationFromPreparationsList (index){
            preparationState.preparationsList.splice(index, 1);
        }

    }

    angular.module('data-prep.services.state')
        .service('PreparationStateService', PreparationStateService)
        .constant('preparationState', preparationState);
})();