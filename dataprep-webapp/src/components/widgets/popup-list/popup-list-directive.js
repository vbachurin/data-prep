(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendPopUpList
     * @description PopUpList widget
     * @restrict E
     */
    function TalendPopUpList (){
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            templateUrl: 'components/widgets/popup-list/popup-list.html',
            link: function (scope, iElement) {

                var listTitle = iElement.find('.list-title');
                var list = iElement.find('.list-content');
                var listCloseButton =  iElement.find('.list-content-close');
                var listWrapper =  angular.element('.list-wrapper');

                list.removeClass('visible');
                listCloseButton.css('display', 'none');

                listTitle.on('click', function() {
                    if(list.css('display') === 'none') {
                        list.addClass('visible');
                        listCloseButton.css('display', 'inline-block');
                        listWrapper.css('background-color', '#DDDDDD');

                    }
                });
                listCloseButton.on('click', function() {
                    if(list.css('display') === 'block') {
                        list.removeClass('visible');
                        listCloseButton.css('display', 'none');
                        listWrapper.css('background-color', 'transparent');
                    }
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendPopUpList', TalendPopUpList);
})();