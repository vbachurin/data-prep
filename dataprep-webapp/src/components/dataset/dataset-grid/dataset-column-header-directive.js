(function() {
    'use strict';

    function DatasetColumnHeader() {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/dataset-grid/dataset-column-header-directive.html',
            scope:{
                column : '='
            },
            link: function(scope) {
                var MIN_PERCENT = 10;

                scope.column.total = scope.column.quality.valid + scope.column.quality.empty + scope.column.quality.invalid;

                // *_percent is the real % of empty/valid/invalid records, while *_percent_width is the width % of the bar.
                // They can be differents if less than MIN_PERCENT are valid/invalid/empty, to assure a min width of each bar. To be usable by the user.
                // TODO remove completely one bar if absolute zero records match (ie: if 0 invalid records, do not display invalid bar)
                scope.column.quality.emptyPercent = Math.ceil(scope.column.quality.empty * 100 / scope.column.total);
                scope.column.quality.emptyPercentWidth = Math.max(scope.column.quality.emptyPercent, MIN_PERCENT);

                scope.column.quality.invalidPercent = Math.ceil(scope.column.quality.invalid * 100 / scope.column.total);
                scope.column.quality.invalidPercentWidth = Math.max(scope.column.quality.invalidPercent, MIN_PERCENT);

                scope.column.quality.validPercent = 100 - scope.column.quality.emptyPercent - scope.column.quality.invalidPercent;
                scope.column.quality.validPercentWidth = 100 - scope.column.quality.emptyPercentWidth - scope.column.quality.invalidPercentWidth;
            }
        };
    }

    angular.module('data-prep-dataset')
        .directive('datasetColumnHeader', DatasetColumnHeader);
})();
