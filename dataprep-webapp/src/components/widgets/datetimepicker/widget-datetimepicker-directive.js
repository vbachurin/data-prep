/**
 * @ngdoc directive
 * @name talend.widget.directive:TalendDatetimePicker
 * @description This directive create a datetimepicker element.
 * @restrict E
 */
export default function TalendDatetimePicker() {

    return {
        restrict: 'E',
        template: '<input class="datetimepicker" type="text" ng-model="ctrl.value" />',
        scope: {
            value: '=ngModel'
        },
        bindToController: true,
        controller: () => {},
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

            var input = iElement.find('.datetimepicker');
            input.datetimepicker({
                    format: format,
                    formatTime: formatTime,
                    formatDate: formatDate
                }
            );
            input.bind('keydown', function (event) {
                // hide calendar on 'ESC' keydown
                if (event.keyCode === 27) {
                    input.datetimepicker('hide');
                    event.stopPropagation();
                }
            });
        }
    };

}
