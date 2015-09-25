describe('text to html adaptation service', function() {
	'use strict';

	beforeEach(module('data-prep.services.utils'));

	it('should return value when it is falsy', inject(function (TextFormatService) {
		//given
		var value = '';

		//when
		var result = TextFormatService.adaptValueToHtmlConstraints(value);

		//then
		expect(result).toBe(value);
	}));

	it('should add a span on leading spaces', inject(function (TextFormatService) {
		//given
		var value = '  my value';

		//when
		var result = TextFormatService.adaptValueToHtmlConstraints(value);

		//then
		expect(result).toBe('<span class="hiddenChars">  </span>my value');
	}));

	it('should add a span on leading spaces and convert < and > into their html codes', inject(function (TextFormatService) {
		//given
		var value = '  <b>my value</b>';

		//when
		var result = TextFormatService.adaptValueToHtmlConstraints(value);

		//then
		expect(result).toBe('<span class="hiddenChars">  </span>&lt;b&gt;my value&lt;/b&gt;');
	}));

	it('should add a span on trailing spaces', inject(function (TextFormatService) {
		//given
		var value = 'my value  ';

		//when
		var result = TextFormatService.adaptValueToHtmlConstraints(value);

		//then
		expect(result).toBe('my value<span class="hiddenChars">  </span>');
	}));

	it('should add a span on leading and trailing spaces', inject(function (TextFormatService) {
		//given
		var value = '     my value  ';

		//when
		var result = TextFormatService.adaptValueToHtmlConstraints(value);

		//then
		expect(result).toBe('<span class="hiddenChars">     </span>my value<span class="hiddenChars">  </span>');
	}));

	it('should add a line breaking arrow at the end of each line', inject(function (TextFormatService) {
		//given
		var value = 'my \nnew\nvalue';

		//when
		var result = TextFormatService.adaptValueToHtmlConstraints(value);

		//then
		expect(result).toBe('my ↵\nnew↵\nvalue');
	}));

	it('should adapt input with line breaking arrow and leading/trailing spaces spans', inject(function (TextFormatService) {
		//given
		var value = '     my \nnew\nvalue  ';

		//when
		var result = TextFormatService.adaptValueToHtmlConstraints(value);

		//then
		expect(result).toBe('<span class="hiddenChars">     </span>my ↵\nnew↵\nvalue<span class="hiddenChars">  </span>');
	}));
});