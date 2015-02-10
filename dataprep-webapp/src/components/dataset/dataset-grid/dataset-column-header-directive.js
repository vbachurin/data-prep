(function() {
    'use strict';

    function DatasetColumnHeader() {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/dataset-grid/dataset-column-header-directive.html',
            scope:{
                column : '='
            },
            bindToController: true,
            controllerAs: 'datasetHeaderCtrl',
            controller: function() {
                var MIN_PERCENT = 10;

                this.column.total = this.column.quality.valid + this.column.quality.empty + this.column.quality.invalid;

                // *_percent is the real % of empty/valid/invalid records, while *_percent_width is the width % of the bar.
                // They can be differents if less than MIN_PERCENT are valid/invalid/empty, to assure a min width of each bar. To be usable by the user.
                // TODO remove completely one bar if absolute zero records match (ie: if 0 invalid records, do not display invalid bar)
                this.column.quality.emptyPercent = Math.ceil(this.column.quality.empty * 100 / this.column.total);
                this.column.quality.emptyPercentWidth = Math.max(this.column.quality.emptyPercent, MIN_PERCENT);

                this.column.quality.invalidPercent = Math.ceil(this.column.quality.invalid * 100 / this.column.total);
                this.column.quality.invalidPercentWidth = Math.max(this.column.quality.invalidPercent, MIN_PERCENT);

                this.column.quality.validPercent = 100 - this.column.quality.emptyPercent - this.column.quality.invalidPercent;
                this.column.quality.validPercentWidth = 100 - this.column.quality.emptyPercentWidth - this.column.quality.invalidPercentWidth;
            }
        };
    }

    angular.module('data-prep-dataset')
        .directive('datasetColumnHeader', DatasetColumnHeader);
})();
