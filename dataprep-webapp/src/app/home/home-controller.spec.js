describe('Home controller', function() {
    'use strict';

    var createController, scope;

    beforeEach(module('data-prep'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('HomeCtrl', {
                $scope: scope
            });
            return ctrl;
        };

    }));

});
