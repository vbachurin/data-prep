(function() {
    'use strict';

    function TalendSlidable() {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            templateUrl: '/components/widgets/slidable/slidable.html',
            scope: {
                side: '@',
                visible: '='
            },
            bindToController: true,
            controllerAs: 'slidableCtrl',
            controller: function() {
                var vm = this;

                vm.toggle = function() {
                    vm.visible = ! vm.visible;
                };
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendSlidable', TalendSlidable);
})();