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
                Date.parseDate = function (input, format) {
                    return moment(input, format).toDate();
                };
                Date.prototype.dateFormat = function (format) {
                    return moment(this).format(format);
                };

                var format = attributes.format ? attributes.format : 'DD/MM/YYYY hh:mm:ss';
                var formatTime = attributes.formatTime ? attributes.formatTime : 'hh:mm:ss';
                var formatDate = attributes.formatDate ? attributes.formatDate : 'DD/MM/YYYY';

                var dateInput = angular.element('.datetimepicker');
                dateInput.datetimepicker({
                        format: format,
                        formatTime: formatTime,
                        formatDate: formatDate
                    }
                );

                /**
                 * @ngdoc method
                 * @name hideCalendar
                 * @methodOf talend.widget.directive:TalendDatetimePicker
                 * @description [PRIVATE] hide calendar widget
                 */
                var hideCalendar = function () {
                    dateInput.datetimepicker('hide');
                };

                /**
                 * @ngdoc method
                 * @name attachKeyMap
                 * @methodOf talend.widget.directive:TalendDatetimePicker
                 * @description [PRIVATE] Attach ESC actions
                 * <ul>
                 *     <li>ESC : hide the calendar</li>
                 * </ul>
                 */
                var attachKeyMap = function () {
                    dateInput.bind('keydown', function (event) {

                        // hide calendar on 'ESC' keydown
                        if (event.keyCode === 27) {
                            hideCalendar();
                            event.stopPropagation();
                        }
                    });
                };

                /**
                 * on element destroy, we destroy the scope which unregister body mousedown
                 */
                element.on('$destroy', function () {
                    scope.$destroy();
                });
                scope.$on('$destroy', function () {
                    dateInput.off('mousedown');
                });

                attachKeyMap();
            }
        };

    }

    angular.module('talend.widget')
        .directive('talendDatetimePicker', TalendDatetimePicker);
})();
