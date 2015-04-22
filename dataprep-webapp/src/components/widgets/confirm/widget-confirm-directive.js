(function() {
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
     <talend-confirm
            disable-enter="disableEnter"
            texts="texts">
     </talend-confirm>
     * @param {boolean} disableEnter Disable the ENTER key support
     * @param {string[]} texts The texts ids (translation ids) to display
     */
    function TalendConfirm() {
        return {
            restrict: 'E',
            templateUrl: 'components/widgets/confirm/confirm.html',
            scope: {
                disableEnter: '=',
                texts: '='
            },
            bindToController: true,
            controller: 'TalendConfirmCtrl',
            controllerAs: 'confirmCtrl'
        };
    }

    angular.module('talend.widget')
        .directive('talendConfirm', TalendConfirm);
})();