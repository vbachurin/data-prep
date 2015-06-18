(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid.controller:DatagridCtrl
     * @description Dataset grid controller.
     */
    function DatagridCtrl($timeout) {
        var vm = this;
        var tooltipPromise, tooltipHidePromise;
        var tooltipDelay = 300;

        /**
         * @ngdoc method
         * @name cancelTooltip
         * @methodOf data-prep.datagrid.controller:DatagridCtrl
         * @description [PRIVATE] Cancel the current tooltip promise
         */
        var cancelTooltip = function() {
            if(tooltipPromise) {
                $timeout.cancel(tooltipPromise);
            }
        };

        /**
         * @ngdoc method
         * @name createTooltip
         * @methodOf data-prep.datagrid.controller:DatagridCtrl
         * @param {object} record - the current record (only used if tooltip is used as an value editor, may be removed)
         * @param {string} colId - the column id (only used if tooltip is used as an value editor, may be removed)
         * @param {object} position - the position where to display it {x: number, y: number}
         * @param {String} htmlStr- the html string to be displayed in the tooltip box
         * @description [PRIVATE] Update the tooltip component and display with a delay
         */
        var createTooltip = function(record, colId, position, htmlStr) {
            tooltipPromise = $timeout(function() {
                vm.record = record;
                vm.position = position;
                vm.colId = colId;
                vm.showTooltip = true;
                vm.htmlStr = htmlStr;
            }, tooltipDelay);
        };

        /**
         * @ngdoc method
         * @name updateTooltip
         * @methodOf data-prep.datagrid.controller:DatagridCtrl
         * @param {object} record - the current record (only used if tooltip is used as an value editor, may be removed)
         * @param {string} colId - the column id (only used if tooltip is used as an value editor, may be removed)
         * @param {object} position - the position where to display it {x: number, y: number}
         * @param {String} htmlStr- the HTML string to be displayed in the tooltip.
         * @description Cancel the old tooltip promise if necessary and create a new one
         */
        vm.updateTooltip = function(record, colId, position, htmlStr) {
            cancelTooltip();
            createTooltip(record, colId, position, htmlStr);
        };

        /**
         * @ngdoc method
         * @name hideTooltip
         * @methodOf data-prep.datagrid.controller:DatagridCtrl
         * @description Cancel the old tooltip promise if necessary and hide the tooltip
         */
        vm.hideTooltip = function() {
            cancelTooltip();
            if(vm.showTooltip) {
                tooltipHidePromise = $timeout(function() {
                    vm.showTooltip = false;
                });
            }
        };

        /**
         * @ngdoc method
         * @name computeHTMLForLeadingOrTrailingHiddenChars
         * @methodOf data-prep.datagrid.directive:Datagrid
         * @description [PRIVATE] split the string value into leadin chars, text and trailing char and create html element using the class hiddenChars
         * to specify the hiddenChars.
         */
         vm.computeHTMLForLeadingOrTrailingHiddenChars = function(value){
            var returnStr = value;
            var hiddenCharsRegExpMatch = value.match(/(^\s*)?([\s\S]*?)(\s*$)/);
            if (hiddenCharsRegExpMatch[1]){//leading hidden chars found
                returnStr = '<span class="hiddenChars">' + hiddenCharsRegExpMatch[1] + '</span>' + hiddenCharsRegExpMatch[2];
            }else{//no leading hiddend chars
                returnStr = hiddenCharsRegExpMatch[2] ;
            }
            if (hiddenCharsRegExpMatch[3]){//trailing hidden chars
                returnStr += '<span class="hiddenChars">' + hiddenCharsRegExpMatch[3] + '</span>';
            }//else no trailing char so leave returnStr as is.

            return returnStr;
        };

    }

    angular.module('data-prep.datagrid')
        .controller('DatagridCtrl', DatagridCtrl);
})();