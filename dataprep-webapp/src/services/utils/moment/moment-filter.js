/**
 * @ngdoc service
 * @name data-prep.services.utils.filter:MessageService
 * @description Display message toasts
 */
export default function TDPMoment() {
    return function (dateString, format) {
        return moment(dateString, format ? format : 'x').fromNow();
    };
}