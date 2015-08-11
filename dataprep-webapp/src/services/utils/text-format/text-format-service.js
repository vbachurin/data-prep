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
		 * @name computeHTMLForLeadingOrTrailingHiddenChars
		 * @methodOf data-prep.services.utils:TextFormatService
		 * @description split the string value into leading chars, text and trailing char and create html element using
		 * the class hiddenChars to specify the hiddenChars.If the text contains break lines, the class
		 * hiddenCharsBreakLine is used to notice it.
		 * @param {string} value The string value to adapt
		 */
		this.computeHTMLForLeadingOrTrailingHiddenChars = function computeHTMLForLeadingOrTrailingHiddenChars(value){
			if(!value) {
				return value;
			}

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
		};
	}

	angular.module('data-prep.services.utils')
		.service('TextFormatService', TextFormatService);
})();