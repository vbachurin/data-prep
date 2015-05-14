(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid.preview:DatasetPreviewCtrl
     * @description Dataset preview grid controller.
     * @requires data-prep.services.dataset.service:DatasetRestService
     */
    function DatasetPreviewCtrl($scope,$state,$stateParams,DatasetRestService) {

        var self = this;
        self.visible = false;

        self.close = function() {
          $state.go('nav.home.datasets');
        };

        var loadPreview = function(){
            if($stateParams.datasetid) {
                var datasetid = $stateParams.datasetid;
                console.log('datasetid:' + datasetid);
                return DatasetRestService.getContent(datasetid, true,true)
                    .then(function(data) {
                              $scope.metadata=data.metadata;
                              $scope.data=data;
                              self.visible=true;
                              var options = {
                                enableColumnReorder: false,
                                editable: false,
                                enableAddRow: false,
                                enableCellNavigation: true,
                                enableTextSelectionOnCells: false
                              };

                              var columns = [];
                              angular.forEach(data.columns, function(value, key) {
                                this.push({id: value.id, name: value.id, field: value.id});
                              }, columns);

                              var grid = new Slick.Grid('#previewdatagrid', data.records, columns, options);
                          });
            }

        };

        loadPreview();

    }

    Object.defineProperty(DatasetPreviewCtrl.prototype,
                          'showPreview', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.visible;
            },
            set: function(value) {
                this.visible = value;
            }
        });

    angular.module('data-prep.dataset-preview')
        .controller('DatasetPreviewCtrl', DatasetPreviewCtrl);

})();