(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendLoading
     * @description This directive create the loading screen. A 120ms delay is set before display, in order to
     * avoid the screen on really fast request for example<br/>
     *
     * Listeners :
     * <ul>
     *     <li>`talend.loading.start` : display the loading screen</li>
     *     <li>`talend.loading.stop` : stop the loading screen</li>
     * </ul>
     * @restrict E
     */
    function TalendLoading($rootScope, $timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/widgets/loading/loading.html',
            link: function() {
                var loadingPromise;
                var body = angular.element('body');

                $rootScope.$on('talend.loading.start', function() {
                    loadingPromise = $timeout(function() {
                        body.addClass('loading-open');
                    }, 120);
                });
                $rootScope.$on('talend.loading.stop', function() {
                    $timeout.cancel(loadingPromise);
                    body.removeClass('loading-open');
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendLoading', TalendLoading);
})();