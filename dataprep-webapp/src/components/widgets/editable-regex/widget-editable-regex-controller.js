(function() {
    'use strict';

    function escape(value) {
        return value.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '[$&]');
    }

    var contains = {key: '≅', label: 'Contains', adapt: function adapt(value) {
        return '.*' + escape(value) + '.*';
    }};

    var startsWith = {key: '>', label: 'Starts With', adapt: function adapt(value) {
        return '^' + escape(value) + '.*';
    }};

    var endsWith = {key: '<', label: 'Ends With', adapt: function adapt(value) {
        return '.*' + escape(value) + '$';
    }};

    var regex = {key: '^\\', label: 'RegEx', adapt: function adapt(value) {
        return value;
    }};

    /**
     * @ngdoc controller
     * @name talend.widget.controller:EditableRegexCtrl
     * @description Editable regex controller. It manage the entered value adaptation to match the wanted regex type
     */
    function TalendEditableRegexCtrl() {
        var vm = this;

        /**
         * @ngdoc property
         * @name types
         * @propertyOf talend.widget.controller:EditableRegexCtrl
         * @description The array of regex type
         */
        vm.types = [contains, startsWith, endsWith, regex];

        /**
         * @ngdoc property
         * @name selectedType
         * @propertyOf talend.widget.controller:EditableRegexCtrl
         * @description The selected regex type. This is initialized with 'contains' type
         */
        vm.selectedType = contains;

        /**
         * @ngdoc property
         * @name regex
         * @propertyOf talend.widget.controller:EditableRegexCtrl
         * @description The entered text. This is initialized with empty string
         */
        vm.regex = '';

        /**
         * @ngdoc method
         * @name updateModel
         * @methodOf talend.widget.controller:EditableRegexCtrl
         * @description Update the model bound with ngModel, depending on selected type and entered text
         */
        vm.updateModel = function updateModel() {
            vm.value = vm.regex ? vm.selectedType.adapt(vm.regex) : vm.regex;
        };

        /**
         * @ngdoc method
         * @name setSelectedType
         * @methodOf talend.widget.controller:EditableRegexCtrl
         * @description Change selected type and trigger model update
         */
        vm.setSelectedType = function setSelectedType(type) {
            vm.selectedType = type;
            vm.updateModel();
        };
    }

    angular.module('talend.widget')
        .controller('TalendEditableRegexCtrl', TalendEditableRegexCtrl);
})();