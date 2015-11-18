(function() {
    'use strict';

    function escape(value) {
        return value.replace(/[[\]{}()*+?.\\^$|/]/g, '[$&]');
    }

    function unescape(value) {
        return value
            .replace('[[]', '[')
            .replace('[]]', ']')
            .replace('[{]', '{')
            .replace('[}]', '}')
            .replace('[(]', '(')
            .replace('[)]', ')')
            .replace('[*]', '*')
            .replace('[+]', '+')
            .replace('[?]', '?')
            .replace('[.]', '.')
            .replace('[\\]', '\\')
            .replace('[^]', '^')
            .replace('[$]', '$')
            .replace('[|]', '|')
            .replace('[/]', '/');
    }

    function removeEscapedSpecialChar(value) {
        return value.replace(/[[][[\]{}()*+?.\\^$|/][\]]/g, '');
    }

    function hasSpecialChar(value) {
        return removeEscapedSpecialChar(value).match(/.*[[\]{}()*+?.\\^$|/].*/);
    }

    var equals = {
        key: '=',
        label: 'Equals',
        adapt: function adapt(value) {
            return escape(value);
        },
        match: function match(value) {
            return !hasSpecialChar(value);
        },
        clean: function clean(value) {
            return unescape(value);
        }
    };

    var contains = {
        key: '≅',
        label: 'Contains',
        extract: function extract(value) {
            return value.substring(2, value.length - 2);
        },
        adapt: function adapt(value) {
            return '.*' + escape(value) + '.*';
        },
        match: function match(value) {
            return value.match(/^[.][*].*[.][*]$/) && !hasSpecialChar(this.extract(value));
        },
        clean: function clean(value) {
            return unescape(this.extract(value));
        }
    };

    var startsWith = {
        key: '>',
        label: 'Starts With',
        extract: function extract(value) {
            return value.substring(1, value.length - 2);
        },
        adapt: function adapt(value) {
            return '^' + escape(value) + '.*';
        },
        match: function match(value) {
            return value.match(/^[^].*[.][*]$/) && !hasSpecialChar(this.extract(value));
        },
        clean: function clean(value) {
            return unescape(this.extract(value));
        }
    };

    var endsWith = {
        key: '<',
        label: 'Ends With',
        extract: function extract(value) {
            return value.substring(2, value.length - 1);
        },
        adapt: function adapt(value) {
            return '.*' + escape(value) + '$';
        },
        match: function match(value) {
            return value.match(/^[.][*].*[$]$/) && !hasSpecialChar(this.extract(value));
        },
        clean: function clean(value) {
            return unescape(this.extract(value));
        }
    };

    var regex = {
        key: '^\\',
        label: 'RegEx',
        adapt: function adapt(value) {
            return value;
        },
        match: function match() {
            return true;
        },
        clean: function clean(value) {
            return value;
        }
    };

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
        vm.types = [equals, contains, startsWith, endsWith, regex];

        /**
         * @ngdoc property
         * @name selectedType
         * @propertyOf talend.widget.controller:EditableRegexCtrl
         * @description The selected regex type. This is initialized with 'contains' type
         */
        vm.selectedType = vm.value ? getRegexType(vm.value) : equals;

        /**
         * @ngdoc property
         * @name regex
         * @propertyOf talend.widget.controller:EditableRegexCtrl
         * @description The entered text. This is initialized with empty string
         */
        vm.regex = vm.value ? vm.selectedType.clean(vm.value) : '';

        vm.updateModel = updateModel;
        vm.setSelectedType = setSelectedType;

        /**
         * @ngdoc method
         * @name updateModel
         * @methodOf talend.widget.controller:EditableRegexCtrl
         * @description Update the model bound with ngModel, depending on selected type and entered text
         */
        function updateModel() {
            vm.value = vm.regex ? vm.selectedType.adapt(vm.regex) : vm.regex;
        }

        /**
         * @ngdoc method
         * @name setSelectedType
         * @methodOf talend.widget.controller:EditableRegexCtrl
         * @description Change selected type and trigger model update
         */
        function setSelectedType(type) {
            vm.selectedType = type;
            updateModel();
        }

        /**
         * @ngdoc method
         * @name setSelectedType
         * @methodOf talend.widget.controller:EditableRegexCtrl
         * @description Change selected type and trigger model update
         */
        function getRegexType(value) {
            return _.find(vm.types, function(type) {
                return type.match(value);
            });
        }
    }

    angular.module('talend.widget')
        .controller('TalendEditableRegexCtrl', TalendEditableRegexCtrl);
})();