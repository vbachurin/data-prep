describe('DatasetColumnHeader controller', function() {
    'use strict';

    var createController, scope;
    var result = {'records':
        [{'firstname':'Grover','avgAmount':'82.4','city':'BOSTON','birth':'01-09-1973','registration':'17-02-2008','id':'1','state':'AR','nbCommands':'41','lastname':'Quincy'},{'firstname':'Warren','avgAmount':'87.6','city':'NASHVILLE','birth':'11-02-1960','registration':'18-08-2007','id':'2','state':'WA','nbCommands':'17','lastname':'Johnson'}]
    };

    beforeEach(module('data-prep-dataset'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('DatasetGridCtrl', {
                $scope: scope
            });
            return ctrl;
        };
    }));

    it('should call transform service with loading modal show/hide', inject(function($rootScope) {
        //given
        var ctrl = createController();
        ctrl.data = {};
        scope.$digest();

        //when
        $rootScope.$emit('talend.dataset.transform', {data: result});
        $rootScope.$apply();

        //then
        expect(ctrl.data.records).toBe(result.records);
    }));
});
