/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

(function() {
    'use strict';
    function TalendFileSelector() {
        return {
            restrict: 'E',
            templateUrl: 'components/widgets/file-selector/file-selector.html',
            scope: {
                buttonDataIcon: '@',
                buttonTitle: '@',
                fileModel: '=',
                onFileChange: '&'
            },
            bindToController: true,
            controllerAs: 'talendFileSelectorCtrl',
            controller: function() {},
            link: function(scope, element) {
                element.find('span').bind('click', function() {
                    element.find('input').click();
                });
            }
        };
    }
    angular.module('talend.widget')
        .directive('talendFileSelector', TalendFileSelector);
})();