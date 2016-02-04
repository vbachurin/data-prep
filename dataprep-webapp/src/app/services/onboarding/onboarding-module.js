import datasetTour from './onboarding-datasets-constants';
import playgroundTour from './onboarding-playground-constants';
import recipeTour from './onboarding-recipe-constants';
import OnboardingService from './onboarding-service';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.onboarding
     * @description This module contains the services to manage onboarding tours
     */
    angular.module('data-prep.services.onboarding', [])
        .constant('datasetTour', datasetTour)
        .constant('playgroundTour', playgroundTour)
        .constant('recipeTour', recipeTour)
        .service('OnboardingService', OnboardingService);
})();