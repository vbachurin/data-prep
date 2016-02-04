/**
 * @ngdoc directive
 * @name talend.widget.directive:TalendConfirm
 * @description This directive create a confirmation popup.<br/>
 * Key action (inherited from {@link talend.widget.directive:TalendModal TalendModal}):
 * <ul>
 *     <li>ENTER : validate (if not disabled)</li>
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
export default function TalendConfirm() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/widgets/confirm/confirm.html',
        scope: {
            disableEnter: '=',
            texts: '='
        },
        bindToController: true,
        controller: 'TalendConfirmCtrl',
        controllerAs: 'confirmCtrl'
    };
}