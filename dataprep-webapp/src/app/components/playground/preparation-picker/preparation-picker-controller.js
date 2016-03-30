/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

class PreparationPickerCtrl {
    constructor($rootScope, $timeout, $state, DatasetService, StateService, state, PreparationService, PreparationListService, RecipeService) {
        'ngInject';

        this.$rootScope = $rootScope;
        this.$timeout = $timeout;
        this.$state = $state;
        this.datasetService = DatasetService;
        this.stateService = StateService;
        this.state = state;
        this.preparationService = PreparationService;
        this.preparationListService = PreparationListService;
        this.isFetchingPreparations = true;
        this.recipeService = RecipeService;
    }

    /**
     * @ngdoc method
     * @name $onInit
     * @methodOf data-prep.preparation-picker.controller:PreparationPickerCtrl
     * @description initializes preparation picker form
     **/
    $onInit() {
        this.datasetService.getCompatiblePreparations(this.datasetId)
            .then((compatiblePreparations) => {
                if (compatiblePreparations.length) {
                    if (this.state.playground.preparation) {
                        compatiblePreparations = _.reject(compatiblePreparations, {id: this.state.playground.preparation.id});
                    }

                    const candidatePreparations = _.map(compatiblePreparations, (candidatePrepa) => {
                        return {
                            preparation: candidatePrepa,
                            dataset: _.find(this.state.inventory.datasets, {id: candidatePrepa.dataSetId})
                        };
                    });

                    this.stateService.setCandidatePreparations(candidatePreparations);
                }
                else {
                    this.stateService.setCandidatePreparations([]);
                }
            })
            .finally(() => {
                this.isFetchingPreparations = false;
            });
    }

    /**
     * @ngdoc method
     * @name selectPreparation
     * @methodOf data-prep.preparation-picker.controller:PreparationPickerCtrl
     * @description selects the preparation to apply
     **/
    selectPreparation(selectedPrepa) {
        this.selectedPreparation = selectedPrepa;
        this.$rootScope.$emit('talend.loading.start');
        if (this.state.playground.preparation) {
            this.preparationService.delete(this.state.playground.preparation)
                .then(() => {this._applyPreparation();});
        }
        else {
            this._applyPreparation();
        }
    }

    /**
     * @ngdoc method
     * @name _applyPreparation
     * @methodOf data-prep.preparation-picker.controller:PreparationPickerCtrl
     * @description applies the selected preparation to the dataset
     **/
    _applyPreparation() {
        this.preparationService.clone(this.selectedPreparation.preparation.id)
            .then((preparationCloneId) => this.preparationService.update(preparationCloneId, {
                dataSetId: this.datasetId,
                name: this.state.playground.preparation ? this.state.playground.preparation.name : this.datasetName
            }))
            .then((updatedPreparationId) => {//TODO backend should not return a new preparation Id after an update
                return this.preparationListService.refreshPreparations()
                    .then(() => updatedPreparationId);
            })
            .then((updatedPreparationId) => {
                this.stateService.updatePreparationPickerDisplay(false);
                this.stateService.resetPlayground();//in order to remove the chart container ng-if="...vertical"
                this.$state.go('playground.preparation', {prepid: updatedPreparationId}, {reload: true});
            })
            .finally(() => {
                this.$rootScope.$emit('talend.loading.stop');

                //this.stateService.setCandidatePreparations([]);
            });
    }
}

export default PreparationPickerCtrl