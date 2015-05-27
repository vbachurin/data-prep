(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid.preview:DatasetPreviewXlsCtrl
     * @description Dataset preview grid controller.
     * @requires data-prep.services.dataset.service:DatasetRestService
     * @requires data-prep.services.dataset.service:DatasetListService
     */
    function DatasetPreviewXlsCtrl($rootScope,$scope,$state,$log,$stateParams,DatasetRestService,DatasetListService) {

        var self = this;
        self.datasetid;
        self.visible = false;
        self.metadata;
        self.selectedSheetContent={};
        self.records;
        self.columns;
        self.grid;

        /**
         * @ngdoc method
         * @name close
         * @methodOf data-prep.dataset-list.controller:DatasetPreviewXlsCtrl
         * @description triggered on closing dataset preview modal to to /datasets view
         */
        self.close = function() {
          $state.go('nav.home.datasets');
        };

        /**
         * @methodOf data-prep.dataset-list.controller:DatasetPreviewXlsCtrl
         * @description triggered on non 200 http response. Can happen when dataset has been modified in the backend so
         * we redirect to all datasets view and updating list
         * @param res rest call response
         */
        var previewPremiseError = function(res){
          $log.debug('previewPremiseError status:'+res.status);
          if (res.status === 301){
            $rootScope.$emit('talend.preview.draft.validated');
            DatasetListService
                .refreshDatasets()
                .then(function(data){
                        $state.go('nav.home.datasets');
                      });
            return;
          }
          $rootScope.$emit('talend.preview.error');
        };

        /**
         * @ngdoc method
         * @name updateSheetName
         * @methodOf data-prep.dataset-list.controller:DatasetPreviewXlsCtrl
         * @description triggered on sheet name change (trigger redisplaying preview grid)
         */
        self.updateSheetName = function(){
          return DatasetRestService.getPreview(self.datasetid,true,self.selectedSheetContent.name)
              .then(function(data) {
                      drawGrid(data);
                    },previewPremiseError);
        };

        /**
         * @ngdoc method
         * @name updateDataset
         * @methodOf data-prep.dataset-list.controller:DatasetPreviewXlsCtrl
         * @description responsible for sending update dataset rest call to the backend
         */
        self.updateDataset = function(){
          $log.debug('updateDataset');
          self.metadata.sheetName = self.selectedSheetContent.name;
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
         * @methodOf data-prep.dataset-list.controller:DatasetPreviewXlsCtrl
         * @description [PRIVATE] draw the slick grid for data preview
         */
        var drawGrid = function(data){
          self.metadata = data.metadata;
          self.selectedSheetContent.sheetName=data.metadata.sheetName;

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
         * @methodOf data-prep.dataset-list.controller:DatasetPreviewXlsCtrl
         * @description [PRIVATE] find the dataset id in params then trigge grid draw
         */
        var loadPreview = function(){
          $log.debug('loadPreview');
          if($stateParams.datasetid) {
              self.datasetid = $stateParams.datasetid;
              $log.debug('type:'+$stateParams.type);
              return DatasetRestService.getPreview(self.datasetid,true)
                  .then(function(data) {
                          $log.debug('before drawGrid');
                          drawGrid(data)
                        },previewPremiseError);
          }

        };

        // load the preview
        loadPreview();

    }

    Object.defineProperty(DatasetPreviewXlsCtrl.prototype,
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

    angular.module('data-prep.dataset-preview-xls')
        .controller('DatasetPreviewXlsCtrl', DatasetPreviewXlsCtrl);

})();