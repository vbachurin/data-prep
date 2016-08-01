/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

import _ from 'lodash';

/**
 * @ngdoc service
 * @name data-prep.services.transformation.service:TransformationService
 * @description Transformation service.
 * This service provide the entry point to get and manipulate transformations
 * @requires data-prep.services.parameters.service:ParametersService
 * @requires data-prep.services.utils.service:ConverterService
 * @requires data-prep.services.transformation.service:TransformationRestService
 */
export default function TransformationService(TransformationRestService, ParametersService, ConverterService) {
    'ngInject';

    const COLUMN_CATEGORY = 'column_metadata';

    return {
        getLineTransformations,
        getColumnTransformations,
        getColumnSuggestions,
        initDynamicParameters,
    };

    // --------------------------------------------------------------------------------------------
    // ----------------------------------Transformations suggestions Utils-------------------------
    // --------------------------------------------------------------------------------------------
    /**
     * @ngdoc method
     * @name cleanParams
     * @methodOf data-prep.services.transformation.service:TransformationService
     * @param {object[]} menus - the menus to clean
     * @description Remove 'column_id' and 'column_name' parameters (automatically sent),
     * and clean empty arrays (choices and params)
     */
    function cleanParams(menus) {
        return _.forEach(menus, (menu) => {
            const filteredParameters = _.filter(menu.parameters, (param) => !param.implicit);
            menu.parameters = filteredParameters.length ? filteredParameters : null;
        });
    }

    /**
     * @ngdoc method
     * @name insertType
     * @methodOf data-prep.services.transformation.service:TransformationService
     * @param {object[]} transformation The transformation with parameters to adapt
     * @description Insert adapted html input type in each parameter in the menu
     */
    function insertType(transformation) {
        if (transformation.parameters) {
            _.forEach(transformation.parameters, (param) => {
                param.inputType = ConverterService.toInputType(param.type);

                // also take care of select parameters...
                if (param.type === 'select' && param.configuration && param.configuration.values) {
                    _.forEach(param.configuration.values, (selectItem) => {
                        selectItem.inputType = ConverterService.toInputType(selectItem.type);
                        // ...and its parameters
                        if (selectItem.parameters) {
                            insertType(selectItem);
                        }
                    });
                }
            });
        }
    }

    /**
     * @ngdoc method
     * @name insertInputTypes
     * @methodOf data-prep.services.transformation.service:TransformationService
     * @param {Array} transformations The transformations with parameters to adapt
     * @description Insert parameter type to HTML input type in each transformations
     */
    function insertInputTypes(transformations) {
        _.forEach(transformations, insertType);
    }

    /**
     * @ngdoc method
     * @name setHtmlDisplayLabels
     * @methodOf data-prep.services.transformation.service:TransformationService
     * @description Inject the UI label in each transformations
     * @param {Array} transformations The list of transformations
     */
    function setHtmlDisplayLabels(transformations) {
        _.forEach(transformations, (transfo) => {
            transfo.labelHtml =
                transfo.label + (transfo.parameters || transfo.dynamic ? '...' : '');
        });
    }

    function isNotColumnCategory(category) {
        return (item) => {
            return item.category !== category;
        };
    }

    function labelCriteria(transfo) {
        return transfo.label.toLowerCase();
    }

    /**
     * @ngdoc method
     * @name prepareTransformations
     * @methodOf data-prep.services.transformation.service:TransformationService
     * @description Sort and group transformations by category
     * @return {Object} An object {category, categoryHtml, transformations} .
     * "category" the category
     * "categoryHtml" the adapted category for UI
     * "transformations" the array of transformations for this category
     */
    function prepareTransformations(transformations) {
        const groupedTransformations = _.chain(transformations)
            .filter(isNotColumnCategory(COLUMN_CATEGORY))
            .sortBy(labelCriteria)
            .groupBy('category')
            .value();

        return _.chain(Object.getOwnPropertyNames(groupedTransformations))
            .sort()
            .map((key) => {
                return {
                    category: key,
                    categoryHtml: key.toUpperCase(),
                    transformations: groupedTransformations[key],
                };
            })
            .value();
    }

    // --------------------------------------------------------------------------------------------
    // -----------------------------------Transformations suggestions------------------------------
    // --------------------------------------------------------------------------------------------
    /**
     * @ngdoc method
     * @name getLineTransformations
     * @methodOf data-prep.services.transformation.service:TransformationService
     * @description Get transformations from REST call, clean and adapt them
     * @return {Object} An object {allTransformations, allCategories} .
     * "allTransformations" is the array of all transformations (cleaned and adapted for UI)
     * "allCategories" is the array of all transformations grouped by category
     */
    function getLineTransformations() {
        return TransformationRestService.getLineTransformations()
            .then((response) => {
                const allTransformations = cleanParams(response.data);
                insertInputTypes(allTransformations);
                setHtmlDisplayLabels(allTransformations);
                const allCategories = prepareTransformations(allTransformations);
                return {
                    allTransformations,
                    allCategories,
                };
            });
    }

    /**
     * @ngdoc method
     * @name getColumnTransformations
     * @methodOf data-prep.services.transformation.service:TransformationService
     * @param {object} column The transformations target column
     * @description Get transformations from REST call, clean and adapt them
     * @return {Object} An object {allTransformations, allCategories} .
     * "allTransformations" is the array of all transformations (cleaned and adapted for UI)
     * "allCategories" is the array of all transformations grouped by category
     */
    function getColumnTransformations(column) {
        return TransformationRestService.getColumnTransformations(column)
            .then((response) => {
                const allTransformations = cleanParams(response.data);
                insertInputTypes(allTransformations);
                setHtmlDisplayLabels(allTransformations);
                const allCategories = prepareTransformations(allTransformations);
                return {
                    allTransformations,
                    allCategories,
                };
            });
    }

    /**
     * @ngdoc method
     * @name getColumnSuggestions
     * @methodOf data-prep.services.transformation.service:TransformationService
     * @param {object} column The transformations target column
     * @description Get suggestions from REST call, clean and adapt them
     * @returns {Array} All the suggestions, cleaned and adapted for UI
     */
    function getColumnSuggestions(column) {
        return TransformationRestService.getColumnSuggestions(column)
            .then((response) => {
                const allTransformations = cleanParams(response.data);
                insertInputTypes(allTransformations);
                setHtmlDisplayLabels(allTransformations);
                return allTransformations;
            });
    }

    // --------------------------------------------------------------------------------------------
    // ---------------------------Transformation Dynamic parameters--------------------------------
    // --------------------------------------------------------------------------------------------
    /**
     * @ngdoc method
     * @name initDynamicParameters
     * @methodOf data-prep.services.transformation.service:TransformationService
     * @description Fetch the dynamic parameter and set them in transformation
     */
    function initDynamicParameters(transformation, infos) {
        ParametersService.resetParameters(transformation);

        const action = transformation.name;
        return TransformationRestService
            .getDynamicParameters(
                action,
                infos.columnId,
                infos.datasetId,
                infos.preparationId,
                infos.stepId
            )
            .then((response) => {
                const parameters = response.data;
                transformation[parameters.type] = parameters.details;
                return transformation;
            });
    }
}
