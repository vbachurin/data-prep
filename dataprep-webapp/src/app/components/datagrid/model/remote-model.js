/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

export default class RemoteModel {
    constructor($q, length) {
        this.$q = $q;
        this.requestCanceler = null;

        this.data = {length: length};
        this.filters = [];


        this.onDataLoading = new Slick.Event();
        this.onDataLoadingError = new Slick.Event();
        this.onDataLoaded = new Slick.Event();
    }

    /**
     * Set the function to fetch records. (from, to, cancelerPromise) => promise<records[]>
     * @param {function} fetchRecords The fetch records function.
     */
    setFetchRecords(fetchRecords) {
        this.fetchRecords = fetchRecords;
    }

    /**
     * Empty the data property
     */
    clear() {
        for (let key in this.data) {
            delete this.data[key];
        }
        this.data.length = 0;
    }

    /**
     * Empty the data between the from/to indexes
     * @param {number} from The start index
     * @param {number} to The start index
     */
    clearPart(from, to) {
        for (let i = from; i <= to; i++) {
            delete this.data[i];
        }
    }

    /**
     * Check if the data is fully loaded between the 2 indexes
     * @param {number} from The start index
     * @param {number} to the end index
     * @returns {boolean} True if all the records between the indexes are loaded
     */
    isDataLoaded(from, to) {
        for (let i = from; i <= to; i++) {
            if (!this.data[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the data between from/to index if needed
     * @param {number} from The start index
     * @param {number} to The end index
     */
    ensureData(from, to) {
        if(!this.fetchRecords) {
            throw new Error('No fetch records function has been set. Perhaps you just forget it. \nRemoteModel.setFetchRecords()');
        }

        //if pending request, cancel it
        if(this.requestCanceler) {
            this.requestCanceler.resolve('user cancel');
            this.requestCanceler = null;
        }

        let startIndex = from < 0 ? 0 : from;
        let endIndex = this.data.length > 0 ? Math.min(to, this.data.length - 1) : to;

        //exclude data already loaded at the beginning and the ending of the range
        while(startIndex < endIndex && this.data[startIndex]) {
            startIndex++;
        }
        while(startIndex < endIndex && this.data[endIndex]) {
            endIndex --;
        }

        //nothing to fetch
        if(startIndex === endIndex && this.data[startIndex]) {
            this.onDataLoaded.notify({from: from, to: to});
            return;
        }

        //fetch records
        this.onDataLoading.notify({from: from, to: to});
        clearTimeout(this.fetchTimeout);
        this.fetchTimeout = setTimeout(() => {
            this.requestCanceler = this.$q.defer();
            this.fetchRecords(startIndex, endIndex, this.requestCanceler.promise)
                .then((records) => this.onRecordsReceived(startIndex, endIndex, records))
                .catch((error) => this.onRecordsError(startIndex, endIndex, error))
                .finally(() => this.requestCanceler = null);
        }, 100);
    }

    /**
     * Update records in data structure
     * @param {number} from Start index
     * @param {number} to End index
     * @param {Array} records Records to insert
     */
    onRecordsReceived(from, to, records) {
        for (let i = 0; i < records.length; i++) {
            this.data[from + i] = records[i];
        }
        this.onDataLoaded.notify({from: from, to: to});
    }

    /**
     * Notify error on records fetch
     * @param {number} from Start index
     * @param {number} to end index
     * @param {object} error The error
     */
    onRecordsError(from, to, error) {
        this.onDataLoadingError.notify({from: from, to: to, error: error});
    }

    /**
     * Reload the data between from/to indexes
     * @param {number} from Start index
     * @param {number} to End index
     */
    reloadData(from, to) {
        this.clearPart(from, to);
        this.ensureData(from, to);
    }
}