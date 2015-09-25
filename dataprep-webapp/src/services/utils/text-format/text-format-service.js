(function() {
	'use strict';

	/**
	 * @ngdoc service
	 * @name data-prep.services.utils.service:TextFormatService
	 * @description text formatting function to show the spaces, newlines, long strings,
	 */
	function TextFormatService() {

		/**
		 * @ngdoc method
		 * @name adaptValueToHtmlConstraints
		 * @methodOf data-prep.services.utils:TextFormatService
		 * @description converts the value in a way to show the heading or trailing spaces, and to escape the html code
		 * @param {string} type - value The string value to adapt
		 */
		this.adaptValueToHtmlConstraints = function adaptValueToHtmlConstraints(value) {
			if (!value) {
				return value;
			}

			return computeHTMLForLeadingOrTrailingHiddenChars(escapeHtmlTags(value));
		};

			/**
		 * @ngdoc method
		 * @name computeHTMLForLeadingOrTrailingHiddenChars
		 * @methodOf data-prep.services.utils:TextFormatService
		 * @description split the string value into leading chars, text and trailing char and create html element using
		 * the class hiddenChars to specify the hiddenChars.If the text contains break lines, the class
		 * hiddenCharsBreakLine is used to notice it.
		 * @param {string} type - value The string value to adapt
		 */
		function computeHTMLForLeadingOrTrailingHiddenChars(value){
			var returnStr = '';
			var hiddenCharsRegExpMatch = value.match(/(^\s*)?([\s\S]*?)(\s*$)/);

			//leading hidden chars found
			if (hiddenCharsRegExpMatch[1]){
				returnStr = '<span class="hiddenChars">' + hiddenCharsRegExpMatch[1] + '</span>';
			}

			//breaking lines indicator
			var lines = value.trim().split('\n');
			if(lines.length < 2) {
				returnStr += hiddenCharsRegExpMatch[2] ;
			}
			else {
				_.forEach(lines, function(line, index) {
					returnStr += line + (index === lines.length -1 ? '' : '↵\n');
				});
			}

			//trailing hidden chars
			if (hiddenCharsRegExpMatch[3]){
				returnStr += '<span class="hiddenChars">' + hiddenCharsRegExpMatch[3] + '</span>';
			}
			return returnStr;
		}

		/**
		 * @ngdoc method
		 * @name escapeHtmlTags
		 * @methodOf data-prep.services.utils:TextFormatService
		 * @description replace the special caracters < and the > with their html code in order to disable html interpretation
		 * @param {string} type - value The string value to replace
		 */
		function escapeHtmlTags(value) {
			return (value + '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
		}
	}

	angular.module('data-prep.services.utils')
		.service('TextFormatService', TextFormatService);
})();