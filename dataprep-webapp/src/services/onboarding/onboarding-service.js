(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.onboarding.service:OnboardingService
     * @description OnboardingService service. This service exposes functions to start onboarding tours
     */
    function OnboardingService($window, datasetTour, preparationTour) {

        var TOUR_OPTIONS_KEY = 'org.talend.dataprep.tour_options';

        /**
         * @ngdoc property
         * @name template
         * @propertyOf data-prep.services.onboarding.service:OnboardingService
         * @description The step template with title and content
         */
        var template = '<div class="introjs-tooltiptitle"><%= title %></div>' +
            '<div class="introjs-tooltipcontent"><%= content %></div>';

        /**
         * @ngdoc method
         * @name createIntroSteps
         * @methodOf data-prep.services.onboarding.service:OnboardingService
         * @param {Array} configs The array of configs, one config for each step
         * @description Create the Intro.js steps
         * @returns {Array} The Intro.js steps
         */
        var createIntroSteps = function createIntroSteps(configs) {
            return _.map(configs, function (config) {
                return {
                    element: config.element,
                    position: config.position,
                    intro: _.template(template)(config)
                };
            });
        };

        /**
         * @ngdoc method
         * @name getTourOptions
         * @methodOf data-prep.services.onboarding.service:OnboardingService
         * @description Get options from localstorage
         * @returns {object} The saved tour config
         */
        var getTourOptions = function getTourOptions() {
            var tourOptionsString = $window.localStorage.getItem(TOUR_OPTIONS_KEY);
            return tourOptionsString ? JSON.parse(tourOptionsString) : {};
        };

        /**
         * @ngdoc method
         * @name setTourOptions
         * @methodOf data-prep.services.onboarding.service:OnboardingService
         * @param {object} options The options to save
         * @description Set options in localstorage
         */
        var setTourOptions = function setTourOptions(options) {
            $window.localStorage.setItem(TOUR_OPTIONS_KEY, JSON.stringify(options));
        };

        /**
         * @ngdoc method
         * @name getTourConstantsFromTourId
         * @methodOf data-prep.services.onboarding.service:OnboardingService
         * @param {String} The tour Id
         * @description Get Tour options constant
         * @returns {array} Tour options constant
         */
        var getTourConstantsFromTourId = function getTourConstantsFromTourId(tour) {
            switch(tour) {
                case 'dataset':
                    return datasetTour;
                case 'preparation':
                    return preparationTour;
                default:
                    return datasetTour;
            }
        };

        /**
         * @ngdoc method
         * @name setTourDone
         * @methodOf data-prep.services.onboarding.service:OnboardingService
         * @param {String} The tour Id
         * @description Set tour done in localStorage
         */
        var setTourDone = function setTourDone(tour) {
            var options = getTourOptions();
            options[tour] = true;
            setTourOptions(options);
        };

        /**
         * @ngdoc method
         * @name startTour
         * @methodOf data-prep.services.onboarding.service:OnboardingService
         * @param {String} The tour Id
         * @description Configure and start an onboarding tour
         */
        this.startTour = function startTour(tour) {
            introJs()
                .setOptions({
                    nextLabel: 'NEXT',
                    prevLabel: 'BACK',
                    skipLabel: 'SKIP',
                    doneLabel: 'OK, LET ME TRY!',
                    steps: createIntroSteps(getTourConstantsFromTourId(tour))
                })
                .oncomplete(function() {
                    setTourDone(tour);
                })
                .onexit(function() {
                    setTourDone(tour);
                })
                .start();
        };

        /**
         * @ngdoc method
         * @name shouldStartTour
         * @methodOf data-prep.services.onboarding.service:OnboardingService
         * @description Check if the tour should be started depending on the saved options
         * @param {String} The tour Id
         * @return {boolean} True if the tour has not been completed yet
         */
        this.shouldStartTour = function shouldStartTour(tour) {
            var tourOptions = getTourOptions();
            return !tourOptions[tour];
        };
    }

    angular.module('data-prep.services.onboarding')
        .service('OnboardingService', OnboardingService);
})();