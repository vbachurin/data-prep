(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.type-transformation-menu.controller:TypeTransformMenuCtrl
     * @description Type Transformation menu controller.
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires data-prep.services.dataset.service:ColumnTypesService
     * @requires data-prep.services.utils.service:ConverterService
     */
    function TypeTransformMenuCtrl(PlaygroundService, ColumnTypesService, ConverterService) {

        var vm = this;

        /**
         * @ngdoc property
         * @name types
         * @propertyOf data-prep.type-transformation-menu.controller:TypeTransformMenuCtrl
         * @description The supported primitive types
         * @type {array}
         */
        vm.types = [];

        ColumnTypesService.getTypes()
            .then(function (types) {
                vm.types = [];
                // TDP-399 we limit numeric types to integer and float (displayed as decimal)
                _.forEach( types,function(type){
                  if (type.id){
                      switch ( type.id.toLowerCase() ) {
                          case 'integer':
                          case 'float':
                          case 'boolean':
                          case 'string':
                          case 'char':
                          case 'date':
                              vm.types.push( type );
                              break;
                          //case 'double':
                          //case 'numeric':
                          //case 'any':
                          default:
                              // ignoring others
                      }
                  }
                } );

            });

        /**
         * @ngdoc method
         * @name checkedSimplifiedType
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @description return if the type must be checked as ui display different types from backend types.
         * @param {object} type the current type to be displayed in the ui menu list
         */
        vm.checkedSimplifiedType = function(type){
            return ConverterService.simplifyType(type.id) === ConverterService.simplifyType(vm.currentDomain);
        };

        /**
         * @ngdoc method
         * @name changeDomain
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @description Change the column domain. It change it on the ui, call the backend and revert the ui if the backend fails
         * @param {object} domain The new domain information
         */
        vm.changeDomain = function changeDomain(domain) {
            var originalDomain = getOriginalDomain();
            setColumnDomainAndType(domain, null);

            /*jshint camelcase: false */
            var parameters = {
                scope: 'column',
                column_id: vm.column.id,
                column_name: vm.column.name,
                new_domain_id: domain.id,
                new_domain_label: domain.label,
                new_domain_frequency: domain.frequency
            };
            PlaygroundService.appendStep('domain_change', parameters)
                .catch(setColumnDomainAndType.bind(vm, originalDomain));
        };

        /**
         * @ngdoc method
         * @name changeType
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @description Change the column type. It change it on the ui, call the backend and revert the ui if the backend fails
         * @param {object} type The new type information
         */
        vm.changeType = function changeType(type) {
            var originalType = vm.column.type;
            var originalDomain = getOriginalDomain();
            setColumnDomainAndType({id: '', label: '', frequency: 0}, type.id);

            /*jshint camelcase: false */
            var parameters = {
                scope: 'column',
                column_id: vm.column.id,
                column_name: vm.column.name,
                new_type: type.id
            };
            PlaygroundService.appendStep('type_change', parameters)
                .catch(setColumnDomainAndType.bind(vm, originalDomain, originalType));
        };

        /**
         * @ngdoc method
         * @name adaptDomains
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @description Adapt the semantic domain list for ui.
         */
        vm.adaptDomains = function adaptDomains() {
            vm.domains = _.chain(vm.column.semanticDomains)
                .filter('id')
                .sortBy('frequency')
                .reverse()
                .value();
            refreshCurrentDomain();
        };

        /**
         * @ngdoc method
         * @name refreshCurrentDomain
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @description Refresh current domain and simplified domain variables
         */
        function refreshCurrentDomain() {
            vm.currentDomain = vm.column.domain ? vm.column.domain : vm.column.type.toUpperCase();
            vm.currentSimplifiedDomain = vm.column.domain ? vm.column.domain : ConverterService.simplifyType(vm.column.type);
        }

        /**
         * @ngdoc method
         * @name setColumnDomainAndType
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @description Update current column domain and type
         * @param {object} domain The semantic domain
         * @param {object} type The type
         */
        function setColumnDomainAndType(domain, type) {
            vm.column.domain = domain.id;
            vm.column.domainLabel = domain.label;
            vm.column.domainFrequency = domain.frequency;
            if (type) {
                vm.column.type = type;
            }

            refreshCurrentDomain();
        }

        /**
         * @ngdoc method
         * @name getOriginalDomain
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @description Get the column original domain infos
         * @return {object} the current column domain infos
         */
        function getOriginalDomain() {
            return {
                id: vm.column.domain,
                label: vm.column.domainLabel,
                frequency: vm.column.domainFrequency
            };
        }
    }

    angular.module('data-prep.type-transformation-menu')
        .controller('TypeTransformMenuCtrl', TypeTransformMenuCtrl);
})();