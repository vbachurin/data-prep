(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.widget.directive:NavigationList
     * @description This directive display a navigable list of elements
     * @restrict E
     * @usage
     * <navigation-list
            list="list"
            on-click="callbackOn(item)"
            selected-item="selectedItem"
            get-label="callback(item)"
            on-add-item="true">
       </navigation-list>
     * @param {Array} list the collection to navigate into
     * @param {object} selectedItem selected Item from the given list
     * @param {function} onClick The callback executed on an item clicked
     * @param {function} getLabel The function that returns the label to show
     * @param {function} onAddItem The function is called when the add button is clicked
     */
    function NavigationList($timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/widgets/navigation-list/navigation-list.html',
            scope: {
                list: '=',
                selectedItem: '=',
                onClick: '&',
                getLabel: '&',
                onAddItem: '&'
            },
            bindToController: true,
            controllerAs: 'navigationListCtrl',
            controller: function () {},

            link: function (scope, iElement, iAttrs, ctrl) {

                if(iAttrs.onAddItem) {
                    ctrl.showAddButton = true;
                } else {
                    ctrl.showAddButton = false;
                }

                $timeout(function () {

                    var wrapper = iElement.find('.items-list-wrapper').eq(0);
                    var leftButton = iElement.find('.arrow-left').eq(0);
                    var rightButton = iElement.find('.arrow-right').eq(0);
                    var itemsList = iElement.find('.items-list').eq(0);
                    itemsList.css('left', 0);
                    itemsList.css('float', 'left');
                    var posLeft = 0;

                    function initLeftPosition () {
                        if(itemsList.css('float') === 'right') {
                            posLeft = itemsList.position().left;
                            if(posLeft > 0){
                                itemsList.css('float', 'left');
                                itemsList.css('left', 0);
                                return;
                            }
                        } else {
                            posLeft = parseInt(itemsList.css('left'), 10);
                        }
                    }

                    leftButton.on('click', function () {
                        initLeftPosition();
                        if (posLeft < 0) {

                            itemsList.css('left', posLeft + 200);
                            itemsList.css('float', 'left');
                            posLeft = parseInt(itemsList.css('left'), 10);

                            if (posLeft >= 0) {
                                itemsList.css('left', 0);
                            }
                        }
                    });

                    rightButton.on('click', function () {
                        initLeftPosition();
                        if((posLeft + ctrl.list.length * 200) >= wrapper.width()) {
                            itemsList.css('left', posLeft - 200);
                            posLeft = parseInt(itemsList.css('left'), 10);

                            if ((posLeft + ctrl.list.length * 200) <= wrapper.width()) {
                                itemsList.css('float', 'right');
                                itemsList.css('left', '');
                            }
                        }
                    });

                    scope.$watch(function () {
                            return ctrl.list;
                        }, function () {
                            if(ctrl.list && ctrl.list.length > 0) {
                                itemsList.css('width', ctrl.list.length * 200);
                            }
                        }
                    );

                    scope.$watch(function () {
                            return ctrl.selectedItem;
                        }, function () {
                            if(ctrl.list && ctrl.list.length > 0 && ctrl.selectedItem){
                                for(var i= 0; i< ctrl.list.length; i++) {
                                    if(ctrl.list[i] === ctrl.selectedItem){
                                        initLeftPosition();
                                        if((posLeft + (i+1)*200) > wrapper.width() || (posLeft + (i+1)*200) < 0) {
                                            if((i+1)*200 < wrapper.width()){
                                                itemsList.css('float', 'left');
                                                itemsList.css('left', 0);
                                            } else if(i === (ctrl.list.length -1)){
                                                itemsList.css('float', 'right');
                                                itemsList.css('left', '');
                                            } else {
                                                itemsList.css('float', 'left');
                                                itemsList.css('left', wrapper.width() - (i+1)*200);
                                            }
                                        }
                                        break;
                                    }
                                }
                            } else {
                                itemsList.css('float', 'left');
                                itemsList.css('left', 0);
                            }

                        }
                    );

                    $(window).on('resize', function(){
                        itemsList.css('left', 0);
                        itemsList.css('float', 'left');
                    });
                }, 500);
            }
        };
    }

    angular.module('talend.widget')
        .directive('navigationList', NavigationList);
})();