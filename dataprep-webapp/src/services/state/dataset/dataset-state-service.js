export const datasetState = {
    uploadingDatasets: []
};

export function DatasetStateService() {

    return {
        //uploading datasets
        startUploadingDataset: startUploadingDataset,
        finishUploadingDataset: finishUploadingDataset
    };

    //--------------------------------------------------------------------------------------------------------------
    //----------------------------------------------UPLOADING DATASETS----------------------------------------------
    //--------------------------------------------------------------------------------------------------------------
    function startUploadingDataset(dataset) {
        datasetState.uploadingDatasets.push(dataset);
    }

    function finishUploadingDataset(dataset) {
        datasetState.uploadingDatasets.splice(datasetState.uploadingDatasets.indexOf(dataset), 1);
    }
}