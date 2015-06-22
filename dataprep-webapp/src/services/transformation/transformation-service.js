(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.transformation.service:TransformationService
     * @description Transformation service. This service provide the entry point to get and manipulate transformations
     * @requires data-prep.services.transformation.service:TransformationRestService
     * @requires data-prep.services.utils.service:ConverterService
     */
    function TransformationService(TransformationRestService, ConverterService) {
        var choiceType = 'CHOICE';
        var clusterType = 'CLUSTER';

        /**
         * @ngdoc method
         * @name isColumnInfo
         * @methodOf data-prep.services.transformation.service:TransformationService
         * @param {object} param - the paramters to check
         * @description [PRIVATE] Return true if the parameter is 'column_id' or 'column_name'
         */
        var isColumnInfo = function isColumnInfo(param) {
            return param.name === 'column_id' || param.name === 'column_name';
        };

        //--------------------------------------------------------------------------------------------------------------
        //------------------------------------------Transformation suggestions------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name cleanParamsAndItems
         * @methodOf data-prep.services.transformation.service:TransformationService
         * @param {object[]} menus - the menus to clean
         * @description [PRIVATE] Remove 'column_id' and 'column_name' parameters (automatically sent), and clean empty arrays (choices and params)
         */
        var cleanParamsAndItems = function cleanParamsAndItems(menus) {
            return _.forEach(menus, function(menu) {
                //params
                var filteredParameters = _.filter(menu.parameters, function(param) {
                    return !isColumnInfo(param);
                });
                menu.parameters = filteredParameters.length ? filteredParameters : null;

                //items
                menu.items = menu.items.length ? menu.items : null;
            });
        };

        /**
         * @ngdoc method
         * @name insertType
         * @methodOf data-prep.services.transformation.service:TransformationService
         * @param {object[]} menu - the menu item with parameters to adapt
         * @description [PRIVATE] Insert adapted html input type in each parameter in the menu
         */
        var insertType = function insertType(menu) {
            if(menu.parameters) {
                _.forEach(menu.parameters, function(param) {
                    param.inputType = ConverterService.toInputType(param.type);
                });
            }
        };

        /**
         * @ngdoc method
         * @name adaptInputTypes
         * @methodOf data-prep.services.transformation.service:TransformationService
         * @param {object[]} menus - the menus with parameters to adapt
         * @description [PRIVATE] Adapt each parameter type to HTML input type
         */
        var adaptInputTypes = function adaptInputTypes(menus) {
            _.forEach(menus, function(menu) {
                insertType(menu);

                _.forEach(menu.items, function(item) {
                    _.forEach(item.values, function(choiceValue) {
                        insertType(choiceValue);
                    });
                });
            });

            return menus;
        };

        /**
         * @ngdoc method
         * @name getTransformations
         * @methodOf data-prep.services.transformation.service:TransformationService
         * @param {object} stringifiedColumn The transformations target column as string
         * @description Get transformations from REST call, clean and adapt them
         */
        this.getTransformations = function getTransformations(stringifiedColumn) {
            return TransformationRestService.getTransformations(stringifiedColumn)
                .then(function(response) {
                    var menus = cleanParamsAndItems(response.data);
                    return adaptInputTypes(menus);
                });
        };

        //--------------------------------------------------------------------------------------------------------------
        //------------------------------------------Transformation parameters-------------------------------------------
        //--------------------------------------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name resetParamValue
         * @methodOf data-prep.services.transformation.service:TransformationService
         * @param {object} params The params to reset
         * @param {string} type The param type
         * @description [PRIVATE] Reset params values with saved initial values
         */
        this.resetParamValue = function resetParamValue(params, type) {
            if(!params) {
                return;
            }

            function executeOnSimpleParams(simpleParamsToInit) {
                _.forEach(simpleParamsToInit, function (param) {
                    param.value = param.initialValue;
                });
            }

            switch(type) {
                case choiceType:
                    _.forEach(params, function (choice) {
                        choice.selectedValue = choice.initialValue;

                        _.forEach(choice.values, function (choiceItem) {
                            executeOnSimpleParams(choiceItem.parameters);
                        });
                    });
                    break;

                case clusterType:
                    _.forEach(params.clusters, function(cluster) {
                        cluster.active = cluster.initialActive;
                        executeOnSimpleParams(cluster.parameters);
                        executeOnSimpleParams([cluster.replace]);
                    });
                    break;

                default:
                    executeOnSimpleParams(params);
            }
        };

        /**
         * @ngdoc method
         * @name initParameters
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} parameters The parameters
         * @param {object} paramValues The parameters initial values
         * @description [PRIVATE] Init parameters initial value and type
         * @returns {object[]} The parameters with initialized values
         */
        var initParameters = function initParameters(parameters, paramValues) {
            return _.chain(parameters)
                .filter(function(param) {
                    return !isColumnInfo(param);
                })
                .forEach(function(param) {
                    param.initialValue = param.value = paramValues[param.name];
                    param.inputType = ConverterService.toInputType(param.type);
                })
                .value();
        };

        /**
         * @ngdoc method
         * @name initChoices
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} choices The choices
         * @param {object} paramValues The parameters and choice initial values
         * @description [PRIVATE] Init choice initial value, including each choice params initial value and type
         * @returns {object} The choices with initialized values
         */
        var initChoices = function initChoices(choices, paramValues) {
            _.forEach(choices, function(choice) {
                choice.selectedValue = choice.initialValue = _.find(choice.values, function(choiceItem) {
                    return choiceItem.name === paramValues[choice.name];
                });

                _.forEach(choice.values, function(choiceItem) {
                    initParameters(choiceItem.parameters, paramValues);
                });
            });

            return choices;
        };

        /**
         * @ngdoc method
         * @name initCluster
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} cluster The Cluster parameters
         * @param {object} paramValues The clusters initial values
         * @description [PRIVATE] Init Clusters initial value
         * @returns {object} The Cluster with initialized values
         */
        var initCluster = function initCluster(cluster, paramValues) {
            _.forEach(cluster.clusters, function(clusterItem) {
                var firstActiveParam = _.chain(clusterItem.parameters)
                    .forEach(function(param) {
                        param.initialValue = param.value = param.name in paramValues;
                    })
                    .filter('value')
                    .first()
                    .value();
                clusterItem.initialActive = !!firstActiveParam;

                //get the replace value or the default if the cluster item is inactive
                //and init the replace input value
                var replaceValue = firstActiveParam ? paramValues[firstActiveParam.name] : clusterItem.replace.default;
                var replaceParamValues = {replaceValue: replaceValue};
                initParameters([clusterItem.replace], replaceParamValues);
            });
            return cluster;
        };

        /**
         * @ngdoc method
         * @name initParamsValues
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} transformation The transformation infos
         * @param {object} paramValues The transformation parameters initial values
         * @description Init parameters values and save them as initial values
         */
        this.initParamsValues = function initParamsValues(transformation, paramValues) {
            if(transformation.parameters) {
                transformation.parameters = initParameters(transformation.parameters, paramValues);
            }
            if(transformation.items) {
                transformation.items = initChoices(transformation.items, paramValues);
            }
            if(transformation.cluster) {
                transformation.cluster = initCluster(transformation.cluster, paramValues);
            }
        };

        //--------------------------------------------------------------------------------------------------------------
        //-------------------------------------Transformation Dynamic parameters----------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name resetParameters
         * @methodOf data-prep.services.transformation.service:TransformationService
         * @description [PRIVATE] Reset all the transformation parameters
         */
        var resetParameters = function resetParameters(transformation) {
            transformation.parameters = null;
            transformation.items = null;
            transformation.cluster = null;
        };

        /**
         * @ngdoc method
         * @name initDynamicParameters
         * @methodOf data-prep.services.transformation.service:TransformationService
         * @description Fetch the dynamic parameter and set them in transformation
         */
        this.initDynamicParameters = function initDynamicParameters(transformation, infos) {
            resetParameters(transformation);

            var action = transformation.name;
            return TransformationRestService.getDynamicParameters(action, infos.columnId, infos.datasetId, infos.preparationId, infos.stepId)
                .then(function(response) {
                    var parameters = response.data;
                    transformation[parameters.type] = parameters.details;
                    return transformation;
                });
        };

    }

    angular.module('data-prep.services.transformation')
        .service('TransformationService', TransformationService);
})();
