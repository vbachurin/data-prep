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
    function TalendLoading($rootScope) {
        return {
            restrict: 'E',
            templateUrl: 'components/widgets/loading/loading.html',
            link: function(scope, iElement) {
                var loadingTimeout;

                $rootScope.$on('talend.loading.start', function() {
                    clearTimeout(loadingTimeout);
                    iElement[0].className = 'is-loading';

                    loadingTimeout = setTimeout(function() {
                        iElement[0].className = 'is-loading show-loading';
                    }, 200);
                });
                $rootScope.$on('talend.loading.stop', function() {
                    clearTimeout(loadingTimeout);
                    iElement[0].className = '';
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendLoading', TalendLoading);
})();