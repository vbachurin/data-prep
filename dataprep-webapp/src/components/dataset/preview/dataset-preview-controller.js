(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid.preview:DatasetPreviewCtrl
     * @description Dataset preview grid controller.
     * @requires data-prep.services.dataset.service:DatasetRestService
     */
    function DatasetPreviewCtrl($scope,$stateParams,DatasetRestService) {

        var self = this;
        self.visible = false;


        var loadPreview = function(){
            if($stateParams.datasetid) {
                var datasetid = $stateParams.datasetid;
                console.log('datasetid:' + datasetid);
                return DatasetRestService.getContent(datasetid, true,true)
                    .then(function(data) {
                              $scope.metadata=data.metadata;
                              $scope.data=data;
                              self.visible=true;
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
                console.log('showPreview call get:'+this.visible);
                return this.visible;
            },
            set: function(value) {
                console.log('showPreview call set:'+value);
                this.visible = value;
            }
        });

    angular.module('data-prep.dataset-preview')
        .controller('DatasetPreviewCtrl', DatasetPreviewCtrl);

})();