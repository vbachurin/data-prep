/* jshint ignore:start */
function DataViewMock() {
	var filter, filterArgs, idProperty;
	var data = [];

	this.setItems = function(items, idPropertyObj){
		data = items;
		idProperty = idPropertyObj;
	};

	this.beginUpdate = function () {
	};
	this.endUpdate = function () {
	};

	this.setFilterArgs = function (args) {
		filterArgs = args;
	};

	this.setFilter = function (args) {
		filter = args;
	};

	this.filter = function (data) {
		return filter(data, filterArgs);
	};

	this.getIdxById = function (tdpId) {
		var record = _.find(data, {tdpId: tdpId});
		return record ? data.indexOf(record) : null;
	};
	this.getItemById = function (tdpId) {
		return _.find(data, {tdpId: tdpId});
	};

	this.getItem = function(rowIndex){
		return data[rowIndex];
	};

	this.getLength = function(){
		return data.length;
	};

	this.insertItem = function () {
	};
	this.deleteItem = function () {
	};
	this.updateItem = function () {
	};

	this.onRowCountChanged = {
		subscribe: function () {}
	};
	this.onRowsChanged = {
		subscribe: function () {}
	};
}
/* jshint ignore:end */