(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.dataset-upload-list.directive:DatasetUploadList
     * @description This dataset display the upload list that had an upload error. Unlike the dataset list which is
     * based on a service property for the dataset list, this one is based on a given attribute value.
     *  <table>
     *      <tr>
     *          <th>Attributes</th>
     *          <th>Description</th>
     *      </tr>
     *      <tr>
     *          <td>datasets</td>
     *          <td>the upload error datasets</td>
     *      </tr>
     *  </table>
     * @restrict E
     * @usage
     <dataset-upload-list
            datasets="datasets">
     </dataset-upload-list>
     */
    function DatasetUploadList() {
        return {
            templateUrl: 'components/dataset/upload-list/dataset-upload-list.html',
            restrict: 'E',
            scope: {
                datasets: '='
            },
            bindToController: true,
            controllerAs: 'uploadListCtrl',
            controller: function() {}
        };
    }

    angular.module('data-prep.dataset-upload-list')
        .directive('datasetUploadList', DatasetUploadList);
})();