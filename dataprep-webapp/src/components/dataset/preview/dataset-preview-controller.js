(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid.preview:DatasetPreviewCtrl
     * @description Dataset preview grid controller.
     * @requires data-prep.services.dataset.service:DatasetRestService
     */
    function DatasetPreviewCtrl($scope,$state,$log,$stateParams,DatasetRestService) {

        var self = this;
        self.datasetid=
        self.visible = false;
        self.metadata;
        self.selectedSheetName;
        self.records;
        self.columns;

        self.close = function() {
          $state.go('nav.home.datasets');
        };

        self.updateSheetName = function(){
          return DatasetRestService.getContent(self.datasetid, true,true,self.selectedSheetName)
              .then(function(data) {
                      drawGrid(data)
                    });
        };

        self.updateDataset = function(){
          $log.debug('updateDataset');
          DatasetRestService.update();
        };

        var drawGrid = function(data){
          self.metadata = data.metadata;
          self.selectedSheetName=data.metadata.sheetName;

          var options = {
            enableColumnReorder: false,
            editable: false,
            enableAddRow: false,
            enableCellNavigation: true,
            enableTextSelectionOnCells: false
          };

          self.columns = [];
          angular.forEach(data.columns, function(value, key) {
            this.push({id: value.id, name: value.id, field: value.id});
          }, self.columns);
          self.records=data.records;
          var grid = new Slick.Grid( $('#previewdatagrid'), self.records, self.columns, options);
          self.visible=true;
        };

        var loadPreview = function(){
            if($stateParams.datasetid) {
                self.datasetid = $stateParams.datasetid;
                return DatasetRestService.getContent(self.datasetid, true,true)
                    .then(function(data) {
                            drawGrid(data)
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