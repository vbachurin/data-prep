(function () {
	'use strict';

	/**
	 * @ngdoc controller
	 * @name data-prep.folder.controller:FolderCtrl
	 * @description Export controller.
	 * @requires data-prep.services.folder.service:FolderService
	 * @requires data-prep.services.state.constant:state
	 */
	function FolderCtrl (FolderService, state) {
		var vm = this;
		vm.state = state;

		vm.loadingChildren = true;
		vm.state = state;

		/**
		 * @ngdoc method
		 * @name goToFolder
		 * @methodOf data-prep.folder.controller:FolderCtrl
		 * @param {object} folder - the folder to go
		 */
		vm.goToFolder = function goToFolder (folder) {
			FolderService.getFolderContent(folder);
		};

		/**
		 * @ngdoc method
		 * @name initMenuChildren
		 * @methodOf data-prep.folder.controller:FolderCtrl
		 * @param {object} folder - the folder
		 * @description build the children of the folder menu entry as parameter
		 */
		vm.initMenuChildren = function initMenuChildren (folder) {
			vm.loadingChildren = true;
			FolderService.populateMenuChildren(folder)
				.finally(
				function () {
					vm.loadingChildren = false;
				});
		};

		/**
		 * Load folders on start
		 */
		FolderService.getFolderContent();

	}

	angular.module('data-prep.folder')
		.controller('FolderCtrl', FolderCtrl);
})();