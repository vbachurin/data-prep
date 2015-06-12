(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid.controller:DatagridCtrl
     * @description Dataset grid controller.
     * @requires data-prep.services.transformation.service:ColumnSuggestionService
     */
    function ColumnSuggestionsCtrl(ColumnSuggestionService) {
        var vm = this;
        vm.columnSuggestionService = ColumnSuggestionService;

        vm.select = function select(transfo) {
            if(transfo.dynamic) {
                //TODO JSO : manage dynamic params
                console.log('Open dynamic params for transfo ' + transfo.name);
            }
            else {
                //TODO JSO : call this simple transformation
                vm.transformClosure(transfo)();
            }
        };

        vm.transformClosure = function transform(transfo) {
            return function(params) {
                //TODO JSO : apply transfo with params on current column
                console.log('Apply ' + transfo.name + ' with params ' + JSON.stringify(params));
            };
        };
    }

    /**
     * @ngdoc property
     * @name datasets
     * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
     * @description The dataset list.
     * This list is bound to {@link data-prep.services.dataset.service:DatasetService DatasetService}.datasetsList()
     */
    Object.defineProperty(ColumnSuggestionsCtrl.prototype,
        'column', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.columnSuggestionService.currentColumn;
            }
        });

    /**
     * @ngdoc property
     * @name datasets
     * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
     * @description The dataset list.
     * This list is bound to {@link data-prep.services.dataset.service:DatasetService DatasetService}.datasetsList()
     */
    Object.defineProperty(ColumnSuggestionsCtrl.prototype,
        'suggestions', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.columnSuggestionService.transformations;
            }
        });

    angular.module('data-prep.column-suggestions')
        .controller('ColumnSuggestionsCtrl', ColumnSuggestionsCtrl);
})();