(function() {
    'use strict';
    function UploadFile() {
        return {
            restrict: 'A',
            link: function(scope, element, attr) {
                element.bind('click', function() {
                    document.getElementById(attr.uploadFile).click();
                });
            }
        };
    }
    angular.module('data-prep.dataset-list')
        .directive('uploadFile', UploadFile);
})();