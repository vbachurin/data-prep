/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

import moment from 'moment';

/**
 * @ngdoc directive
 * @name talend.widget.directive:TalendDatetimePicker
 * @description This directive create a datetimepicker element.
 * @restrict E
 * @usage
 <talend-datetime-picker
     ng-model="ctrl.value"
     on-select="ctrl.onSelect"
     on-blur="ctrl.onBlur"
     format="DD/MM/YYYY"
     is-date-time></talend-datetimepicker>
 * @param {string}   value Variable to bind input ngModel
 * @param {function} onSelect Event handler when date is picked
 * @param {function} onBlur Event handler when input is blurred
 * @param {string} format MomentJS date format
 */
export default function TalendDatetimePicker($timeout) {
    'ngInject';

    return {
        restrict: 'E',
        template: '<input class="datetimepicker" type="text" ng-blur="ctrl.onBlur()" ng-model="ctrl.value" />',
        scope: {
            value: '=ngModel',
            onSelect: '&',
            onBlur: '&'
        },
        bindToController: true,
        controller: () => {
        },
        controllerAs: 'ctrl',
        link: function (scope, iElement, iAttrs, ctrl) {
            Date.parseDate = function (input, format) {
                return moment(input, format).toDate();
            };
            Date.prototype.dateFormat = function (format) {
                return moment(this).format(format);
            };

            const format = iAttrs.format ? iAttrs.format : 'DD/MM/YYYY HH:mm:ss';
            const formatTime = iAttrs.formatTime ? iAttrs.formatTime : 'HH:mm:ss';
            const formatDate = iAttrs.formatDate ? iAttrs.formatDate : 'DD/MM/YYYY';

            function onSelectDate() {
                $timeout(() => {
                    ctrl.onSelect();
                });
            }

            const input = iElement.find('.datetimepicker');
            input.datetimepicker({
                lang: 'en',
                format: format,
                formatDate: formatDate,
                formatTime: formatTime,
                timepicker: _.has(iAttrs, 'isDateTime'),
                onSelectDate: onSelectDate
            });

            input.bind('keydown', (event) => {
                // hide calendar on 'ESC' keydown
                if (event.keyCode === 27) {
                    input.datetimepicker('hide');
                    event.stopPropagation();
                }
            });

            scope.$on('$destroy', () => {
                input.datetimepicker('destroy');
            });
        }
    };
}
