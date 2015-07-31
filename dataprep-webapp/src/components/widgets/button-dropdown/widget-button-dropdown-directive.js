(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendButtonDropdown
     * @description This directive create a button dropdown element.
     * @restrict EA
     * @usage
     <talend-button-dropdown button-text="Click Me" button-action="buttonAction()">
        <ul>
            <li>Menu 1</li>
            <li>Menu 2</li>
        </ul>
     </talend-button-dropdown>
     * @param {string} buttonText The text to display in the main button
     * @param {function} buttonAction The callback to execute on main button click
     */
    function TalendButtonDropdown($timeout) {
        return {
            restrict: 'E',
            transclude: true,
            template:   '<talend-dropdown class="button-dropdown" close-on-select="true">   ' +
                        '   <div class="dropdown-container">    ' +
                        '       <button class="button-dropdown-main" ng-click="buttonDropdownCtrl.buttonAction()">  ' +
                        '           <i data-icon="{{buttonDropdownCtrl.buttonIcon}}" class="iconfont" ng-if="buttonDropdownCtrl.buttonIcon"></i>{{buttonDropdownCtrl.buttonText}}   '+
                        '       </button>   '+
                        '       <div class="line-separator"></div>  '+
                        '       <button class="button-dropdown-side dropdown-action"></button>  '+
                        '       <ng-transclude class="dropdown-menu"></ng-transclude>   '+
                        '   </div>  '+
                        '</talend-dropdown> ',
            scope: {
                buttonIcon: '@',
                buttonText: '@',
                buttonAction: '&'
            },
            link: function (scope, iElement, attrs) {

                $timeout(function() {
                    if(!attrs.buttonAction){
                        iElement.find('.button-dropdown-main')
                            .on('click', function(){
                                iElement.find('.dropdown-action').click();
                            });
                    }
                });

            },
            bindToController: true,
            controller: function() {},
            controllerAs: 'buttonDropdownCtrl'
        };
    }

    angular.module('talend.widget')
        .directive('talendButtonDropdown', TalendButtonDropdown);
})();
