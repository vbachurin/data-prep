(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.playground.directive:Playground
     * @description This directive create the playground.
     * @restrict E
     */
    function Playground($timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/playground/playground.html',
            bindToController: true,
            controllerAs: 'playgroundCtrl',
            controller: 'PlaygroundCtrl',
            link: function (scope, iElement, iAttrs, ctrl) {
                var attachKeyDown = function () {
                    angular.element('body > talend-modal .steps-header').find('input#prepNameInput')
                        .bind('keydown', function(e) {
                            e.stopPropagation();

                            if (e.keyCode === 13) {
                                ctrl.confirmPrepNameEdition();
                            }
                            else if (e.keyCode === 27) {
                                $timeout(ctrl.cancelPrepNameEdition);
                            }
                        });
                };
                $timeout(attachKeyDown);
            }
        };
    }

    angular.module('data-prep.playground')
        .directive('playground', Playground);
})();