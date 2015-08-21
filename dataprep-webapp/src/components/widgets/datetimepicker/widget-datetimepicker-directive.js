(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendDatetimePicker
     * @description This directive create a datetimepicker element.<br/>
     * Key action :
     * <ul>
     *     <li>ESC : close the dropdown</li>
     * </ul>
     * @restrict EA
     * @usage
     */
    function TalendDatetimePicker($timeout) {

        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            templateUrl: 'components/widgets/datetimepicker/datetimepicker.html',
            scope: {
                closeOnSelect: '=',
                onOpen: '&',
                value: '=ngModel'
            },
            bindToController: true,
            controller: function () {
            },
            controllerAs: 'ctrl',
            link: function () {

              Date.parseDate = function( input, format ){
                return moment(input,format).toDate();
              };
              Date.prototype.dateFormat = function( format ){
                return moment(this).format(format);
              };
              var dateInput = angular.element('.datetimepicker');
              dateInput.datetimepicker({
                    format:'DD/MM/YYYY hh:mm',
                    formatTime:'hh:mm',
                    formatDate:'DD/MM/YYYY'
                  }
              );

            }
        };

    }

    angular.module('talend.widget')
        .directive('talendDatetimePicker', TalendDatetimePicker);
})();
