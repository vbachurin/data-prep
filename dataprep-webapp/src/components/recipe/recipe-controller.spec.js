/*jshint camelcase: false */

describe('Recipe controller', function() {
    'use strict';

    var createController, scope;
    var lastActiveStep = {inactive: false};

    beforeEach(module('data-prep.recipe'));

    beforeEach(inject(function($rootScope, $controller, $q, $timeout, RecipeService, PlaygroundService, PreparationService, PreviewService) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('RecipeCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        spyOn($rootScope, '$emit').and.callThrough();
        spyOn(RecipeService, 'refresh').and.callFake(function() {
            var recipe = RecipeService.getRecipe();
            recipe.splice(0, recipe.length);
            recipe.push(lastActiveStep);
        });
        spyOn(PlaygroundService, 'updateStep').and.returnValue($q.when(true));
        spyOn(PreviewService, 'getPreviewDiffRecords').and.returnValue($q.when(true));
        spyOn(PreviewService, 'getPreviewUpdateRecords').and.returnValue($q.when(true));
        spyOn(PreviewService, 'cancelPreview').and.returnValue(null);
        spyOn($timeout, 'cancel').and.callThrough();
    }));

    it('should bind recipe getter with RecipeService', inject(function(RecipeService) {
        //given
        var ctrl = createController();
        expect(ctrl.recipe).toEqual([]);

        var column = {id: 'colId'};
        var transformation = {
            name: 'split',
            category: 'split',
            parameters: [],
            items: []
        };

        //when
        RecipeService.getRecipe().push({
            column: column,
            transformation: transformation
        });

        //then
        expect(ctrl.recipe.length).toBe(1);
        expect(ctrl.recipe[0].column).toBe(column);
        expect(ctrl.recipe[0].transformation).toEqual(transformation);
    }));

    it('should create a closure that update the step parameters', inject(function($rootScope, PlaygroundService) {
        //given
        var ctrl = createController();
        var step = {
            column: {id: 'state'},
            transformation: {
                stepId: 'a598bc83fc894578a8b823',
                name: 'cut'
            },
            actionParameters: {
                action: 'cut',
                parameters: {pattern: '.', column_name: 'state'}
            }
        };
        var parameters = {pattern: '-'};

        //when
        var updateClosure = ctrl.stepUpdateClosure(step);
        updateClosure(parameters);
        $rootScope.$digest();

        //then
        expect(PlaygroundService.updateStep).toHaveBeenCalledWith(step, parameters);
    }));

    it('should update step when parameters are different', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        var step = {
            column: {id: 'state'},
            transformation: {
                stepId: 'a598bc83fc894578a8b823',
                name: 'cut'
            },
            actionParameters: {
                action: 'cut',
                parameters: {pattern: '.', column_name: 'state', column_id: '0001', scope: 'column'}
            }
        };
        var parameters = {pattern: '-'};

        //when
        ctrl.updateStep(step, parameters);

        //then
        expect(PlaygroundService.updateStep).toHaveBeenCalledWith(step, {pattern: '-', column_name: 'state', column_id: '0001', scope: 'column'});
    }));

    it('should hide parameters modal on update step when parameters are different', inject(function($rootScope) {
        //given
        var ctrl = createController();
        var step = {
            column: {id: 'state'},
            transformation: {
                stepId: 'a598bc83fc894578a8b823',
                name: 'cut'
            },
            actionParameters: {
                action: 'cut',
                parameters: {pattern: '.', column_name: 'state'}
            }
        };
        var parameters = {pattern: '-'};
        ctrl.showModal = {'a598bc83fc894578a8b823' : true};

        //when
        ctrl.updateStep(step, parameters);
        $rootScope.$digest();

        //then
        expect(ctrl.showModal).toEqual({});
    }));

    it('should do nothing if parameters are unchanged', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        var step = {
            column: {id: '0', name:'state'},
            transformation: {
                stepId: 'a598bc83fc894578a8b823',
                name: 'cut'
            },
            actionParameters: {
                action: 'cut',
                parameters: {pattern: '.', column_id: '0', column_name: 'state'}
            }
        };
        var parameters = {pattern: '.'};

        //when
        ctrl.updateStep(step, parameters);

        //then
        expect(PlaygroundService.updateStep).not.toHaveBeenCalled();
    }));

    it('should do nothing on update preview if the step is inactive', inject(function($rootScope, PreviewService) {
        //given
        var ctrl = createController();
        var step = {
            column: {id: 'state'},
            transformation: {
                stepId: 'a598bc83fc894578a8b823',
                name: 'cut'
            },
            actionParameters: {
                action: 'cut',
                parameters: {pattern: '.', column_name: 'state'}
            },
            inactive: true
        };
        var parameters = {pattern: '--'};
        var closure = ctrl.previewUpdateClosure(step);

        //when
        closure(parameters);
        $rootScope.$digest();

        //then
        expect(PreviewService.getPreviewUpdateRecords).not.toHaveBeenCalled();
    }));

    it('should do nothing on update preview if the params have not changed', inject(function($rootScope, PreviewService) {
        //given
        var ctrl = createController();
        var step = {
            column: {id: '0', name:'state'},
            transformation: {
                stepId: 'a598bc83fc894578a8b823',
                name: 'cut'
            },
            actionParameters: {
                action: 'cut',
                parameters: {pattern: '.', column_id:'0', column_name: 'state'}
            }
        };
        var parameters = {pattern: '.'};
        var closure = ctrl.previewUpdateClosure(step);

        //when
        closure(parameters);
        $rootScope.$digest();

        //then
        expect(PreviewService.getPreviewUpdateRecords).not.toHaveBeenCalled();
    }));

    it('should call update preview', inject(function($rootScope, PreviewService, RecipeService) {
        //given
        RecipeService.refresh(); //set last active step for the test : see mock
        $rootScope.$digest();

        var ctrl = createController();
        var step = {
            column: {id: '0', name:'state'},
            transformation: {
                stepId: 'a598bc83fc894578a8b823',
                name: 'cut'
            },
            actionParameters: {
                action: 'cut',
                parameters: {pattern: '.', column_id: '0', column_name: 'state', scope: 'column'}
            }
        };
        var parameters = {pattern: '--'};
        var closure = ctrl.previewUpdateClosure(step);

        //when
        closure(parameters);
        $rootScope.$digest();

        //then
        expect(PreviewService.getPreviewUpdateRecords).toHaveBeenCalledWith(lastActiveStep, step, {pattern: '--', column_id: '0', column_name: 'state', scope: 'column'});
    }));

    describe('step parameters', function () {
        it('should return that step has dynamic parameters when it has cluster', function() {
            //given
            var ctrl = createController();
            var step = {
                transformation: {
                    cluster: {}
                }
            };

            //when
            var hasDynamicParams = ctrl.hasDynamicParams(step);

            //then
            expect(hasDynamicParams).toBeTruthy();
        });

        it('should return that step has NO dynamic parameters', function() {
            //given
            var ctrl = createController();
            var step = {
                transformation: {}
            };

            //when
            var hasDynamicParams = ctrl.hasDynamicParams(step);

            //then
            expect(hasDynamicParams).toBeFalsy();
        });

        it('should return that step has static parameters when it has simple params', function() {
            //given
            var ctrl = createController();
            var step = {
                transformation: {
                    parameters: [{}]
                }
            };

            //when
            var hasStaticParams = ctrl.hasStaticParams(step);

            //then
            expect(hasStaticParams).toBeTruthy();
        });

        it('should return that step has static parameters when it has choice params', function() {
            //given
            var ctrl = createController();
            var step = {
                transformation: {
                    items: [{}]
                }
            };

            //when
            var hasStaticParams = ctrl.hasStaticParams(step);

            //then
            expect(hasStaticParams).toBeTruthy();
        });

        it('should return that step has NO static parameters', function() {
            //given
            var ctrl = createController();
            var step = {
                transformation: {}
            };

            //when
            var hasStaticParams = ctrl.hasStaticParams(step);

            //then
            expect(hasStaticParams).toBeFalsy();
        });

        it('should return that step has parameters when it has static params', function() {
            //given
            var ctrl = createController();
            var step = {
                transformation: {
                    parameters: [{}]
                }
            };

            //when
            var hasParams = ctrl.hasParameters(step);

            //then
            expect(hasParams).toBeTruthy();
        });

        it('should return that step has parameters when it has dybamic params', function() {
            //given
            var ctrl = createController();
            var step = {
                transformation: {
                    cluster: []
                }
            };

            //when
            var hasParams = ctrl.hasParameters(step);

            //then
            expect(hasParams).toBeTruthy();
        });

        it('should return that step has NO parameters', function() {
            //given
            var ctrl = createController();
            var step = {
                transformation: {}
            };

            //when
            var hasParams = ctrl.hasParameters(step);

            //then
            expect(hasParams).toBeFalsy();
        });
    });

    describe('remove step', function() {
        beforeEach(inject(function(PlaygroundService) {
            spyOn(PlaygroundService, 'removeStep').and.returnValue();
        }));

        it('should prompt a confirm popup', inject(function($q, TalendConfirmService) {
            //given
            spyOn(TalendConfirmService, 'confirm').and.returnValue($q.when());
            var ctrl = createController();
            var step = {};

            expect(TalendConfirmService.confirm).not.toHaveBeenCalled();

            //when
            ctrl.remove(step);

            //then
            expect(TalendConfirmService.confirm).toHaveBeenCalledWith({disableEnter: true}, ['DELETE_STEP']);
        }));

        it('should remove step in cascade mode on user confirmation', inject(function($q, PlaygroundService, TalendConfirmService) {
            //given
            spyOn(TalendConfirmService, 'confirm').and.returnValue($q.when());
            var ctrl = createController();
            var step = {};

            //when
            ctrl.remove(step);
            scope.$digest();

            //then
            expect(PlaygroundService.removeStep).toHaveBeenCalledWith(step, 'cascade');
        }));

        it('should NOT remove step on user dismiss', inject(function($q, PlaygroundService, TalendConfirmService) {
            //given
            spyOn(TalendConfirmService, 'confirm').and.returnValue($q.reject());
            var ctrl = createController();
            var step = {};

            //when
            ctrl.remove(step);
            scope.$digest();

            //then
            expect(PlaygroundService.removeStep).not.toHaveBeenCalled();
        }));
    });
});
