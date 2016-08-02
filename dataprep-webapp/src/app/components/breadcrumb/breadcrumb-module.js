/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

import angular from 'angular';
import sunchoke from 'sunchoke';
import BreadcrumbComponent from './breadcrumb-component';

const MODULE_NAME = 'data-prep.breadcrumb';

angular.module(MODULE_NAME, [sunchoke.dropdown])
    .component('breadcrumb', BreadcrumbComponent);

export default MODULE_NAME;
