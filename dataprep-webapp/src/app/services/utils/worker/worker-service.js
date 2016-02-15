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
 * @name data-prep.services.utils.service:WorkerService
 * @description This service convert a function into a web worker
 */
export default function WorkerService($q) {
    'ngInject';

    return {
        create: create
    };

    /**
     * @ngdoc method
     * @name create
     * @methodOf data-prep.services.utils.service:WorkerService
     * @param {object} parameters Worker call parameters
     * @param {object} options ParallelJS options
     * @description Create a wrapper on a worker that execute the provided function
     * @returns {object} The wrapper on a created web worker
     */
    function create(parameters, options) {
        var operation = new Parallel(parameters, options);
        var defer = $q.defer();
        var workerOperator =  {operation: operation, defer: defer};

        workerOperator.importScripts = (scriptUrl) => {
            operation.require(scriptUrl);
            return workerOperator;
        };
        workerOperator.require = (script) => {
            operation.require(script);
            return workerOperator;
        };
        workerOperator.run = (mainFn) => {
            operation.spawn(mainFn).then(defer.resolve, defer.reject);
            return defer.promise;
        };
        workerOperator.cancel = () => {
            defer.reject('user cancel')
        };

        return workerOperator;
    }
}