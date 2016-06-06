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
 * @name data-prep.folder-selection.component:FolderTreeNodeComponent
 * @description This component display a folder tree node
 * @restrict E
 *
 * @usage
 *<folder-tree-node ng-switch-when="true"
 *                  ng-repeat="node in $ctrl.searchItems track by node.folder.id"
 *                  name="node.folder.path"
 *                  level="0"
 *                  is-opened="false"
 *                  has-children="false"
 *                  is-selected="node.folder.selected"
 *                  on-toggle="$ctrl.toggle(node)"
 *                  on-select="$ctrl.chooseFolder(node.folder)">
 *</folder-tree-node>
 *
 * @param {string}  name the name of the tree node
 * @param {integer} level the level of the node
 * @param {boolean}  isOpened check whether a node is opened or not
 * @param {boolean} hasChildren check whether a node has children or not
 * @param {boolean}  isSelected whether a node is selected or not
 * @param {function}    onToggle function to call when opening/closing a node
 * @param {function}    onSelect function to call when selecting a node
 */

const FolderTreeNodeComponent = {
    bindings: {
        name: '<',
        level: '<',
        isOpened: '<',
        hasChildren: '<',
        isSelected: '<',
        onToggle: '&',
        onSelect: '&',
    },
    templateUrl: 'app/components/folder-selection/folder-tree-node/folder-tree-node.html'
};

export default FolderTreeNodeComponent;