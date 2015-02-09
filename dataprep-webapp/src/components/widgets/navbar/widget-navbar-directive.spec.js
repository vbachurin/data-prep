'use strict';

describe('Navbar directive', function() {
    var scope, element;

    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        
        element = angular.element('<talend-navbar>' + 
        '   <a href="javascript:void(0)" class="navigation-menu-button">MENU</a>' +
        '   <nav role="navigation">' +
        '       <ul class="navigation-menu show">' +
        '           <li class="nav-link"><a href="javascript:void(0)" class="icon t-top-bar_notification"></a></li>' +
        '           <li class="nav-link more"><a href="javascript:void(0)" class="icon t-top-bar_profile nomore"></a>' +
        '               <ul class="submenu">' +
        '                   <li><a href="javascript:void(0)">Logout</a></li>' +
        '                   <li class="divider"></li>' +
        '                   <li><a href="javascript:void(0)">Edit profil</a></li>' +
        '               </ul>' +
        '           </li>' +
        '       </ul>' +
        '   </nav>' +
        '</talend-navbar>');
        $compile(element)(scope);
        scope.$digest();
    }));
    
    it('should remove navigation-menu \'show\' class', function() {
        //then
        expect(element.find('.navigation-menu').hasClass()).toBe(false);
    });
});