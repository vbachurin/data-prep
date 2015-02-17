describe('DatasetColumnHeader controller', function() {
    'use strict';

    var createController, scope;
    var result = {'records':
        [{'firstname':'Grover','avgAmount':'82.4','city':'BOSTON','birth':'01-09-1973','registration':'17-02-2008','id':'1','state':'AR','nbCommands':'41','lastname':'Quincy'},{'firstname':'Warren','avgAmount':'87.6','city':'NASHVILLE','birth':'11-02-1960','registration':'18-08-2007','id':'2','state':'WA','nbCommands':'17','lastname':'Johnson'}]
    };
    var metadata = {
        'id':'44f5e4ef-96e9-4041-b86a-0bee3d50b18b',
        'name':'customers_jso_light',
        'author':'anonymousUser',
        'records':15,
        'nbLinesHeader':1,
        'nbLinesFooter':0,
        'created':'02-16-2015 08:52'};
    var column = {
        'id': 'MostPopulousCity',
        'quality': {
            'empty': 5,
            'invalid': 10,
            'valid': 72
        },
        'type': 'string'
    };

    beforeEach(module('data-prep-dataset'));

    beforeEach(inject(function($rootScope, $controller, $q, TransformationService, DatasetGridService) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('DatasetColumnHeaderCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        DatasetGridService.setDataset({}, {});

        spyOn(TransformationService, 'transform').and.returnValue($q.when({data: result}));
        spyOn($rootScope, '$emit').and.callThrough();
        spyOn(DatasetGridService, 'updateRecords').and.callThrough();
    }));

    it('should call transform service with loading modal show/hide', inject(function($rootScope, TransformationService, DatasetGridService) {
        //given
        var action = 'uppercase';
        var ctrl = createController();
        ctrl.metadata = metadata;
        ctrl.column = column;
        scope.$digest();

        //when
        ctrl.transform(action);
        expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
        $rootScope.$apply();

        //then
        expect(TransformationService.transform).toHaveBeenCalledWith(metadata.id, action, {'column_name': column.id});
        expect(DatasetGridService.updateRecords).toHaveBeenCalledWith(result.records);
        expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
    }));
});
