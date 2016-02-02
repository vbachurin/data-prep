export default function FilterMonitor() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/filter/monitor/filter-monitor.html',
        scope: {
            filters: '=',
            onReset: '&',
            nbLines: '=',
            nbTotalLines: '=',
            percentage: '='
        },
        bindToController: true,
        controller: () => {},
        controllerAs: 'filterMonitorCtrl'
    };
}