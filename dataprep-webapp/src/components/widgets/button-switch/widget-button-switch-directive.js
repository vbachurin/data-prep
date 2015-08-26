(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendConfirm
     * @description This directive create a badge with editable content.<br/>
     * Key action (inherited from {@link talend.widget.directive:TalendModal TalendModal}):
     * <ul>
     *     <li>ENTER : validate (is not disabled)</li>
     *     <li>ESC : dismiss the modal</li>
     * </ul>
     * @restrict E
     * @usage
     *
     <talend-button-switch
     button-values="valuesArray"
     button-current-text="texts"
     button-action="clickAction">
     </talend-button-switch>

     * @param {boolean} disableEnter Disable the ENTER key support
     * @param {string[]} texts The texts ids (translation ids) to display
     */
    function TalendButtonSwitch() {
        return {
            restrict: 'E',
            templateUrl: 'components/widgets/button-switch/button-switch.html',
            scope: {
                currentValue: '=',
                displayKey: '@',
                values: '=',
                changeAction: '&'
            },
            bindToController: true,
            controller: function () {},
            controllerAs: 'buttonSwitchCtrl',
            link: function (scope, iElement, attrs, ctrl) {
                function next() {
                    var index = ctrl.values.indexOf(ctrl.currentValue);
                    return (index === -1 || index >= ctrl.values.length - 1) ?
                        ctrl.values[0] :
                        ctrl.values[index + 1];
                }

                iElement.on('click', function () {
                    ctrl.changeAction({selected: next()});
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendButtonSwitch', TalendButtonSwitch);
})();