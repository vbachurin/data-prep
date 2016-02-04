/**
 * @ngdoc directive
 * @name talend.widget.directive:EditableSelect
 * @description This directive create an editable combobox
 * @restrict E
 * @usage
 <editable-select
     list="selectValues"
     ng-model="value"></editable-select>
 * @param {Array} list The list of selectable values in the combobox
 * @param {object} ngModel The model to bind
 */
export default function EditableSelect() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/widgets/editable-select/editable-select.html',
        scope: {
            list: '=',
            value: '=ngModel'
        },
        bindToController: true,
        controller: () => {},
        controllerAs: 'editableSelectCtrl'
    };
}