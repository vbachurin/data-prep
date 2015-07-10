(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.history-control.directive:HistoryControl
     * @description This directive add the buttons and shortcuts to control the history actions
     * @restrict E
     */
    function HistoryControl($document) {
        return {
            restrict: 'E',
            templateUrl: 'components/history-control/history-control.html',
            controller: 'HistoryControlCtrl',
            controllerAs: 'historyCtrl',
            link: function(scope, iElement, iAttrs, ctrl) {
                //attach Ctrl+Z and Ctrl+Y event
                $document.on('keydown', function(e) {
                    var evtobj = window.event? window.event : e;

                    //CTRL+Z
                    if (evtobj.keyCode === 90 && evtobj.ctrlKey) {
                        ctrl.service.undo();
                    }
                    //Ctrl+Y
                    else if (evtobj.keyCode === 89 && evtobj.ctrlKey) {
                        ctrl.service.redo();
                    }
                });
            }
        };
    }

    angular.module('data-prep.history-control')
        .directive('historyControl', HistoryControl);
})();