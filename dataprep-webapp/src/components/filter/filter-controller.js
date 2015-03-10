(function() {
    'use strict';

    function FilterCtrl() {
        var vm = this;

        vm.close = function(text) {
            console.log(text);
        };
    }

    angular.module('data-prep-filter')
        .controller('FilterCtrl', FilterCtrl);
})();