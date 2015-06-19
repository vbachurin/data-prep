(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name talend.widget.controller:TalendAccordionsCtrl
     * @description Accordions directive controller
     */
    function TalendAccordionsCtrl() {
        var vm = this;

        /**
         * @ngdoc property
         * @name accordions
         * @propertyOf talend.widget.controller:TalendAccordionsCtrl
         * @description The array containing all its accordions items
         * @type {Array}
         */
        vm.accordions = [];

        /**
         * @ngdoc method
         * @name register
         * @methodOf talend.widget.controller:TalendAccordionsCtrl
         * @description Register an accordions item
         */
        vm.register = function register(accordion) {
            vm.accordions.push(accordion);
        };

        /**
         * @ngdoc method
         * @name toggle
         * @methodOf talend.widget.controller:TalendAccordionsCtrl
         * @description Open an accordion and hide the others
         */
        vm.toggle = function toggle(accordion) {
            var state = accordion.active;
            _.forEach(vm.accordions, function(accordionToDeactivate) {
                accordionToDeactivate.active = false;
            });
            accordion.active = !state;
        };
    }

    angular.module('talend.widget')
        .controller('TalendAccordionsCtrl', TalendAccordionsCtrl);
})();