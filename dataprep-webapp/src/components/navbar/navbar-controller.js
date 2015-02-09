(function() {
    'use strict';
    
    function NavbarCtrl() {
        var menuToggle = $('#js-mobile-menu').unbind();
        $('#js-navigation-menu').removeClass('show');

        menuToggle.on('click', function(e) {
            console.log('click');
            e.preventDefault();
            $('#js-navigation-menu').slideToggle(function(){
                if($('#js-navigation-menu').is(':hidden')) {
                    $('#js-navigation-menu').removeAttr('style');
                }
            });
        });
    }
    
    angular.module('data-prep-navbar')
        .controller('NavbarCtrl', NavbarCtrl);
})();