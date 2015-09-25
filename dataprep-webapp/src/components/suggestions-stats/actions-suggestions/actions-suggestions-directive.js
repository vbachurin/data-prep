(function() {
    'use strict';

    function ActionsSuggestions($timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/suggestions-stats/actions-suggestions/actions-suggestions.html',
            bindToController: true,
            controllerAs: 'actionsSuggestionsCtrl',
            controller: 'ActionsSuggestionsCtrl',
            link: function (scope, iElement, iAttrs, ctrl) {

                ctrl.scrollToBottom = function scrollToBottom(elm) {
                    $timeout(function(){
                        var contentContainer = elm.find('>.content-container');
                        var availablelTopSpace = contentContainer.offset().top - $('.action-suggestion-tab-items').offset().top;
                        var scrollTo = 0;
                        if(availablelTopSpace >= contentContainer.context.clientHeight) {
                            if (contentContainer.offset().top > ($('.split-handler').offset().top -  contentContainer.context.clientHeight)){
                                scrollTo = $('.action-suggestion-tab-items')[0].scrollTop + contentContainer.context.clientHeight;
                                $('.action-suggestion-tab-items').animate({
                                    scrollTop: scrollTo
                                }, 500);
                            }
                        } else {
                            scrollTo = $('.action-suggestion-tab-items')[0].scrollTop + availablelTopSpace - $('.accordion').height();
                            $('.action-suggestion-tab-items').animate({
                                scrollTop: scrollTo
                            }, 500);
                        }
                    },200);
                };
            }
        };
    }

    angular.module('data-prep.actions-suggestions')
        .directive('actionsSuggestions', ActionsSuggestions);
})();