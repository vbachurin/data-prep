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
 * @name data-prep.folder-selection.component:FolderTreeComponent
 * @description This component display a folder tree
 * @restrict E
 *
 * @usage
 * <folder-tree ng-switch-when="false"
 *              node="$ctrl.tree"
 *              level="0"
 *              on-toggle="$ctrl.toggle(node)"
 *              on-select="$ctrl.chooseFolder(node.folder)">
 * </folder-tree>
 *
 * @param {object}  node the root of the tree
 * @param {integer} level the level of the node
 * @param {function}    onToggle function to call when opening/closing a node
 * @param {function}    onSelect function to call when selecting a node
 */

const FolderTreeComponent = {
    bindings: {
        node: '<',
        level: '<',
        onToggle: '&',
        onSelect: '&',
    },
    templateUrl: 'app/components/folder-selection/folder-tree/folder-tree.html'
};

export default FolderTreeComponent;