(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid.preview:DatasetPreviewCtrl
     * @description Dataset preview grid controller.
     * @requires data-prep.services.dataset.service:DatasetRestService
     * @requires data-prep.services.dataset.service:DatasetListService
     */
    function DatasetPreviewCtrl($scope,$state,$log,$stateParams,DatasetRestService,DatasetListService) {

        var self = this;
        self.datasetid;
        self.visible = false;
        self.metadata;
        self.selectedSheetName;
        self.records;
        self.columns;
        self.grid;

        /**
         * @ngdoc method
         * @name close
         * @methodOf data-prep.dataset-list.controller:DatasetPreviewCtrl
         * @description triggered on closing dataset preview modal to to /datasets view
         */
        self.close = function() {
          $state.go('nav.home.datasets');
        };

        /**
         * @ngdoc method
         * @name updateSheetName
         * @methodOf data-prep.dataset-list.controller:DatasetPreviewCtrl
         * @description triggered on sheet name change (trigger redisplaying preview grid)
         */
        self.updateSheetName = function(){
          return DatasetRestService.getPreview(self.datasetid,true,self.selectedSheetName)
              .then(function(data) {
                      drawGrid(data)
                    });
        };

        /**
         * @ngdoc method
         * @name updateDataset
         * @methodOf data-prep.dataset-list.controller:DatasetPreviewCtrl
         * @description responsible for sending update dataset rest call to the backend
         */
        self.updateDataset = function(){
          $log.debug('updateDataset');
          self.metadata.sheetName = self.selectedSheetName;
          DatasetRestService.updateMetadata(self.metadata )
              .then(function(data){
                DatasetListService
                    .refreshDatasets()
                    .then(function(data){
                      $state.go('nav.home.datasets');
                    });
          });
        };

        /**
         * @ngdoc method
         * @name drawGrid
         * @methodOf data-prep.dataset-list.controller:DatasetPreviewCtrl
         * @description [PRIVATE] draw the slick grid for data preview
         */
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
          self.grid = new Slick.Grid( $('#previewdatagrid'), self.records, self.columns, options);
          self.visible=true;
        };

        /**
         * FIXME handle 301 status return, write a message to user and go to /datasets ?
         * @ngdoc method
         * @name loadPreview
         * @methodOf data-prep.dataset-list.controller:DatasetPreviewCtrl
         * @description [PRIVATE] find the dataset id in params then trigge grid draw
         */
        var loadPreview = function(){
            if($stateParams.datasetid) {
                self.datasetid = $stateParams.datasetid;
                return DatasetRestService.getPreview(self.datasetid,true)
                    .then(function(data) {
                            drawGrid(data)
                          } );
            }

        };

        // load the preview
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