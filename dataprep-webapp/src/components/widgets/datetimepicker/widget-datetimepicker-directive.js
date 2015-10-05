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
     * @restrict E
     */
    function TalendDatetimePicker() {

        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'components/widgets/datetimepicker/datetimepicker.html',
            scope: {
                value: '=ngModel'
            },
            bindToController: true,
            controller: function () {},
            controllerAs: 'ctrl',
            link: function (scope, iElement, iAttrs) {
                Date.parseDate = function (input, format) {
                    return moment(input, format).toDate();
                };
                Date.prototype.dateFormat = function (format) {
                    return moment(this).format(format);
                };

                var format = iAttrs.format ? iAttrs.format : 'DD/MM/YYYY hh:mm:ss';
                var formatTime = iAttrs.formatTime ? iAttrs.formatTime : 'hh:mm:ss';
                var formatDate = iAttrs.formatDate ? iAttrs.formatDate : 'DD/MM/YYYY';

                iElement.datetimepicker({
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
                    iElement.datetimepicker('hide');
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
                    iElement.bind('keydown', function (event) {
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
                iElement.on('$destroy', function () {
                    scope.$destroy();
                });

                attachKeyMap();
            }
        };

    }

    angular.module('talend.widget')
        .directive('talendDatetimePicker', TalendDatetimePicker);
})();
