(function () {
    'use strict';

    function TransformParams() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation-params/transformation-params.html',
            replace: true,
            scope: {
                menu: '=',
                onSubmit: '&'
            },
            bindToController: true,
            controllerAs: 'paramsCtrl',
            controller: 'TransformParamsCtrl'
        };
    }

    angular.module('data-prep.transformation-params')
        .directive('transformParams', TransformParams);
})();