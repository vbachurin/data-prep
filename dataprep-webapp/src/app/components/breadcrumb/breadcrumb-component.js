/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/
/**
 * @ngdoc component
 * @name data-prep.breadcrumb.component:BreadcrumbComponent
 * @description This component display a breadcrumb
 * @restrict E
 *
 * @usage
 * <breadcrumb items="$ctrl.state.inventory.breadcrumb"
 *             children="$ctrl.state.inventory.breadcrumbChildren"
 *             on-select="$ctrl.go(item)"
 *             on-list-open="$ctrl.fetchChildren(item)">
 * </breadcrumb>
 *
 * @param {array}    items nodes of the breadcrumb
 * @param {array}      children items of the dropdown of a node of the breadcrumbChildren
 * @param {function}    onListOpen function to call when opening a dropdown
 * @param {function}    onSelect function to call when selecting an item of the breadcrumb
 */

const BreadcrumbComponent = {
    templateUrl: 'app/components/breadcrumb/breadcrumb.html',
    bindings: {
        items: '<',
        children: '<',
        onListOpen: '&',
        onSelect: '&',
    }
};

export default BreadcrumbComponent;
