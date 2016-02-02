/**
 * @ngdoc directive
 * @name data-prep.actions-suggestions.directive:actionsSuggestions
 * @description Actions and suggestions list element
 * @restrict E
 * @usage <actions-suggestions></actions-suggestions>
 * */
export default function ActionsSuggestions($timeout) {
    'ngInject';

    return {
        restrict: 'E',
        templateUrl: 'app/components/suggestions-stats/actions-suggestions/actions-suggestions.html',
        bindToController: true,
        controllerAs: 'actionsSuggestionsCtrl',
        controller: 'ActionsSuggestionsCtrl',
        link: function (scope, iElement, iAttrs, ctrl) {

            //Scroll the actual tab container to the bottom of the element to display
            ctrl.scrollToBottom = function scrollToBottom(elementToDisplay) {
                $timeout(function () {
                    var splitHandler = angular.element('.split-handler').eq(0);
                    var tabContainer = iElement.find('.action-suggestion-tab-items').eq(0);
                    var etdContainer = elementToDisplay.find('>.accordion >.content-container >.content');
                    if (!etdContainer.length) {
                        return;
                    }

                    var tabOffset = tabContainer.offset();
                    var etdOffset = etdContainer.offset();

                    var etdHeight = etdContainer.height();

                    var availableTopSpace = etdOffset.top - tabOffset.top;
                    var scrollDistance;
                    if (availableTopSpace >= etdHeight) {
                        if (etdOffset.top > (splitHandler.offset().top - etdHeight)) {
                            scrollDistance = tabContainer[0].scrollTop + etdHeight;
                            tabContainer.animate({
                                scrollTop: scrollDistance
                            }, 500);
                        }
                    }
                    else {
                        var accordionTriggerHeight = elementToDisplay.find('>.accordion >.trigger-container').height();
                        scrollDistance = tabContainer[0].scrollTop + availableTopSpace - accordionTriggerHeight;
                        tabContainer.animate({
                            scrollTop: scrollDistance
                        }, 500);
                    }
                }, 200, false);
            };
        }
    };
}