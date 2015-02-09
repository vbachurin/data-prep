(function () {
  'use strict';

    /**
     * Dropdown directive
     * 
     * Example :
     * <talend-dropdown>
     * <div class="dropdown-container grid-header">
     *      <div class="dropdown-action">
     *          <div class="grid-header-title dropdown-button">{{ column.id }}</div>
     *          <div class="grid-header-type">{{ column.type }}</div>
     *      </div>
     *      <ul class="dropdown-menu">
     *          <li role="presentation"><a role="menuitem" tabindex="-1" href="#">Hide Column {{ column.id | uppercase }}</a></li>
     *          <li class="divider"></li>
     *          <li role="presentation"><a role="menuitem" tabindex="-1" href="#">Split first Space</a></li>
     *          <li role="presentation"><a role="menuitem" tabindex="-1" href="#">Uppercase</a></li>
     *      </ul>
     * </div>
     * </talend-dropdown>
     * 
     * Class 'dropdown-action' : action zone that trigger menu toggle
     * Class 'dropdown-button' : add a caret at the end off element
     * Class 'dropdown-menu' : menu
     * Class 'dorpdown-menu > li' : menu items
     *
     * @returns directive
     */
  function TalendDropdown() {
    return {
      restrict: 'EA',
      transclude: true,
      templateUrl: 'components/widgets/dropdown/dropdown.html',
      link: {
        post: function(scope, iElement) {
            var menu = iElement.find('.dropdown-menu');
            
            var hideAllDropDowns = function() {
                angular.element('.dropdown-menu').removeClass('show-menu');
            };
            
            var hideMenu = function(){
                menu.removeClass('show-menu');
            };

            // Show or hide menu on action zone click
            iElement.find('.dropdown-action').on('click', function(event) {
                event.stopPropagation();
                
                var hasClass = menu.hasClass('show-menu');
                hideAllDropDowns();

                hasClass ? menu.removeClass('show-menu') : menu.addClass('show-menu');
            });
            //hide menu on menu item select
            iElement.find('.dropdown-menu > li').click(hideMenu);
            //hide menu on body click
            angular.element('body').click(hideMenu);
        }
      }
    };
  }

  angular.module('talend.widget')
    .directive('talendDropdown', TalendDropdown);
})();
