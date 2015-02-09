(function () {
  'use strict';

  function TalendModal($rootScope) {
    return {
      restrict: 'EA',
      transclude: true,
      templateUrl: 'components/widgets/modal.html',
      scope: {
        state: '='
      },
      bindToController: true,
      controller: function () {
        var vm = this;

        vm.safeDigest = function () {
          if (!$rootScope.$$phase) {
            $rootScope.$digest();
          }
        };

        vm.hide = function() {
          angular.element('.modal-state:checked').prop('checked', false).change();
        };
      },
      controllerAs: 'talendModalCtrl',
      link: {
        post: function(scope, iElement, iAttr, ctrl) {
          angular.element('.modal-state').on('change', function (event) {
            if (event.target.checked) {
              angular.element('body').addClass('modal-open');
            } else {
              angular.element('body').removeClass('modal-open');
            }
            ctrl.state = event.target.checked;
            ctrl.safeDigest();
          });

          angular.element('.talend-modal-action').on('click', function () {
            ctrl.hide();
          });

          angular.element('.modal-inner').on('click', function (e) {
            e.stopPropagation();
          });

          angular.element('.modal-window').on('click', function () {
            ctrl.hide();
          });
        }
      }
    };
  }

  angular.module('talend.widget')
    .directive('talendModal', TalendModal);
})();
