/**
 * @ngdoc directive
 * @name data-prep.history-control.directive:HistoryControl
 * @description This directive add the buttons and shortcuts to control the history actions
 * @restrict E
 */
export default function HistoryControl($document, HistoryService) {
    'ngInject';

    return {
        restrict: 'E',
        templateUrl: 'app/components/history-control/history-control.html',
        controller: function () {
            this.service = HistoryService;
        },
        controllerAs: 'historyCtrl',
        link: function () {
            //attach Ctrl+Z and Ctrl+Y event
            $document.on('keydown', function (evtobj) {

                //CTRL+Z
                if (evtobj.keyCode === 90 && evtobj.ctrlKey) {
                    HistoryService.undo();
                }
                //Ctrl+Y
                else if (evtobj.keyCode === 89 && evtobj.ctrlKey) {
                    HistoryService.redo();
                }
            });
        }
    };
}