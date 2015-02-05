(function() {
    'use strict';

    function ConfirmClick($window){
        return {
            restrict: 'A',
            link: function(scope, iElement, iAttrs){
                iElement.bind('click', function(e){
                    var message = iAttrs.ngConfirmClick;
                    if(message && !$window.confirm(message)){
                        e.stopImmediatePropagation();
                        e.preventDefault();
                    }
                });
            }
        };
    }

    angular.module('data-prep-utils')
        .directive('ngConfirmClick', ConfirmClick);

})();