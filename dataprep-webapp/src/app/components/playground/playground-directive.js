/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

/**
 * @ngdoc directive
 * @name data-prep.playground.directive:Playground
 * @description This directive create the playground.
 * @restrict E
 */
export default function Playground($timeout) {
    'ngInject';

    return {
        restrict: 'E',
        templateUrl: 'app/components/playground/playground.html',
        bindToController: true,
        controllerAs: 'playgroundCtrl',
        controller: 'PlaygroundCtrl',
        link: function (scope, iElement, iAttrs, ctrl) {
            var container = iElement.find('.playground-container').eq(0);

            container.bind('keydown', function (e) {
                if (e.keyCode === 27 && e.target.nodeName === 'INPUT') {
                    container.focus();
                }
                else {
                    $timeout(ctrl.beforeClose);
                }
            });

            container.focus();
        }
    };
}