(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.utils.service:TextFormatService
     * @description Text formatting service. It helps to adapt values in lookup-datagrid (show spaces, newlines, etc)
     */
    function TextFormatService() {
        return {
            adaptToGridConstraints: adaptToGridConstraints,
            escapeRegex: escapeRegex,
            convertPatternToRegexp: convertPatternToRegexp,
            escapeRegExpExceptStar: escapeRegExpExceptStar,
            convertJavaDateFormatToMomentDateFormat: convertJavaDateFormatToMomentDateFormat
        };

        //--------------------------------------------------------------------------------------------------------------
        //-----------------------------------------------------Regex----------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name escapeRegex
         * @methodOf data-prep.services.utils:TextFormatService
         * @description Escape regex chars
         * @param {string} value The value to adapt
         * @returns {string} The value with regex chars escaped
         */
        function escapeRegex(value) {
            return value.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '[$&]');
        }

        //--------------------------------------------------------------------------------------------------------------
        //-----------------------------------------------------GRID-----------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name adaptToGridConstraints
         * @methodOf data-prep.services.utils:TextFormatService
         * @description Adapt value to the lookup-datagrid display constraint (show spaces, escape, etc)
         * @param {string} value The value to adapt
         */
        function adaptToGridConstraints(value) {
            if (!value) {
                return value;
            }

            var constraints = [
                escapeHtmlTags,
                addLineBreaks,
                addTrailingAndLeadingSpacesDivs
            ];

            _.forEach(constraints, function (constraintFn) {
                value = constraintFn(value);
            });

            return value;
        }

        //--------------------------------------------------------------------------------------------------------------
        //-----------------------------------------------GRID CONSTRAINTS-----------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name addLineBreaks
         * @methodOf data-prep.services.utils:TextFormatService
         * @description Add line break char
         * @param {string} value The value to adapt
         */
        function addLineBreaks(value) {
            return value.replace(new RegExp('\n', 'g'), '↵\n');
        }

        /**
         * @ngdoc method
         * @name addTrailingAndLeadingSpacesDivs
         * @methodOf data-prep.services.utils:TextFormatService
         * @description Add divs with "hiddenChars" class to show the leading and trailing spaces
         * @param {string} value The value to adapt
         */
        function addTrailingAndLeadingSpacesDivs(value) {
            var returnStr = '';
            var hiddenCharsRegExpMatch = value.match(/(^\s*)?([\s\S]*?)(\s*$)/);

            //leading hidden chars found
            if (hiddenCharsRegExpMatch[1]) {
                returnStr = '<span class="hiddenChars">' + hiddenCharsRegExpMatch[1] + '</span>';
            }

            //trimmed value
            returnStr += hiddenCharsRegExpMatch[2];

            //trailing hidden chars
            if (hiddenCharsRegExpMatch[3]) {
                returnStr += '<span class="hiddenChars">' + hiddenCharsRegExpMatch[3] + '</span>';
            }
            return returnStr;
        }

        /**
         * @ngdoc method
         * @name escapeHtmlTags
         * @methodOf data-prep.services.utils:TextFormatService
         * @description Escape the tags
         * @param {string} value The string value to replace
         */
        function escapeHtmlTags(value) {
            return (value + '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        }


        /**
         * @ngdoc method
         * @name convertPatternToRegexp
         * @methodOf data-prep.services.utils:TextFormatService
         * @description Convert pattern to regex
         * @param {string} value The pattern
         */
        function convertPatternToRegexp(pattern) {
            var regexp = '';
            for (var i = 0, len = pattern.length; i < len; i++) {
                switch(pattern[i]){
                    case 'A':
                        regexp = regexp + '[A-Z]';
                        break;
                    case'a':
                        regexp = regexp + '[a-z]';
                        break;
                    case'9':
                        regexp = regexp + '[0-9]';
                        break;
                    default:
                        regexp = regexp + escapeRegExpExceptStar(pattern[i]);
                }
            }
            return '^' + regexp + '$';
        }

        /**
         * @ngdoc method
         * @name escapeRegExpExceptStar
         * @methodOf data-prep.services.utils:TextFormatService
         * @description Escape all regexp characters except * wildcard, and adapt * wildcard to regexp (* --> .*)
         * @param {string} str The string to escape
         */
        function escapeRegExpExceptStar(str) {
            return str.replace(/[\-\[\]\/\{\}\(\)\+\?\.\\\^\$\|]/g, '\\$&').replace(/\*/g, '.*');
        }

        /**
         * @ngdoc method
         * @name convertJavaDateFormatToMomentDateFormat
         * @methodOf data-prep.services.utils:TextFormatService
         * @description convert Java Date Format To Moment Date Format
         * @param {string} str The Java Date Format
         */
        function convertJavaDateFormatToMomentDateFormat(javaDateFormat) {
            var end = false;
            var pattern = javaDateFormat;
            pattern = pattern.replace(/\'/g, function() { //quote problems => replace quotes by brackets (ex : 'T' => [T], ''y => [']y, 'o''clock' => [o'clock])
                return (end = !end) ? '[' : ']';
            }).replace(/\[\]/g, '[\']').replace(/\]\[/g, '\'');

            //Convert java date format to moment.js date format by escaping contents in brackets
            var stringsNotToBeReplaced = pattern.match(/\[.*?\]/g);
            var i = 0;
            pattern = moment().toMomentFormatString(pattern);
            pattern = pattern.replace(/\[.*?\]/g, function() {
                return stringsNotToBeReplaced[i++];
            });

            return pattern;
        }
    }

    angular.module('data-prep.services.utils')
        .service('TextFormatService', TextFormatService);
})();