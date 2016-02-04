/**
 * @ngdoc controller
 * @name data-prep.transformation-form.controller:TransformParamsCtrl
 * @description Transformation parameters controller.
 */
export default function TransformParamsCtrl() {
    var vm = this;

    /**
     * @ngdoc method
     * @name getParameterType
     * @methodOf data-prep.transformation-form.controller:TransformParamsCtrl
     * @description Return the parameter type to display
     * @param {object} parameter The parameter
     */
    vm.getParameterType = function (parameter) {
        var type = parameter.type.toLowerCase();
        switch (type) {
            case 'select':
            case 'cluster':
            case 'date':
            case 'column':
            case 'regex':
                return type;
            default:
                return 'simple';
        }
    };

}