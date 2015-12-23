(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:verticalBarchart
     * @description This directive renders the vertical bar chart.
     * @restrict E
     * @usage
     * <vertical-barchart id="vBarChart"
     *     width="320"
     *     height="400"
     *     on-click="clickFn(interval)"
     *
     *     key-field="country"
     *     key-label="Country of the world"
     *     active-limits="activeLimits"
     *
     *     primary-data="primaryData"
     *     primary-value-field="occurrences"
     *
     *     secondary-data="secondaryData"
     *     secondary-value-field="filteredOccurrences">
     * </vertical-barchart>
     * @param {number}      width The chart width
     * @param {number}      height The chart height
     * @param {function}    onClick The callback on chart bar click. The interval is an Object {min: minValue, max, maxValue}
     * @param {string}      keyField The key property name in primaryData elements
     * @param {string}      keyLabel The label property name in primaryData elements
     * @param {array}       primaryData The primary value array to render
     * @param {string}      primaryValueField The primary value property name in primaryData
     * @param {array}       secondaryData The secondary value array to render
     * @param {string}      secondaryValueField The secondary value property name in secondaryData
     * @param {array}       activeLimits The limits [min, max[ that represents the active part
     */

    function VerticalBarchart() {
        return {
            restrict: 'E',
            scope: {
                onClick: '&',
                activeLimits: '=',
                keyField: '@',
                keyLabel: '@',
                primaryData: '=',
                primaryValueField: '@',
                secondaryData: '=',
                secondaryValueField: '@'
            },
            link: function (scope, element, attrs) {
                var oldVisuData;
                var labelTooltip = scope.keyLabel;
                var activeLimits = scope.activeLimits;
                var renderPrimaryTimeout, renderSecondaryTimeout, updateLimitsTimeout;
                var containerId = '#' + attrs.id;

                // Define chart sizes and margin
                var margin = {top: 20, right: 20, bottom: 10, left: 15};
                var containerWidth = +attrs.width;
                var containerHeight = +attrs.height;
                var width = containerWidth - margin.left - margin.right;
                var height = containerHeight - margin.top - margin.bottom;

                //------------------------------------------------------------------------------------------------------
                //----------------------------------------------- Tooltip ----------------------------------------------
                //------------------------------------------------------------------------------------------------------
                var tooltip = d3.tip()
                    .attr('class', 'vertical-barchart-cls d3-tip')
                    .offset([0, -11])
                    .direction('w')
                    .html(getTooltipContent);

                function getTooltipContent(data) {
                    var range = data[scope.keyField];
                    var uniqueValue = range.min === range.max;
                    var title = (uniqueValue ? 'Value: ' : 'Range: ');
                    var value = range.label || (uniqueValue ? range.min : '[' + range.min + ', ' + range.max + '[');

                    return '<strong>' + labelTooltip + ':</strong> <span style="color:yellow">' + getSecondaryValueFromRange(getRangeInfos(data)) + ' / ' + getPrimaryValue(data) + '</span>' +
                        '<br/>' +
                        '<br/>' +
                        '<strong>' + title + '</strong> ' +
                        '<span style="color:yellow">' + value + '</span>';
                }

                //------------------------------------------------------------------------------------------------------
                //------------------------------------------ Data adaptation -------------------------------------------
                //------------------------------------------------------------------------------------------------------
                function getInterval(data) {
                    var range = getRangeInfos(data);
                    return [range.min, range.max];
                }

                function getRangeInfos(data) {
                    return data[scope.keyField];
                }

                function getPrimaryValue(data) {
                    return data[scope.primaryValueField];
                }

                function getSecondaryValue(data) {
                    return data[scope.secondaryValueField];
                }

                function getSecondaryValueFromRange(range) {
                    var secondaryData = scope.secondaryData;
                    if (!secondaryData) {
                        return 0;
                    }

                    var secondaryDataItem = _.find(secondaryData, function (dataItem) {
                        var secondaryRange = getRangeInfos(dataItem);
                        return (range.min === secondaryRange.min && range.max === secondaryRange.max) ||
                            (range.min instanceof Date && range.min.getTime() === secondaryRange.min.getTime() && range.max.getTime() === secondaryRange.max.getTime());
                    });
                    return secondaryDataItem ? getSecondaryValue(secondaryDataItem) : 0;
                }

                //------------------------------------------------------------------------------------------------------
                //------------------------------------------- Chart utils ----------------------------------------------
                //------------------------------------------------------------------------------------------------------
                var xScale = d3.scale.ordinal().rangeRoundBands([0, width], 0.2);
                var yScale = d3.scale.linear().range([height, 0]);
                var svg;

                function createContainer() {
                    svg = d3.select(containerId)
                        .append('svg')
                        .attr('class', 'vertical-barchart-cls')
                        .attr('width', containerWidth)
                        .attr('height', containerHeight)
                        .append('g')
                        .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');
                    svg.call(tooltip);
                }

                function configureAxisScales(statData) {
                    xScale.domain(statData.map(getInterval));
                    yScale.domain([0, d3.max(statData, getPrimaryValue)]);
                }

                function drawBars(containerClassName, statData, getValue, barClassName) {
                    svg.insert('g', '.grid')
                        .attr('class', containerClassName)
                        .selectAll('.' + barClassName)
                        .data(statData)
                        .enter()
                        .append('rect')
                        .attr('class', barClassName)
                        .attr('x', function (d) {
                            return xScale(getInterval(d));
                        })
                        .attr('width', xScale.rangeBand())
                        .attr('y', function () {
                            return yScale(0);
                        })
                        .attr('height', 0)
                        .transition().ease('cubic').delay(function (d, i) {
                            return i * 10;
                        })
                        .attr('height', function (d) {
                            return height - yScale(getValue(d));
                        })
                        .attr('y', function (d) {
                            return yScale(getValue(d));
                        });
                }

                function drawHorizontalGrid() {
                    var minSizeBetweenGrid = 18;
                    var ticksThreshold = Math.ceil(height / minSizeBetweenGrid);
                    var ticksNbre = yScale.domain()[1] > ticksThreshold ? ticksThreshold : yScale.domain()[1];

                    svg.append('g')
                        .attr('class', 'grid')
                        .call(d3.svg.axis()
                            .scale(yScale)
                            .orient('right')
                            .tickSize(width, 0, 0)
                            .tickFormat(d3.format(',d'))
                            .ticks(ticksNbre)
                        )
                        //place text
                        .selectAll('.tick text')
                        .attr('y', -5)
                        .attr('x', width / 2)
                        .attr('dy', '.15em')
                        .style('text-anchor', 'middle');
                }

                function drawYAxisLegend() {
                    svg.append('g')
                        .attr('class', 'yAxis')
                        .append('text')
                        .attr('x', -height / 2)
                        .attr('y', -2)
                        .attr('transform', 'rotate(-90)')
                        .style('text-anchor', 'middle')
                        .text(labelTooltip);
                }

                function drawHoverBars(statData) {
                    svg.selectAll('g.bg-rect')
                        .data(statData)
                        .enter()
                        .append('g')
                        .attr('class', 'hover')
                        .attr('transform', function (d) {
                            return 'translate(' + (xScale(getInterval(d)) - 2) + ', 0)';
                        })
                        .append('rect')
                        .attr('width', xScale.rangeBand() + 4)
                        .attr('height', height)
                        .attr('class', 'bg-rect')
                        .style('opacity', 0)
                        .on('mouseenter', function (d) {
                            d3.select(this).style('opacity', 0.4);
                            tooltip.show(d);
                        })
                        .on('mouseleave', function (d) {
                            d3.select(this).style('opacity', 0);
                            tooltip.hide(d);
                        })
                        .on('click', function (d) {
                            scope.onClick({interval: getRangeInfos(d)});
                        });
                }

                //------------------------------------------------------------------------------------------------------
                //------------------------------------------- Chart render ---------------------------------------------
                //------------------------------------------------------------------------------------------------------
                function renderWholeVBarchart(firstVisuData, secondVisuData) {
                    createContainer();
                    configureAxisScales(firstVisuData);
                    drawBars('primaryBar', firstVisuData, getPrimaryValue, 'bar');
                    renderSecondVBars(secondVisuData);

                    drawHorizontalGrid();
                    drawYAxisLegend();
                    drawHoverBars(firstVisuData);
                    scope.buckets = d3.selectAll('rect.bar');
                }

                function renderSecondVBars(secondVisuData) {
                    d3.selectAll('g.bars.secondary').remove();
                    if (secondVisuData) {
                        drawBars('secondaryBar', secondVisuData, getSecondaryValue, 'blueBar');
                    }
                }

                function updateBarsLookFeel() {
                    if (activeLimits) {
                        scope.buckets.transition()
                            .delay(function (d, i) {
                                return i * 10;
                            })
                            .style('opacity', function (d) {
                                var range = getRangeInfos(d);
                                var rangeMin = range.min;
                                var rangeMax = range.max;
                                var minLimit = activeLimits[0];
                                var maxLimit = activeLimits[1];
                                return rangeMin === minLimit || (rangeMin < maxLimit && rangeMax > minLimit) ? '1' : '.4';
                            });
                    }
                }

                //------------------------------------------------------------------------------------------------------
                //---------------------------------------------- Watchers ----------------------------------------------
                //------------------------------------------------------------------------------------------------------
                scope.$watchGroup(['primaryData', 'secondaryData'],
                    function (newValues) {
                        var firstVisuData = newValues[0];
                        var secondVisuData = newValues[1];
                        var firstDataHasChanged = firstVisuData !== oldVisuData;

                        if (firstDataHasChanged) {
                            oldVisuData = firstVisuData;
                            element.empty();
                            //because the tooltip is not a child of the vertical barchart element
                            d3.selectAll('.vertical-barchart-cls.d3-tip').remove();
                            if (firstVisuData) {
                                clearTimeout(renderPrimaryTimeout);
                                renderPrimaryTimeout = setTimeout(renderWholeVBarchart.bind(this, firstVisuData, secondVisuData), 100);
                            }
                        }
                        else {
                            clearTimeout(renderSecondaryTimeout);
                            renderSecondaryTimeout = setTimeout(renderSecondVBars.bind(this, secondVisuData), 100);
                        }
                    }
                );

                scope.$watch('activeLimits',
                    function (newLimits) {
                        if (newLimits) {
                            clearTimeout(updateLimitsTimeout);
                            updateLimitsTimeout = setTimeout(function () {
                                activeLimits = newLimits;
                                updateBarsLookFeel();
                            }, 500);
                        }
                    }
                );

                scope.$on('$destroy', function () {
                    d3.selectAll('.vertical-barchart-cls.d3-tip').remove();
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('verticalBarchart', VerticalBarchart);
})();