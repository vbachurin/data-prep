/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

class PreparationPickerCtrl {
    constructor($timeout, $state, DatasetService, StateService, state, PreparationService, PreparationListService) {
        'ngInject';

        this.$timeout = $timeout;
        this.$state = $state;
        this.datasetService = DatasetService;
        this.stateService = StateService;
        this.state = state;
        this.preparationService = PreparationService;
        this.isApplyingPreparation = false;
        this.preparationListService = PreparationListService;
    }

    $onInit() {
        this.datasetService.getCompatiblePreparations(this.dataset.id)
            .then((compatiblePreparations) => {
                if (compatiblePreparations.length) {

                    const candidatePreparations = _.map(compatiblePreparations, (candidatePrepa) => {
                        return {
                            preparation: candidatePrepa,
                            selected: false,
                            dataset: _.find(this.state.inventory.datasets, {id: candidatePrepa.dataSetId})
                        };
                    });

                    candidatePreparations[0].selected = true;
                    this.selectedPreparation = candidatePreparations[0];
                    this.stateService.setCandidatePreparations(candidatePreparations);
                }
            });
    }

    selectPreparation(selectedPrepa) {
        let previousPrepIndex = _.findIndex(this.state.playground.candidatePreparations, {selected: true});
        this.state.playground.candidatePreparations[previousPrepIndex].selected = false;
        selectedPrepa.selected = true;
        this.selectedPreparation = selectedPrepa;
    }

    applyPreparation() {
        this.isApplyingPreparation = true;
        this.preparationService.clone(this.selectedPreparation.preparation.id)
            .then((preparationCloneId) => {
                this.preparationService.update(preparationCloneId, {dataSetId: this.dataset.id, name: this.dataset.name})
                    .then((updatedPreparationId) => {//TODO backend should not return a new preparation Id after an update
                        this.preparationListService.refreshPreparations()
                        .then(() => {
                            this.isApplyingPreparation = false;
                            this.stateService.updatePreparationPickerDisplay(false);
                            this.$state.go('playground.preparation', {prepid: updatedPreparationId}, {reload: true});
                        });
                    }, () => {
                        this.isApplyingPreparation = false;
                    })
            }, () => {
                this.isApplyingPreparation = false;
            });
    }
}

export default PreparationPickerCtrl