/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

/**
 * @ngdoc service
 * @name data-prep.services.export.service:ExportService
 * @description Export service. This service provide the entry point to the backend export REST api.
 * @requires data-prep.services.export.service:ExportRestService
 * @requires data-prep.services.parameters.service:ParametersService
 */
export default class ExportService {
    constructor(state, StateService, ExportRestService, ParametersService) {
        'ngInject';

        this.StateService = StateService;
        this.ExportRestService = ExportRestService;
        this.ParametersService = ParametersService;
        this.state = state;
    }

    /**
     * @ngdoc method
     * @name reset
     * @methodOf data-prep.services.export.service:ExportService
     * @description Reset the export types parameters
     */
    reset() {
        _.forEach(this.state.export.exportTypes, (type) => {
            this.ParametersService.resetParamValue(type.parameters);
        });
    }

    /**
     * @ngdoc method
     * @name getType
     * @methodOf data-prep.services.export.service:ExportService
     * @param {String} id the export type id
     * @description Get the type by id
     */
    getType(id) {
        return _.find(this.state.export.exportTypes, { id });
    }

    /**
     * @ngdoc method
     * @name saveDefaultExport
     * @methodOf data-prep.services.export.service:ExportService
     * @description Save the default export in state
     */
    _saveDefaultExport() {
        const exportType = _.find(this.state.export.exportTypes, { defaultExport: 'true' }) || this.state.export.exportTypes[0];
        this.StateService.setDefaultExportType({ exportType: exportType.id });
    }

    /**
     * @ngdoc method
     * @name refreshTypes
     * @methodOf data-prep.services.export.service:ExportService
     * @description Refresh the export types list and save default if no parameters has been saved yet
     */
    refreshTypes() {
        return this.ExportRestService.exportTypes()
            .then((exportTypes) => {
                this.StateService.setExportTypes(exportTypes);
                this._saveDefaultExport();
            });
    }
}
