describe('recipeBullet directive', function() {

	'use strict';
	var createElement, element, scope, recipeBulletCtrl, mochStepHoverStart, createRecipeBulletController;
	beforeEach(module('data-prep.recipe'));
	beforeEach(module('data-prep.recipeBullet'));
	beforeEach(module('htmlTemplates'));
	beforeEach(inject(function($rootScope, $compile, $controller, RecipeService, BulletService ) {
		'use strict';

		//createRecipeBulletController = function () {
		//	var ctrl = $controller('RecipeBulletCtrl', {
		//		$scope: scope,
		//		$element: element
		//	});
		//	return ctrl;
		//};

		//createRecipeController = function () {
		//	var ctrl = $controller('RecipeCtrl', {
		//		$scope: scope
		//	});
		//	return ctrl;
		//};

		//recipeBulletCtrl = createRecipeBulletController();

		mochStepHoverStart = function(directiveIndex){
			recipeBulletCtrl = element.eq(directiveIndex).controller('recipeBullet');
			//console.log("99999999999999999", element.eq(0));
			spyOn(BulletService, 'stepHoverStart').and.returnValue();
		}

		createElement = function(steps) {
			scope = $rootScope.$new();
			//push all steps in scope
			scope.step1 = steps[0];
			scope.step2 = steps[1];
			scope.step3 = steps[2];
			scope.step4 = steps[3];
			scope.step5 = steps[4];

			//call ParentController
			//recipeCtrl = createRecipeController();
			//
			//scope.recipeBulletCtrl = createRecipeBulletController();

			//push all steps in RecipeService
			RecipeService.getRecipe().push(steps[0]);
			RecipeService.getRecipe().push(steps[1]);
			RecipeService.getRecipe().push(steps[2]);
			RecipeService.getRecipe().push(steps[3]);
			RecipeService.getRecipe().push(steps[4]);

			element = angular.element(
				'<recipe-bullet step="step1" type="startChain" ></recipe-bullet>' +
				'<recipe-bullet step="step2" type="middleChain" ></recipe-bullet>' +
				'<recipe-bullet step="step3" type="middleChain" ></recipe-bullet>' +
				'<recipe-bullet step="step4" type="middleChain" ></recipe-bullet>' +
				'<recipe-bullet step="step5" type="endChain" ></recipe-bullet>');

			$compile(element)(scope);
			scope.$digest();
		};
	}));

	afterEach(inject(function(RecipeService ) {
		scope.$destroy();
		element.remove();
		RecipeService.reset();
	}));

	it('check if the element was correctly created', function() {
		//given
		var step1 = {'column':{'id':'col2'},'transformation':{'name':'uppercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':true};
		var step2 = {'column':{'id':'col1'},'transformation':{'name':'lowerercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':true};
		var step3 = {'column':{'id':'col3'},'transformation':{'name':'negate','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false};
		var step4 = {'column':{'id':'col4'},'transformation':{'name':'propercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false};
		var step5 = {'column':{'id':'col1'},'transformation':{'name':'rename','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false};

		//crée element
		createElement([step1,step2,step3,step4,step5]);
		//assert
		expect(element.find('svg').length).toBe(5);
	});
//
//	it('onmouseenter test on Launch, all the elements are active', function() {
//		//given
//		var step1 = {'column':{'id':'col2'},'transformation':{'name':'uppercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false, rank:0};
//		var step2 = {'column':{'id':'col1'},'transformation':{'name':'lowerercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false, rank:1};
//		var step3 = {'column':{'id':'col3'},'transformation':{'name':'negate','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false, rank:2};
//		var step4 = {'column':{'id':'col4'},'transformation':{'name':'propercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false, rank:3};
//		var step5 = {'column':{'id':'col1'},'transformation':{'name':'rename','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false, rank:4};
//		createElement([step1,step2,step3,step4,step5]);
//		//create event
//		var event = angular.element.Event('mouseenter');
//
//		//enable angular.element('.svg-all-cls') selection
//		var body = angular.element('body');
//		body.append(element);
//		body.append(angular.element("<svg class='tout-svg-cls'><path d='M 15 0 L 15 30 A' /> <circle cx=15 cy=15 r=10 /></svg>"));
//		mochStepHoverStart(0);
//
//		//then
//		//when trigger mouseenter on 1st element
//		//console.log(element[0],"---------------------");
//
//		element.eq(0).trigger(event);
//		scope.$digest();
//
//		//assert
//		var nbreEnaHov = angular.element('.maillon-circle-enabled-hovered').length;
//		expect(nbreEnaHov).toBe(5);
//	});
//
//	it('onmouseenter test when all the elements are inactive', function() {
//		//given
//		var step1 = {'column':{'id':'col2'},'transformation':{'name':'uppercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':true, rank:0};
//		var step2 = {'column':{'id':'col1'},'transformation':{'name':'lowerercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':true, rank:1};
//		var step3 = {'column':{'id':'col3'},'transformation':{'name':'negate','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':true, rank:2};
//		var step4 = {'column':{'id':'col4'},'transformation':{'name':'propercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':true, rank:3};
//		var step5 = {'column':{'id':'col1'},'transformation':{'name':'rename','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':true, rank:4};
//		createElement([step1,step2,step3,step4,step5]);
//		mochStepHoverStart(2);
//		//create event
//		var event = angular.element.Event('mouseenter');
//
//		//because the only way to get all the bullets disabled is to click on the 1st bullet
//		spyOn(recipeBulletCtrl.recipeService, 'getActiveThresholdStepIndexOnLaunch').and.returnValue(-1);
//
//		//enable angular.element('.svg-all-cls') selection
//		var body = angular.element('body');
//		body.append(element);
//		//then
//		//when trigger mouseenter on 1st element
//		element.eq(2).trigger(event);
//		scope.$digest();
//
//		//assert
//		var nbreDisaHov = angular.element('.maillon-circle-disabled-hovered').length;
//		expect(nbreDisaHov).toBe(3);
//
//		var rightClassName = element.eq(0).find('circle')[0].getAttribute("class")
//			=== element.eq(1).find('circle')[0].getAttribute("class")
//			&& element.eq(2).find('circle')[0].getAttribute("class")
//			=== "maillon-circle-disabled-hovered";
//		expect(rightClassName).toBe(true);
//	});
//
//	it('onmouseenter test when 3 first bullets are active 2 are inactive and the 2nd was hovered', function() {
//		//given
//		var step1 = {'column':{'id':'col2'},'transformation':{'name':'uppercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false, rank:0};
//		var step2 = {'column':{'id':'col1'},'transformation':{'name':'lowerercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false, rank:1};
//		var step3 = {'column':{'id':'col3'},'transformation':{'name':'negate','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false, rank:2};
//		var step4 = {'column':{'id':'col4'},'transformation':{'name':'propercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':true, rank:3};
//		var step5 = {'column':{'id':'col1'},'transformation':{'name':'rename','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':true, rank:4};
//		createElement([step1,step2,step3,step4,step5]);
//		mochStepHoverStart(1);
//		//create event
//		var event = angular.element.Event('mouseenter');
//
//		//because the only way to get all the bullets disabled is to click on the 1st bullet
//		spyOn(recipeBulletCtrl.recipeService, 'getActiveThresholdStepIndexOnLaunch').and.returnValue(2);
//
//		//enable angular.element('.svg-all-cls') selection
//		var body = angular.element('body');
//		body.append(element);
//		//then
//		//when trigger mouseenter on 1st element
//		element.eq(1).trigger(event);
//		scope.$digest();
//
//		//assert
//		var nbreDisaHov = angular.element('.maillon-circle-enabled-hovered').length;
//		expect(nbreDisaHov).toBe(2);
//
//		var rightClassName = element.eq(1).find('circle')[0].getAttribute("class")
//			=== element.eq(2).find('circle')[0].getAttribute("class")
//			&& element.eq(2).find('circle')[0].getAttribute("class")
//			=== "maillon-circle-enabled-hovered";
//		expect(rightClassName).toBe(true);
//	});
//
//
//	it('onmouseenter test when 3 first bullets are active 2 are inactive and the last one was hovered', function() {
//		//given
//		var step1 = {'column':{'id':'col2'},'transformation':{'name':'uppercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false, rank:0};
//		var step2 = {'column':{'id':'col1'},'transformation':{'name':'lowerercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false, rank:1};
//		var step3 = {'column':{'id':'col3'},'transformation':{'name':'negate','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':false, rank:2};
//		var step4 = {'column':{'id':'col4'},'transformation':{'name':'propercase','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':true, rank:3};
//		var step5 = {'column':{'id':'col1'},'transformation':{'name':'rename','label':'To uppercase','category':'case','parameters':[],'items':[]},'inactive':true, rank:4};
//		createElement([step1,step2,step3,step4,step5]);
//		mochStepHoverStart(4);
//		//create event
//		var event = angular.element.Event('mouseenter');
//
//		//because the only way to get all the bullets disabled is to click on the 1st bullet
//		spyOn(recipeBulletCtrl.recipeService, 'getActiveThresholdStepIndexOnLaunch').and.returnValue(2);
//
//		//enable angular.element('.svg-all-cls') selection
//		var body = angular.element('body');
//		body.append(element);
//		//then
//		//when trigger mouseenter on 1st element
//		element.eq(4).trigger(event);
//		scope.$digest();
//
//		//assert
//		var nbreDisaHov = angular.element('.maillon-circle-disabled-hovered').length;
//		expect(nbreDisaHov).toBe(2);
//
//		var rightClassName = element.eq(3).find('circle')[0].getAttribute("class")
//			=== element.eq(4).find('circle')[0].getAttribute("class")
//			&& element.eq(4).find('circle')[0].getAttribute("class")
//			=== "maillon-circle-disabled-hovered";
//		expect(rightClassName).toBe(true);
//	});


});