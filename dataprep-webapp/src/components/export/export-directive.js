(function () {
    'use strict';

    function Export($window, MessageService) {
        return {
            templateUrl: 'components/export/export.html',
            restrict: 'E',
            bindToController: true,
            controllerAs: 'exportCtrl',
            controller: 'ExportCtrl',
            link: function (scope, iElement, iAttrs, ctrl) {
                var link = iElement.find('.export_link').eq(0)[0];

                var createBlob = function (csv) {
                    if (typeof(Blob) === typeof(Function)) {
                        return new Blob([csv.content], {
                            type: 'data:text/csv;charset=' + csv.charset + ';'
                        });
                    }
                    else {
                        var BlobBuilder = $window.BlobBuilder || $window.WebKitBlobBuilder || $window.MozBlobBuilder || $window.MSBlobBuilder;
                        var builder = new BlobBuilder();
                        builder.append([csv.content]);
                        builder.append({
                            type: 'data:text/csv;charset=' + csv.charset + ';'
                        });
                        return builder.getBlob();
                    }
                };

                ctrl.initExportLink = function (csv) {
                    var blob = createBlob(csv);

                    // Feature detection : IE
                    if ($window.navigator.msSaveBlob) {
                        $window.navigator.msSaveBlob(blob, csv.name);
                    }
                    else {
                        if (link.download === undefined) {
                            // it needs to implement server side export
                            MessageService.warning('Your browser does not support local download naming. Please change the file name after download to ' + csv.name);
                        }
                        var url = $window.URL || $window.webkitURL || $window.mozURL;
                        link.setAttribute('href', url.createObjectURL(blob));
                        link.setAttribute('download', csv.name);
                    }

                    return link;
                };
            }
        };
    }

    angular.module('data-prep.export')
        .directive('export', Export);
})();