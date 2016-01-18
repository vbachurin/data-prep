(function () {
    'use strict';

    var inventoryState = {
        preparations: null,
        datasets: null
    };

    function InventoryStateService() {
        return {
            setPreparations: setPreparations,
            removePreparation: removePreparation,
            setDatasets: setDatasets,
            removeDataset: removeDataset
        };

        function setPreparations(preparations) {
            inventoryState.preparations = preparations;
        }

        function removePreparation(preparation) {
            inventoryState.preparations = _.reject(inventoryState.preparations, {id: preparation.id});
        }

        function setDatasets(datasets) {
            inventoryState.datasets = datasets;
        }

        function removeDataset(dataset) {
            inventoryState.datasets = _.reject(inventoryState.datasets, {id: dataset.id});
        }
    }

    angular.module('data-prep.services.state')
        .service('InventoryStateService', InventoryStateService)
        .constant('inventoryState', inventoryState);
})();