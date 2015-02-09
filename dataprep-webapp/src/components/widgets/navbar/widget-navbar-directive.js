(function() {
    'use strict';

    /**
     * Navbar widget
     * 
     * <talend-navbar role="banner">
     *      <a href="http://dev.talend.com" class="logo-wrapper">
     *          <span class="logo"></span>
     *      </a>
     *      <a href="javascript:void(0)" class="navigation-menu-button">MENU</a>
     *      <nav role="navigation">
     *          <ul class="navigation-menu show">
     *              <li class="nav-link"><a href="http://dev.talend.com" class="icon t-top-bar_folder"></a></li>
     *              <li class="nav-link"><a href="http://dev.talend.com" class="icon t-top-bar_marketplace"></a></li>
     *              <li class="nav-link"><a href="http://dev.talend.com" class="icon t-top-bar_share"></a></li>
     *              <li class="nav-link"><a href="http://dev.talend.com" class="icon t-top-bar_notification"></a></li>
     *              <li class="nav-link more"><a href="javascript:void(0)" class="icon t-top-bar_profile nomore"></a>
     *                  <ul class="submenu">
     *                      <li><a href="javascript:void(0)">Logout</a></li>
     *                      <li class="divider"></li>
     *                      <li><a href="javascript:void(0)">Edit profil</a></li>
     *                  </ul>
     *              </li>
     *          </ul>
     *      </nav>
     *      <div class="navigation-tools">
     *          <div class="search-bar">
     *              <form role="search">
     *                  <input type="search" placeholder="Search anything here" />
     *              </form>
     *          </div>
     *      </div>
     * </talend-navbar>
     * 
     * Class 'logo-wrapper' : logo container
     * Class 'navigation-menu-button' : menu toggle on mobile
     * Class 'navigation-menu' : menu
     * Class 'navigation-menu > li.nav-link' : menu items
     * Class 'navigation-menu > li.more' : dropdown menu items with caret
     * Class 'navigation-menu > li > ul.submenu' : dropdown submenu
     * Class 'navigation-tools' : tools (ex: search bar)
     *
     * @returns directive
     */
    function TalendNavbar (){
        return {
            restrict: 'EA',
            transclude: true,
            templateUrl: 'components/widgets/navbar/navbar.html',
            link: {
                post: function (scope, iElement) {
                    var menuToggle = iElement.find('.navigation-menu-button').unbind();
                    var menu = iElement.find('.navigation-menu');

                    menu.removeClass('show');
                    menuToggle.on('click', function(e) {
                        e.preventDefault();
                        $('.navigation-menu').slideToggle(function() {
                            if(menu.css('display') === 'none') {
                                menu.removeAttr('style');
                            }
                        });
                    });
                }
            }
        };
    }
    
    angular.module('talend.widget')
        .directive('talendNavbar', TalendNavbar);
})();