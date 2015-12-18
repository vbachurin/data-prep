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
     *     on-click="columnProfileCtrl.vBarchartClickFn"
     *     visu-data="columnProfileCtrl.histogram.data"
     *     visu-data-2="columnProfileCtrl.histogram2.data"
     *     key-field="data"
     *     key-label="{{columnProfileCtrl.histogram.label}}">
     *     value-field="{{columnProfileCtrl.histogram.key}}"
     *     value-field="{{columnProfileCtrl.histogram2.key}}"
     *     active-limits="columnProfileCtrl.histogram.existingFilter"
     * </vertical-barchart>
     * @param {number} width The chart width
     * @param {number} height The chart height
     * @param {function} onClick The callback on chart click
     * @param {array} visuData The value array to render
     * @param {array} visuData2 The second value array to render
     * @param {string} keyField The key property name in visuData elements
     * @param {object} keyLabel The label property name in visuData elements
     * @param {string} valueField The value property name in visuData elements used for 1st column
     * @param {string} valueField2 The second value property name in visuData elements used for 2nd column
     * @param {array} activeLimits The filter limits
     * */

    function VerticalBarchart() {
        return {
            restrict: 'E',
            scope: {
                onClick: '&',
                visuData: '=',
                visuData2: '=',
                keyField: '@',
                valueField: '@',
                valueField2: '@',
                keyLabel: '@',
                activeLimits: '='
            },
            link: function (scope, element, attrs) {
                var oldVisuData;
                var labelTooltip = scope.keyLabel;
                var activeLimits = scope.activeLimits;
                var renderPrimaryTimeout, renderSecondaryTimeout, updateLimitsTimeout;
                var finishedRendering = false;
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

                    return '<strong>' + labelTooltip + ':</strong> <span style="color:yellow">' + getSecondaryValue(data) + ' / ' + getPrimaryValue(data) + '</span>' +
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
                    return data[scope.valueField];
                }

                function getSecondaryValue(data) {
                    if(!scope.visuData2) {
                        return 0;
                    }

                    var range = data[scope.keyField];
                    var secondaryDate = _.find(scope.visuData2, function(data2) {
                        var range2 = data2[scope.keyField];
                        return (range.min === range2.min && range.max === range2.max) ||
                            (range.min instanceof Date && range.min.getTime() === range2.min.getTime() && range.max.getTime() === range2.max.getTime());
                    });
                    return secondaryDate ? secondaryDate[scope.valueField2] : 0;
                }

                //------------------------------------------------------------------------------------------------------
                //---------------------------------------- Chart manipulation ------------------------------------------
                //------------------------------------------------------------------------------------------------------
                // Axis scale definitions
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

                function drawBars(selector, statData, getValue, className) {
                    svg.append('g')
                        .attr('class', 'bars ' + className)
                        .selectAll(selector)
                        .data(statData)
                        .enter()
                        .append('rect')
                        .attr('class', className)
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
                        //draw grid lines
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

                function removeSecondaryData() {
                    d3.selectAll('g.bars.secondary').remove();
                }

                function removeHorizontalGrid() {
                    d3.selectAll('g.grid').remove();
                }

                function removeHoverBars() {
                    d3.selectAll('g.hover').remove();
                }

                function renderWholeVBarchart(firstVisuData, secondVisuData) {
                    createContainer();
                    configureAxisScales(firstVisuData);
                    drawBars('.bar', firstVisuData, getPrimaryValue, 'bar');
                    if (secondVisuData) {
                        drawBars('.secondaryBar', secondVisuData, getSecondaryValue, 'secondaryBar');
                    }

                    drawHorizontalGrid();
                    drawYAxisLegend();
                    drawHoverBars(firstVisuData);
                    scope.buckets = d3.selectAll('rect.bar');
                    finishedRendering = true;
                }

                function renderSecondVBars(firstVisuData, secondVisuData) {
                    removeSecondaryData();
                    removeHorizontalGrid();
                    removeHoverBars();

                    drawBars('.secondaryBar', secondVisuData, getSecondaryValue, 'secondaryBar');
                    drawHorizontalGrid();
                    drawHoverBars(firstVisuData);

                    finishedRendering = true;
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

                scope.$watchGroup(['visuData', 'visuData2'],
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
                                finishedRendering = false;
                                clearTimeout(renderPrimaryTimeout);
                                renderPrimaryTimeout = setTimeout(renderWholeVBarchart.bind(this, firstVisuData, secondVisuData), 100);
                            }
                        }
                        else if (secondVisuData) {
                            finishedRendering = false;
                            clearTimeout(renderSecondaryTimeout);
                            renderSecondaryTimeout = setTimeout(renderSecondVBars.bind(this, firstVisuData, secondVisuData), 100);
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
            }
        };
    }

    angular.module('talend.widget')
        .directive('verticalBarchart', VerticalBarchart);
})();