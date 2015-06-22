(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.playground.directive:Playground
     * @description This directive create the playground.
     * It only consumes {@link data-prep.services.playground.service:PlaygroundService PlaygroundService}
     * @restrict E
     */
    function Playground($timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/playground/playground.html',
            bindToController: true,
            controllerAs: 'playgroundCtrl',
            controller: 'PlaygroundCtrl',
            link:function(scope, iElement, iAttrs, ctrl){
                var attachKeyDown = function() {
                    var prepEditionInput = document.getElementById('prepEditionInputId');
                    angular.element(prepEditionInput).bind('keydown', function (e) {
                        if(e.keyCode === 13){
                            ctrl.confirmNewPrepName();
                        }else if(e.keyCode === 27){
                            $timeout(ctrl.cancelPrepNameEdition);
                        }
                        e.stopPropagation();
                    });
                };
                $timeout(attachKeyDown);
            }
        };
    }

    angular.module('data-prep.playground')
        .directive('playground', Playground);
})();