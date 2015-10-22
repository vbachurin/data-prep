(function() {
    'use strict';
    function TalendFileSelector() {
        return {
            restrict: 'E',
            templateUrl: 'components/widgets/file-selector/file-selector.html',
            replace: true,
            scope: {
                buttonDataIcon: '@',
                buttonTitle: '@',
                buttonId: '@',
                fileModel: '=',
                onFileChange: '&'
            },
            bindToController: true,
            controllerAs: 'datasetListUploadFile',
            controller: function() {},
            link: function(scope, element, attr, ctrl) {
                element.bind('click', function() {
                    document.getElementById(ctrl.buttonId).click();
                });
            }
        };
    }
    angular.module('talend.widget')
        .directive('talendFileSelector', TalendFileSelector);
})();