(function() {
    'use strict';

    function TalendLoading($rootScope, $timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/widgets/loading/loading.html',
            link: function() {
                var loadingPromise;

                $rootScope.$on('talend.loading.start', function() {
                    loadingPromise = $timeout(function() {
                        angular.element('body').addClass('loading-open');
                    }, 120);
                });
                $rootScope.$on('talend.loading.stop', function() {
                    $timeout.cancel(loadingPromise);
                    angular.element('body').removeClass('loading-open');
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendLoading', TalendLoading);
})();