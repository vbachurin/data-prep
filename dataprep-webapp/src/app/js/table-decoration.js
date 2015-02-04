function loadTableFeedbackStyles() {
				
		// MAKE THE TABLE COLUMN RESIZABLE
		$("#mytable").resizableColumns();
		
		// ON SEARCH FOCUS OUT 
		$("#search_inputfield").focusout(function() {
			// To be added: placeholder management
		});
		
		// TABLE HEADER & BODY ON HOVER 
		$('td').hover(function() {
		    var t = parseInt($(this).index()) + 1;
		    $('td:nth-child(' + t + ')').addClass('rollover');
		    $('th:nth-child(' + t + ')').addClass('th-rollover');
		},
		
		function() {
		    var t = parseInt($(this).index()) + 1;
		    $('td:nth-child(' + t + ')').removeClass('rollover');
		    $('th:nth-child(' + t + ')').removeClass('th-rollover');
		});
		
		// TABLE HEADER ON CLICK
		$('th').bind( "click", function(){

		    var selectedCol = parseInt($(this).index());	    
		    
		    parseTable(selectedCol, -1);
		    
		});	
	
		// TABLE BODY ON CLICK
		$('td').bind( "click", function(){

		    var selectedCol = parseInt($(this).index());
		    var selectedRow = parseInt($(this).closest('tr').index());
		    
		    parseTable(selectedCol, selectedRow);
		    
		});	
		
		function parseTable(selectedColIndex, selectedRowIndex) {

		    var $header = $("#mytable thead tr th");

			// Loop through grabbing everything
	        var $rows = $("#mytable tbody tr");
	        var totalRows = $rows.length;
	    
	        for (var rowIndex = 0; rowIndex < totalRows; ++rowIndex) {

	        	var currentRow = $($rows[rowIndex]);
	        	var $cells = currentRow.find("td");

	           	var totalCells = $cells.length;

	        	for (var cellIndex = 0; cellIndex <= totalCells; ++cellIndex) {
	        		var currentCell = $($cells[cellIndex]);
	        	    var currentHeader = $($header[cellIndex]);
	        		if (cellIndex != selectedColIndex) {
	        			currentHeader.addClass('th-unselected');
		        		currentCell.addClass('unselected');
	        			currentHeader.removeClass('th-selected');
		        		currentCell.removeClass('selected');       			
		        		currentCell.removeClass('cellselected');       			        				
		       		} else {
		    			currentHeader.addClass('th-selected');
		        		currentCell.addClass('selected');       			
		       			currentHeader.removeClass('th-unselected');
		    			currentCell.removeClass('unselected');
		        		currentCell.removeClass('cellselected');       			        				
		    			if (rowIndex == selectedRowIndex) {
		    	        		currentCell.addClass('cellselected');     
		    			}
		        	}		        		
	   	        }
	        }
		}
	}	
