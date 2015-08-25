(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:rangeSlider
     * @description This directive renders the rangeSlider.
     * @restrict E
     * @usage
     *     <range-slider
     *             id="domId"
     *             width="250"
     *             height="100"
     *             range-limits="statsDetailsCtrl.rangeLimits"
     *             on-brush-end="statsDetailsCtrl.onBrushEndFn"
     *         ></range-slider>
     * @param {string} id The element id
     * @param {number} width The width of the slider
     * @param {number} height The height of the slider
     * @param {object} rangeLimits {min: number, max: number} The limits of the slider
     * @param {function} onBrushEnd The callback on slider move
     * */

    function RangeSlider() {
        return {
            restrict: 'E',
            scope: {
                rangeLimits: '=',
                onBrushEnd: '&'
            },
            controller: 'RangeSliderCtrl',
            controllerAs: 'rangeSliderCtrl',
            link: function (scope, element, attrs, ctrl) {
                var renderTimeout;
                var container = attrs.id;

                var margin = {top: 25, right: 25, bottom: 80, left: 10};
                var width = attrs.width - margin.left - margin.right;
                var height = attrs.height - margin.top - margin.bottom;

                /**
                 * @ngdoc method
                 * @name renderRangerSlider
                 * @methodOf data-prep.rangeSlider.directive:RangeSlider
                 * @description Render the slider and attach the actions listeners
                 **/
                function renderRangerSlider() {
                    var rangeLimits = scope.rangeLimits;

                    var minimum = rangeLimits.min;
                    var maximum = rangeLimits.max;

                    var minBrush = typeof rangeLimits.minBrush !== 'undefined' ? rangeLimits.minBrush : minimum;
                    var maxBrush = typeof rangeLimits.maxBrush !== 'undefined' ? rangeLimits.maxBrush : maximum;

                    var nbDecimals = d3.max([ctrl.decimalPlaces(minBrush), ctrl.decimalPlaces(maxBrush)]);
                    var centerValue = (minBrush + maxBrush) / 2;

                    //--------------------------------------------------------------------------------------------------
                    //----------------------------------------------BASE------------------------------------------------
                    //--------------------------------------------------------------------------------------------------
                    var scale = d3.scale.linear()
                        .domain([minimum, maximum])
                        .range([0, width]);

                    var svg = d3.select('#' + container).append('svg')
                        .attr('width', width + margin.left + margin.right)
                        .attr('height', height + margin.top + margin.bottom)
                        .attr('class', 'range-slider-cls')
                        .append('g')
                        .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');

                    //--------------------------------------------------------------------------------------------------
                    //----------------------------------------------INPUTS----------------------------------------------
                    //--------------------------------------------------------------------------------------------------
                    svg.append('g').append('foreignObject')
                        .attr('width', width)
                        .attr('height', 40)
                        .attr('transform', 'translate(0,' + (height + 30) + ')')
                        .append('xhtml:div')
                        .html('<span><b>Min </b><input type="text" name="minRange"></span> <span style="float:right;"><b>Max </b> <input type="text" name="maxRange"/></span>');

                    //It will update Min and Max inputs with the correspondent brush extents
                    function fillInputs() {
                        hideMsgErr();
                        var s = scope.brush.extent();
                        document.getElementsByName('minRange')[0].value = s[0] > 1e4 || s[0] < -1e4 ? d3.format('e')(s[0].toFixed(nbDecimals)) : s[0].toFixed(nbDecimals);
                        document.getElementsByName('maxRange')[0].value = s[1] > 1e4 || s[1] < -1e4 ? d3.format('e')(s[1].toFixed(nbDecimals)) : s[1].toFixed(nbDecimals);
                        return s;
                    }

                    //stop event propagation
                    function stopPropagation(e) {
                        e.stopPropagation();
                    }

                    //create a key listener closure
                    var minCorrectness, maxCorrectness;
                    function handleKey(rangeType) {
                        return function (e) {
                            switch (e.keyCode) {
                                case 9:
                                case 13:
                                    if (minCorrectness !== null && maxCorrectness !== null) {
                                        adjustBrush();
                                    }
                                    else {
                                        fillInputs();
                                        hideMsgErr();
                                    }
                                    break;
                                case 27:
                                    fillInputs();
                                    break;
                                default:
                                    minCorrectness = rangeType === 'min' ? ctrl.toNumber(this.value) : minCorrectness;
                                    maxCorrectness = rangeType === 'max' ? ctrl.toNumber(this.value) : maxCorrectness;

                                    if (minCorrectness === null || maxCorrectness === null) {
                                        showMsgErr();
                                    }
                                    else {
                                        hideMsgErr();
                                    }
                            }
                        };
                    }

                    //attach events on inputs
                    function initRangeInputsListeners() {
                        var minRange = document.getElementsByName('minRange')[0];
                        var maxRange = document.getElementsByName('maxRange')[0];

                        minRange.onblur = adjustBrush;
                        minRange.onkeydown = stopPropagation;
                        minRange.onkeyup = handleKey('min');

                        maxRange.onblur = adjustBrush;
                        maxRange.onkeydown = stopPropagation;
                        maxRange.onkeyup = handleKey('max');
                    }

                    //--------------------------------------------------------------------------------------------------
                    //----------------------------------------------BRUSH-----------------------------------------------
                    //--------------------------------------------------------------------------------------------------
                    scope.brush = d3.svg.brush()
                        .x(scale)
                        .extent([centerValue, centerValue]);

                    var brushg = svg.append('g')
                        .attr('transform', 'translate(0,' + (height - 10) + ')')
                        .attr('class', 'brush')
                        .call(scope.brush);

                    brushg.call(scope.brush.event)
                        .transition().duration(400)
                        .call(scope.brush.extent([minBrush, maxBrush]));

                    brushg.selectAll('.resize')
                        .append('rect')
                        .attr('transform', function (d, i) {
                            return i ? 'translate(-10, 0)' : 'translate(0,0)';
                        })
                        .attr('width', 10);

                    brushg.selectAll('rect')
                        .attr('height', 20);

                    brushg.select('.extent')
                        .attr('height', 14)
                        .attr('transform', 'translate(0,3)');

                    //It will update the brush handlers position and propagate the filter
                    function adjustBrush() {
                        scope.oldRangeLimits = scope.brush.extent();
                        var enteredMin = ctrl.toNumber(document.getElementsByName('minRange')[0].value);
                        var enteredMax = ctrl.toNumber(document.getElementsByName('maxRange')[0].value);

                        if (enteredMax === null || enteredMin === null) {
                            return;
                        }

                        nbDecimals = d3.max([ctrl.decimalPlaces(enteredMin), ctrl.decimalPlaces(enteredMax)]);
                        var finalExtent = ctrl.adaptRangeValues(enteredMin, enteredMax, minimum, maximum, nbDecimals);

                        brushg.transition()
                            .call(scope.brush.extent(finalExtent));

                        fillInputs(); //should be after brush update

                        if(scope.oldRangeLimits[0] !== scope.brush.extent()[0] || scope.oldRangeLimits[1] !== scope.brush.extent()[1]) {
                            //trigger brush end callback
                            scope.onBrushEnd()(scope.brush.extent().map(function (n) {
                                return +n.toFixed(nbDecimals);
                            }));
                        }
                    }

                    //attach brush listeners
                    function initBrushListeners() {
                        scope.brush
                            //It will update the min and max inputs, and create a brush on a single value when the user clics on the slider without making a drag( the created brush will be empty )
                            .on('brush', function brushmove() {
                                var newExtent = fillInputs();
                                if (scope.brush.empty()) {
                                    var exp = '1e-' + (nbDecimals + 1);
                                    svg.select('.brush').call(scope.brush.clear().extent([newExtent[0], newExtent[1] + Number(exp)]));
                                }
                            })

                            //It will propagate the new filter limits to the rest of the app, it's triggered when the user finishes a brush
                            .on('brushend', function brushend() {
                                if(scope.oldRangeLimits[0] !== scope.brush.extent()[0] || scope.oldRangeLimits[1] !== scope.brush.extent()[1]){
                                    //trigger filter process in the datagrid
                                    scope.onBrushEnd()(scope.brush.extent().map(function (n) {
                                        return +n.toFixed(nbDecimals);
                                    }));
                                }
                            })

                            .on('brushstart', function brushstart (){
                                scope.oldRangeLimits = scope.brush.extent();
                            });
                    }

                    //--------------------------------------------------------------------------------------------------
                    //----------------------------------------------X AXIS----------------------------------------------
                    //--------------------------------------------------------------------------------------------------
                    svg.append('g')
                        .attr('class', 'x axis')
                        .attr('transform', 'translate(0,' + height + ')')
                        .call(d3.svg.axis().scale(scale).orient('bottom').ticks(Math.abs(scale.range()[1] - scale.range()[0]) / 50)
                            .tickFormat(function (d) {
                                if (d > 1e4 || d < -1e4) {
                                    return d3.format('e')(d);
                                }
                                else {
                                    return d3.format(',')(d);
                                }
                            })
                        )
                        .selectAll('text').attr('y', 13);

                    //--------------------------------------------------------------------------------------------------
                    //--------------------------------------------ERROR TEXT--------------------------------------------
                    //--------------------------------------------------------------------------------------------------
                    svg.append('g').append('text')
                        .attr('class', 'invalid-value-msg')
                        .attr('x', width / 2)
                        .attr('y', height + 75)
                        .attr('text-anchor', 'middle')
                        .attr('fill', 'red');

                    //shows the message Error with details on comma existence
                    function showMsgErr() {
                        var msgErr = 'Invalid Entered Value';
                        var minMaxStr = document.getElementsByName('minRange')[0].value + document.getElementsByName('maxRange')[0].value;
                        var finalMsgErr = ctrl.checkCommaExistence(minMaxStr) ? msgErr + ': Use "." instead of ","' : msgErr;
                        d3.select('text.invalid-value-msg').text(finalMsgErr);
                    }

                    //hides the message Error
                    function hideMsgErr() {
                        d3.select('text.invalid-value-msg').text('');
                    }

                    //--------------------------------------------------------------------------------------------------
                    //-----------------------------------------------INIT-----------------------------------------------
                    //--------------------------------------------------------------------------------------------------
                    fillInputs();
                    initRangeInputsListeners();
                    initBrushListeners();
                }

                scope.$watch('rangeLimits',
                    function (rangeLimits) {
                        element.empty();
                        if ((rangeLimits) && (rangeLimits.min !== rangeLimits.max)) {
                            clearTimeout(renderTimeout);
                            renderTimeout = setTimeout(renderRangerSlider, 100);
                        }
                    });
            }
        };
    }

    angular.module('talend.widget')
        .directive('rangeSlider', RangeSlider);
})();