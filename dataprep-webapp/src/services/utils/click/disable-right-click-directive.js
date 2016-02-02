export default function DisableRightClick() {
    return {
        restrict: 'A',
        link: function (scope, iElement) {
            iElement.bind('contextmenu', function (e) {
                e.preventDefault();
            });
        }
    };
}