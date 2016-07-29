/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

import angular from 'angular';
import DATASET_FILTERS from './datasets-filters/datasets-filters-module';
import DATASET_UPDLOAD_LIST_MODULE from '../../dataset/upload-list/dataset-upload-list-module';
import IMPORT_MODULE from '../../import/import-module';
import INVENTORY_ITEM_MODULE from '../../inventory/item/inventory-item-module';
import SERVICES_DATASET_MODULE from '../../../services/dataset/dataset-module';
import SERVICES_PREPARATION_MODULE from '../../../services/preparation/preparation-module';
import SERVICES_UTILS_MODULE from '../../../services/utils/utils-module';

import PreparationCreatorComponent from './preparation-creator-component';

const MODULE_NAME = 'data-prep.preparation-creator';

/**
 * @ngdoc object
 * @name data-prep.preparation-creator
 * @description This module creates directly a preparation from a dataset
 * @requires data-prep.inventory-item
 * @requires data-prep.datasets-filters
 */
angular.module(MODULE_NAME,
    [
        DATASET_FILTERS,
        DATASET_UPDLOAD_LIST_MODULE,
        IMPORT_MODULE,
        INVENTORY_ITEM_MODULE,
        SERVICES_DATASET_MODULE,
        SERVICES_PREPARATION_MODULE,
        SERVICES_UTILS_MODULE,
    ])
    .component('preparationCreator', PreparationCreatorComponent);

export default MODULE_NAME;
