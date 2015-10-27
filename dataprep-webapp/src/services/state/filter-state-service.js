(function() {
    'use strict';

    var filterState = {
        gridFilters: [],
        applyTransformationOnFilters: false
    };

    var theAndTree, theOrTree;

    function FilterStateService(state) {
        return {
            //common
            reset: reset,

            //grid
            addGridFilter: addGridFilter,
            updateGridFilter: updateGridFilter,
            removeGridFilter: removeGridFilter,
            removeAllGridFilters: removeAllGridFilters,
            convertFiltersToQueryFormat: convertFiltersToQueryFormat
        };

        //--------------------------------------------------------------------------------------------------------------
        //-----------------------------------------------------GRID-----------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        function addGridFilter(filterInfo) {
            var _aux = filterState.gridFilters;
            filterState.gridFilters = _aux.slice(0);
            filterState.gridFilters.push(filterInfo);

            //new filter added
            //var sizeChanged = filterState.gridFilters.length && _aux.length
            if(filterState.gridFilters.length && checkRelevantChanges(filterState.gridFilters, _aux)){
                filterState.applyTransformationOnFilters = true;
            }

        }

        function updateGridFilter(oldFilter, newFilter) {
            var index = filterState.gridFilters.indexOf(oldFilter);
            filterState.gridFilters = filterState.gridFilters.slice(0);
            filterState.gridFilters[index] = newFilter;
        }

        function removeGridFilter(filterInfo) {
            var index = filterState.gridFilters.indexOf(filterInfo);
            var _aux = filterState.gridFilters;
            filterState.gridFilters = _aux.slice(0);
            filterState.gridFilters.splice(index, 1);

            if(filterState.gridFilters.length === 0  && checkRelevantChanges(filterState.gridFilters, _aux)){
                //in order to keep All the lines radio selected
                filterState.applyTransformationOnFilters = false;
            }
        }

        function removeAllGridFilters() {
            filterState.gridFilters = [];
            filterState.applyTransformationOnFilters = false;
        }

        function checkRelevantChanges(newArr, ancientArr){
            return !(newArr.length && ancientArr.length);//the real condition is : !!(newArr.length) !== !!(ancientArr.length);
        }

        //--------------------------------------------------------------------------------------------------------------
        //-----------------------------------------------------COMMON-----------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        function reset() {
            removeAllGridFilters();
        }

        //--------------------------------------------------------------------------------------------------------------
        //-----------------------------------------------------CONVERT FILTERS TO QUERY FORMAT--------------------------
        //--------------------------------------------------------------------------------------------------------------

        function convertFiltersToQueryFormat(filtersArray){
            var formattedFilters = [];
            _.each(filtersArray, function(filter){
                switch (filter.type) {
                    case 'contains':
                        formattedFilters.push({
                            'contains': {
                                'field': filter.colId,
                                'value': filter.value
                            }
                        });
                        break;
                    case 'exact':
                        formattedFilters.push({
                            'eq': {
                                'field': filter.colId,
                                'value': filter.value
                            }
                        });
                        break;
                    case 'invalid_records':
                        createInvalidValuesTree(filter.colId, 'eq');
                        formattedFilters.push(theOrTree);
                        break;
                    case 'empty_records':
                        formattedFilters.push({
                            'eq': {
                                'field': filter.colId,
                                'value': ''
                            }
                        });
                        break;
                    case 'valid_records':
                        createInvalidValuesTree(filter.colId, 'not');
                        formattedFilters.push(theOrTree);
                        break;
                    case 'inside_range':
                        formattedFilters.push({
                                'range': {
                                    field: filter.colId,
                                    start: filter.args.interval[0],
                                    end: filter.args.interval[1]
                                }
                            }
                        );
                        break;
                }
            });
            if(formattedFilters.length === 1){
                return {filter: formattedFilters[0]};
            }
            else if(formattedFilters.length === 0){
                return {};
            }
            else {
                constructPairsTree(formattedFilters, 2, 'and');
                return {filter: theAndTree};
            }
        }

        function constructPairsTree(arr, n, operator){
            var res = [];
            var jsnObj;
            while (arr.length) {
                if(arr.length === 1){
                    var lastRemainingFilter = arr.pop();
                    var lastAndConstructedFilter = res.pop();
                    jsnObj = {};
                    jsnObj[operator] = [];
                    jsnObj[operator].push(lastAndConstructedFilter);
                    jsnObj[operator].push(lastRemainingFilter);
                    res.push(jsnObj);
                }
                else{
                    var two = arr.splice(0, n);
                    jsnObj = {};
                    jsnObj[operator] = two;
                    res.push(jsnObj);
                }
            }
            if(res.length>1){
                constructPairsTree(res, 2, operator);
            }
            else if(res.length === 1){
                if(operator === 'and'){
                    theAndTree = res[0];
                }
                else{
                    theOrTree = res[0];
                }
            }
        }
        
        function createInvalidValuesTree(colId, sign){
            var column = _.find(state.playground.data.columns, {id: colId});
            var invalidValues = column.quality.invalidValues;
            var formattedInvalidValues = [];
            _.each(invalidValues, function(invalidValue){
                var predicat = {};
                predicat[sign] = {
                    'field': colId,
                    'value': invalidValue
                };
                formattedInvalidValues.push(predicat);
            });
            constructPairsTree(formattedInvalidValues, 2, 'or');
        }
    }

    angular.module('data-prep.services.state')
        .service('FilterStateService', FilterStateService)
        .constant('filterState', filterState);
})();