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
     *             width="300"
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
                var filterToApply;

                var lastBrushValues;
                //the left and right margins MUST be the same as the verticalBarchart ones
                var margin = {top: 5, right: 20, bottom: 10, left: 15};
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
                    var minBrush = typeof rangeLimits.minBrush !== 'undefined' ? rangeLimits.minBrush : rangeLimits.min;
                    var maxBrush = typeof rangeLimits.maxBrush !== 'undefined' ? rangeLimits.maxBrush : rangeLimits.max;
                    var nbDecimals = d3.max([ctrl.decimalPlaces(minBrush), ctrl.decimalPlaces(maxBrush)]);
                    lastBrushValues = {
                        min: minBrush,
                        max: maxBrush
                    };

                    var centerValue = (minBrush + maxBrush) / 2;

                    //--------------------------------------------------------------------------------------------------
                    //----------------------------------------------BASE------------------------------------------------
                    //--------------------------------------------------------------------------------------------------
                    var scale = d3.scale.linear()
                        .domain([rangeLimits.min, rangeLimits.max])
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
                        .attr('transform', 'translate(0,' + (height - 45) + ')')
                        .append('xhtml:div')
                        .html('<span><b>Min </b><input type="text" name="minRange"></span> <span style="float:right;"><b>Max </b> <input type="text" name="maxRange"/></span>');

                    //It will update Min and Max inputs with the correspondent brush extents
                    function fillInputs() {
                        hideMsgErr();
                        var s = scope.brush.extent();
                        document.getElementsByName('minRange')[0].value = s[0].toFixed(nbDecimals);
                        document.getElementsByName('maxRange')[0].value = s[1].toFixed(nbDecimals);
                        return s;
                    }

                    //It will update Min and Max inputs with the correspondent entered intervals
                    //executed at column selection
                    function fillWithManuallyEnteredValues() {
                        //there is an existent filter
                        if (scope.rangeLimits.minFilterVal !== undefined && scope.rangeLimits.maxFilterVal !== undefined) {
                            var m = [
                                scope.rangeLimits.minFilterVal,
                                scope.rangeLimits.maxFilterVal
                            ];
                            document.getElementsByName('minRange')[0].value = m[0].toFixed(nbDecimals);
                            document.getElementsByName('maxRange')[0].value = m[1].toFixed(nbDecimals);
                        }
                        else {
                            //initialize the filterToApply, it will be updated when the user types values,
                            // and will be used to propagate the right filter limits
                            filterToApply = scope.brush.extent();
                            fillInputs();
                        }
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
                        .attr('transform', 'translate(0,' + (margin.top + 10) + ')')
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
                        var finalExtent = ctrl.adaptRangeValues(enteredMin, enteredMax, rangeLimits.min, rangeLimits.max, nbDecimals);

                        brushg.transition()
                            .call(scope.brush.extent(finalExtent));

                        //fillInputs(); //should be after brush update

                        if (scope.oldRangeLimits[0] !== scope.brush.extent()[0] ||
                            scope.oldRangeLimits[1] !== scope.brush.extent()[1] ||
                            +enteredMin !== scope.brush.extent()[0] ||
                            +enteredMax !== scope.brush.extent()[1]
                        ) {
                            //trigger brush end callback
                            filterToApply = enteredMin > enteredMax ? [+enteredMax.toFixed(nbDecimals), +enteredMin.toFixed(nbDecimals)] : [+enteredMin.toFixed(nbDecimals), +enteredMax.toFixed(nbDecimals)];

                            scope.onBrushEnd()(filterToApply);
                        }
                    }

                    //attach brush listeners
                    function initBrushListeners() {
                        scope.brush
                            //Will memorize the ancient extent
                            .on('brushstart', function brushstart() {
                                scope.oldRangeLimits = scope.brush.extent();
                            })

                            //It will update the min and max inputs, and create a brush on a single value when the user clics on the slider without making a drag( the created brush will be empty )
                            .on('brush', function brushmove() {
                                var s = scope.brush.extent();
                                //the user is moving the whole brush
                                if (scope.oldRangeLimits[0] !== s[0] && scope.oldRangeLimits[1] !== s[1]) {
                                    document.getElementsByName('minRange')[0].value = s[0].toFixed(nbDecimals);
                                    document.getElementsByName('maxRange')[0].value = s[1].toFixed(nbDecimals);
                                }
                                //the user is moving the left brush handler
                                else if (scope.oldRangeLimits[0] !== s[0]) {
                                    document.getElementsByName('minRange')[0].value = s[0].toFixed(nbDecimals);
                                }
                                //the user is moving the right brush handler
                                else if (scope.oldRangeLimits[1] !== s[1]) {
                                    document.getElementsByName('maxRange')[0].value = s[1].toFixed(nbDecimals);
                                }

                                if (scope.brush.empty()) {
                                    var exp = '1e-' + (nbDecimals + 1);
                                    svg.select('.brush').call(scope.brush.clear().extent([s[0], s[1] + Number(exp)]));
                                }
                            })

                            //It will propagate the new filter limits to the rest of the app, it's triggered when the user finishes a brush
                            .on('brushend', function brushend() {
                                var s = scope.brush.extent();

                                //the user is moving the whole brush
                                if (scope.oldRangeLimits[0] !== s[0] && scope.oldRangeLimits[1] !== s[1]) {
                                    //trigger filter process in the datagrid
                                    scope.onBrushEnd()(scope.brush.extent().map(function (n) {
                                        return +n.toFixed(nbDecimals);
                                    }));
                                    filterToApply = scope.brush.extent();
                                }
                                //the user is moving the left brush handler
                                else if (scope.oldRangeLimits[0] !== s[0]) {
                                    scope.onBrushEnd()([+s[0].toFixed(nbDecimals), filterToApply[1]]);
                                    filterToApply = [+s[0].toFixed(nbDecimals), filterToApply[1]];
                                }
                                //the user is moving the right brush handler
                                else if (scope.oldRangeLimits[1] !== s[1]) {
                                    scope.onBrushEnd()([filterToApply[0], +s[1].toFixed(nbDecimals)]);
                                    filterToApply = [filterToApply[0], +s[1].toFixed(nbDecimals)];
                                }
                                lastBrushValues.min = +s[0].toFixed(nbDecimals);
                                lastBrushValues.max = +s[1].toFixed(nbDecimals);
                            });
                    }

                    //--------------------------------------------------------------------------------------------------
                    //----------------------------------------------X AXIS----------------------------------------------
                    //--------------------------------------------------------------------------------------------------
                    var axisTicksNumber = rangeLimits.max > 1e12 || rangeLimits.min < 1e-10 ? 2 : 3;
                    svg.append('g')
                        .attr('class', 'x axis')
                        .attr('transform', 'translate(0,' + (margin.top + 20) + ')')
                        .call(d3.svg.axis()
                            .scale(scale)
                            .orient('top')
                            .ticks(axisTicksNumber)
                            .tickFormat(function (d) {
                                return d3.format(',')(d);
                            })
                    )
                        .selectAll('text').attr('y', -13);

                    svg.append('g').append('text')
                        .attr('class', 'the-minimum-label')
                        .attr('x', -10)
                        .attr('y', height / 2)
                        .attr('text-anchor', 'start')
                        .attr('fill', 'grey')
                        .text(function () {
                            return d3.format(',')(rangeLimits.min);
                        });

                    svg.append('g').append('text')
                        .attr('class', 'the-maximum-label')
                        .attr('x', width + 10)
                        .attr('y', height / 2)
                        .attr('text-anchor', 'end')
                        .attr('fill', 'grey')
                        .text(function () {
                            return d3.format(',')(rangeLimits.max);
                        });
                    //--------------------------------------------------------------------------------------------------
                    //--------------------------------------------ERROR TEXT--------------------------------------------
                    //--------------------------------------------------------------------------------------------------
                    svg.append('g').append('text')
                        .attr('class', 'invalid-value-msg')
                        .attr('x', width / 2)
                        .attr('y', height)
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
                    fillWithManuallyEnteredValues();
                    initRangeInputsListeners();
                    initBrushListeners();

                    //In case of a single value filter
                    if (scope.brush.empty()) {
                        var exp = '1e-' + (nbDecimals + 1);
                        if (maxBrush === rangeLimits.max) {
                            svg.select('.brush').call(scope.brush.clear().extent([minBrush - Number(exp), maxBrush]));
                        }
                        else {
                            svg.select('.brush').call(scope.brush.clear().extent([minBrush, maxBrush + Number(exp)]));
                        }

                        //the case where there is a filter not responding to the range limits -- caused by sample size change
                        if (scope.rangeLimits.minFilterVal && scope.rangeLimits.maxFilterVal) {
                            document.getElementsByName('minRange')[0].value = scope.rangeLimits.minFilterVal;
                            document.getElementsByName('maxRange')[0].value = scope.rangeLimits.maxFilterVal;
                        }
                    }
                }

                function shouldRerender(newRangeLimits, oldRangeLimits) {
                    return !scope.brush ||
                        oldRangeLimits.min !== newRangeLimits.min ||
                        oldRangeLimits.max !== newRangeLimits.max ||
                        lastBrushValues.min !== newRangeLimits.minBrush ||
                        lastBrushValues.max !== newRangeLimits.maxBrush;
                }

                scope.$watch('rangeLimits',
                    function (newRangeLimits, oldRangeLimits) {
                        if (!newRangeLimits) {
                            element.empty();
                            scope.brush = null;
                        }

                        else if (shouldRerender(newRangeLimits, oldRangeLimits)) {
                            element.empty();
                            if (newRangeLimits.min !== newRangeLimits.max) {
                                clearTimeout(renderTimeout);
                                renderTimeout = setTimeout(renderRangerSlider, 100);
                            }
                        }
                    }
                );
            }
        };
    }

    angular.module('talend.widget')
        .directive('rangeSlider', RangeSlider);
})();