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
                //the left and right margins MUST be the same as the vertical Barchart ones
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

                    //Update min/max inputs with the brush values
                    function updateInputs() {
                        hideMsgErr();
                        var brushValues = scope.brush.extent();
                        document.getElementsByName('minRange')[0].value = brushValues[0].toFixed(nbDecimals);
                        document.getElementsByName('maxRange')[0].value = brushValues[1].toFixed(nbDecimals);
                        return brushValues;
                    }

                    //Init min/max inputs values with existing filter values if defined, min/max otherwise
                    function initInputValues() {
                        var minFilter = rangeLimits.minFilterVal !== undefined ? rangeLimits.minFilterVal : rangeLimits.min;
                        var maxFilter = rangeLimits.maxFilterVal !== undefined ? rangeLimits.maxFilterVal : rangeLimits.max;

                        document.getElementsByName('minRange')[0].value = minFilter.toFixed(nbDecimals);
                        document.getElementsByName('maxRange')[0].value = maxFilter.toFixed(nbDecimals);
                        filterToApply = [minFilter, maxFilter];
                    }

                    //attach events on inputs
                    function initRangeInputsListeners() {
                        var minCorrectness, maxCorrectness;

                        //create a key listener closure
                        function handleKey(rangeType) {
                            return function (e) {
                                switch (e.keyCode) {
                                    case 9:
                                    case 13:
                                        if (minCorrectness !== null && maxCorrectness !== null) {
                                            updateBrush();
                                        }
                                        else {
                                            updateInputs();
                                            hideMsgErr();
                                        }
                                        break;
                                    case 27:
                                        updateInputs();
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

                        //stop event propagation
                        function stopPropagation(e) {
                            e.stopPropagation();
                        }

                        var minRange = document.getElementsByName('minRange')[0];
                        var maxRange = document.getElementsByName('maxRange')[0];

                        minRange.onblur = updateBrush;
                        minRange.onkeydown = stopPropagation;
                        minRange.onkeyup = handleKey('min');

                        maxRange.onblur = updateBrush;
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

                    //Update the brush with input values and call the brush end callback if values have changed
                    function updateBrush() {
                        //Memorize the current brush
                        var initialBrushValue = lastBrushValues;

                        //Collect entered values
                        var enteredMin = ctrl.toNumber(document.getElementsByName('minRange')[0].value);
                        var enteredMax = ctrl.toNumber(document.getElementsByName('maxRange')[0].value);
                        if (enteredMax === null || enteredMin === null) {
                            return;
                        }

                        //Decide of the final brush values
                        lastBrushValues = ctrl.adaptRangeValues(enteredMin, enteredMax, rangeLimits.min, rangeLimits.max);

                        //fire filter to the system
                        if(initialBrushValue.min !== lastBrushValues.min || initialBrushValue.max !== lastBrushValues.max) {
                            //maj brush + sauver (lastBrushValue)
                            brushg.transition().call(scope.brush.extent([lastBrushValues.min, lastBrushValues.max]));

                            //resize the brush with the delta
                            handleUniqueBrushValue();

                            //trigger filter to the system
                            filterToApply = enteredMin > enteredMax ? [+enteredMax.toFixed(nbDecimals), +enteredMin.toFixed(nbDecimals)] : [+enteredMin.toFixed(nbDecimals), +enteredMax.toFixed(nbDecimals)];
                            scope.onBrushEnd()(filterToApply);
                        }
                    }

                    //when brush has a single value (min = max), the brush is empty
                    //an empty brush is not rendered --> we set a delta
                    function handleUniqueBrushValue() {
                        if (scope.brush.empty()) {
                            var min = scope.brush.extent()[0];
                            var max = scope.brush.extent()[1];
                            var exp = '1e-' + (nbDecimals + 2);
                            svg.select('.brush').call(scope.brush.clear().extent([min, max + Number(exp)]));
                        }
                    }

                    //attach brush listeners
                    function initBrushListeners() {
                        var initialBrushValues;
                        scope.brush
                            //Will memorize the ancient extent
                            .on('brushstart', function brushstart() {
                                initialBrushValues = scope.brush.extent();
                            })

                            //It will update the min and max inputs, and create a brush on a single value when the user clics on the slider without making a drag( the created brush will be empty )
                            .on('brush', function brushmove() {
                                var brushValues = scope.brush.extent();
                                if (initialBrushValues[0] !== brushValues[0]) {
                                    document.getElementsByName('minRange')[0].value = brushValues[0].toFixed(nbDecimals);
                                }
                                if (initialBrushValues[1] !== brushValues[1]) {
                                    document.getElementsByName('maxRange')[0].value = brushValues[1].toFixed(nbDecimals);
                                }
                            })

                            //It will propagate the new filter limits to the rest of the app, it's triggered when the user finishes a brush
                            .on('brushend', function brushend() {
                                var brushValues = scope.brush.extent();
                                lastBrushValues = {
                                    min: +brushValues[0].toFixed(nbDecimals),
                                    max: +brushValues[1].toFixed(nbDecimals)
                                };

                                //left brush moved
                                if (initialBrushValues[0] !== brushValues[0]) {
                                    filterToApply[0] = lastBrushValues.min;
                                }
                                //right brush moved
                                if (initialBrushValues[1] !== brushValues[1]) {
                                    filterToApply[1] = lastBrushValues.max;
                                }
                                scope.onBrushEnd()(filterToApply);

                                handleUniqueBrushValue();
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
                    initInputValues();
                    initRangeInputsListeners();
                    initBrushListeners();
                    handleUniqueBrushValue();
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