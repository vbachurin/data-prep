(function () {
    'use strict';

    var menusMock = [
        {
            'name': 'uppercase',
            'category': 'case'
        },
        {
            'name': 'lowercase',
            'category': 'case'
        },
        {
            'name': 'withParam',
            'category': 'case',
            'parameters': [
                {
                    'name': 'param',
                    'type': 'text',
                    'default': '.'
                }
            ]
        },
        {
            'name': 'split',
            'category': 'split',
            'choice': {
                name: 'mode',
                values: [
                    {
                        name: 'noparam'
                    },
                    {
                        name: 'regex',
                        'parameters': [
                            {
                                'name': 'regexp',
                                'type': 'text',
                                'default': '.'
                            }
                        ]
                    },
                    {
                        name: 'index',
                        'parameters': [
                            {
                                'name': 'index',
                                'type': 'number',
                                'default': '5'
                            }
                        ]
                    },
                    {
                        name: 'twoParams',
                        'parameters': [
                            {
                                'name': 'index',
                                'type': 'number',
                                'default': '5'
                            },
                            {
                                'name': 'index2',
                                'type': 'number',
                                'default': '5'
                            }
                        ]
                    }
                ]
            }
        }
    ];

    /**
     * DatasetColumnHeader directive controller
     * @param $rootScope
     * @param TransformationService
     */
    function DatasetColumnHeaderCtrl($q, $timeout) {
        var vm = this;

        /**
         * Compute quality bars percentage
         */
        vm.refreshQualityBar = function () {
            var MIN_PERCENT = 10;
            var column = vm.column;

            column.total = column.quality.valid + column.quality.empty + column.quality.invalid;

            // *_percent is the real % of empty/valid/invalid records, while *_percent_width is the width % of the bar.
            // They can be differents if less than MIN_PERCENT are valid/invalid/empty, to assure a min width of each bar. To be usable by the user.
            // TODO remove completely one bar if absolute zero records match (ie: if 0 invalid records, do not display invalid bar)
            column.quality.emptyPercent = Math.ceil(column.quality.empty * 100 / column.total);
            column.quality.emptyPercentWidth = Math.max(column.quality.emptyPercent, MIN_PERCENT);

            column.quality.invalidPercent = Math.ceil(column.quality.invalid * 100 / column.total);
            column.quality.invalidPercentWidth = Math.max(column.quality.invalidPercent, MIN_PERCENT);

            column.quality.validPercent = 100 - column.quality.emptyPercent - column.quality.invalidPercent;
            column.quality.validPercentWidth = 100 - column.quality.emptyPercentWidth - column.quality.invalidPercentWidth;
        };

        /**
         * Insert a divider between each group of menus
         * @param menuGroups - the menus grouped by category
         * @returns {Array}
         */
        var insertDividers = function(menuGroups) {
            var divider = {isDivider : true};
            var result = [];
            _.forEach(menuGroups, function(group) {
                if(result.length) {
                    result.push(divider);
                }

                result.push(group);
            });

            return result;
        };

        /**
         * Group all menus by category and insert dividers between each group
         * @param menus - the menu list
         * @returns {Array}
         */
        var groupMenus = function(menus) {
            var groups = _.groupBy(menus, function(menuItem) { return menuItem.category; });
            var groupsAndDividers = insertDividers(groups);

            return _.flatten(groupsAndDividers);
        };

        /**
         * Get transformations from rest call
         */
        vm.initTransformations = function () {
            if (!vm.transformations && !vm.initTransformationsInProgress) {
                vm.initTransformationsInProgress = true;

                $timeout(function () {
                    $q.when({data: menusMock}).then(function (response) {
                        vm.transformations = groupMenus(response.data);
                    });
                }, 500);
            }
        };
    }

    angular.module('data-prep-dataset')
        .controller('DatasetColumnHeaderCtrl', DatasetColumnHeaderCtrl);
})();
