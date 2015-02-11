(function () {
    'use strict';

    function TalendModal($rootScope) {
        return {
            restrict: 'EA',
            transclude: true,
            templateUrl: 'components/widgets/modal/modal.html',
            scope: {
                state: '=',
                closeButton: '=',
                fullscreen: '='
            },
            bindToController: true,
            controllerAs: 'talendModalCtrl',
            controller: function ($scope) {
                var vm = this;

                vm.hide = function () {
                    vm.state = false;
                };

                $scope.$watch(function() {return vm.state;}, function(newValue) {
                    if (newValue) {
                        angular.element('body').addClass('modal-open');
                    } else {
                        angular.element('body').removeClass('modal-open');
                    }
                });
            },
            link: {
                post: function (scope, iElement, iAttrs, ctrl) {
                    var safeDigest = function() {
                        if(! $rootScope.$$phase) {
                            $rootScope.$apply();
                        }
                    };
                    
                    // Close action on all 'talend-modal-close' elements
                    iElement.find('.talend-modal-close').on('click', function() {
                        ctrl.hide();
                        safeDigest();
                    });

                    // stop propagation on click on inner modal to prevent modal close
                    iElement.find('.modal-inner').on('click', function (e) {
                        e.stopPropagation();
                    });
                }
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendModal', TalendModal);
})();
