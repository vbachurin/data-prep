(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.onboarding.service:OnboardingService
     * @description OnboardingService service. This service exposes functions to start onboarding tours
     */
    function OnboardingService($window, datasetTour) {
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
         * @name startTour
         * @methodOf data-prep.services.onboarding.service:OnboardingService
         * @description Configure and start an onboarding tour
         */
        this.startTour = function startTour() {
            introJs()
                .setOptions({
                    nextLabel: 'NEXT',
                    prevLabel: 'BACK',
                    skipLabel: 'SKIP',
                    doneLabel: 'OK, LET ME TRY!',
                    steps: createIntroSteps(datasetTour)
                })
                .oncomplete(function() {
                    var options = getTourOptions();
                    options.done = true;
                    setTourOptions(options);
                })
                .start();
        };

        /**
         * @ngdoc method
         * @name shouldStartTour
         * @methodOf data-prep.services.onboarding.service:OnboardingService
         * @description Check if the tour should be started depending on the saved options
         * @return {boolean} True if the tour has not been completed yet
         */
        this.shouldStartTour = function shouldStartTour() {
            return !getTourOptions().done;
        };
    }

    angular.module('data-prep.services.onboarding')
        .service('OnboardingService', OnboardingService);
})();