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
    function TalendDatetimePicker() {

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
            link: function (scope, element, attributes) {
              Date.parseDate = function( input, format ){
                return moment(input,format).toDate();
              };
              Date.prototype.dateFormat = function( format ){
                return moment(this).format(format);
              };

              var format = attributes.format?attributes.format:'DD/MM/YYYY hh:mm:ss';
              var formatTime = attributes.formatTime?attributes.formatTime:'hh:mm:ss';
              var formatDate = attributes.formatDate?attributes.formatDate:'DD/MM/YYYY';

              var dateInput = angular.element('.datetimepicker');
              dateInput.datetimepicker({
                    format: format,
                    formatTime: formatTime,
                    formatDate:formatDate
                  }
              );

            }
        };

    }

    angular.module('talend.widget')
        .directive('talendDatetimePicker', TalendDatetimePicker);
})();
