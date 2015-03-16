(function() {
    'use strict';

    function FilterListCtrl() {
        var vm = this;

        vm.close = function(text) {
            console.log(text);
        };
    }

    angular.module('data-prep.filter-list')
        .controller('FilterListCtrl', FilterListCtrl);
})();