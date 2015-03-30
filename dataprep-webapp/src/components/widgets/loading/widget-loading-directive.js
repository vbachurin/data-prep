(function() {
    'use strict';

    function TalendLoading($rootScope) {
        return {
            restrict: 'E',
            templateUrl: 'components/widgets/loading/loading.html',
            link: function() {
                $rootScope.$on('talend.loading.start', function() {
                    angular.element('body').addClass('loading-open');
                });
                $rootScope.$on('talend.loading.stop', function() {
                    angular.element('body').removeClass('loading-open');
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendLoading', TalendLoading);
})();