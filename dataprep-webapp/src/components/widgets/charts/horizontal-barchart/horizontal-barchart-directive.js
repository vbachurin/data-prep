(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:horizontalBarchart
     * @description This directive renders the horizontal bar chart.
     * @restrict E
     * @usage
     * <horizontal-barchart id="hBarChart"
     *    width="320"
     *    height="400"
     *    on-click="columnProfileCtrl.barchartClickFn"
     *    visu-data="columnProfileCtrl.histogram.data"
     *    value-field="formattedValue"
     *    key-field="{{columnProfileCtrl.histogram.key}}"
     *    key-label="{{columnProfileCtrl.histogram.label}}">
     * </horizontal-barchart>
     * */

    function HorizontalBarchart() {
        return {
            restrict: 'E',
            scope: {
                onClick: '&',
                visuData: '=',
                keyField: '@',
                valueField: '@',
                valueField2: '@',
                keyLabel: '@',
                frontBarClass: '@'
            },
            link: function (scope, element, attrs) {
                var secondaryClassName = scope.frontBarClass ? scope.frontBarClass : 'blueBar';
                var containerId = '#' + attrs.id;
                var renderTimeout;

                // Define chart sizes and margin
                var margin = {top: 15, right: 10, bottom: 10, left: 10};
                var containerWidth = +attrs.width;

                //------------------------------------------------------------------------------------------------------
                //----------------------------------------------- Tooltip ----------------------------------------------
                //------------------------------------------------------------------------------------------------------
                var tooltip = d3.tip()
                    .attr('class', 'horizontal-barchart-cls d3-tip')
                    .offset([-10, 0])
                    .html(function (d) {
                        var key = getKey(d);
                        var primaryValue = getPrimaryValue(d);
                        var secondaryValue = getSecondaryValue(d);

                        return '<strong>' + scope.keyLabel + ':</strong> <span style="color:yellow">' + (secondaryValue ? secondaryValue + ' / ' : '') + primaryValue + '</span>' +
                            '<br/><br/>' +
                            '<strong>Record:</strong> <span style="color:yellow">' + key + '</span>';
                    });

                //------------------------------------------------------------------------------------------------------
                //---------------------------------------- Data manipulation -------------------------------------------
                //------------------------------------------------------------------------------------------------------
                function getKey(data) {
                    return data[scope.keyField];
                }

                function getPrimaryValue(data) {
                    return data[scope.valueField];
                }

                function getSecondaryValue(data) {
                    return data[scope.valueField2];
                }

                //------------------------------------------------------------------------------------------------------
                //---------------------------------------- Chart manipulation ------------------------------------------
                //------------------------------------------------------------------------------------------------------
                var xScale, yScale;
                var xAxis, yAxis;
                var svg;

                // Axis scale definitions
                function initScales(width, height) {
                    xScale = d3.scale.linear().range([0, width]);
                    yScale = d3.scale.ordinal().rangeBands([0, height], 0.18);
                }

                function initAxis(height) {
                    xAxis = d3.svg
                        .axis()
                        .scale(xScale)
                        .tickFormat(d3.format('d'))
                        .orient('top')
                        .tickSize(-height)
                        .ticks(Math.abs(xScale.range()[1] - xScale.range()[0]) / 50);
                    yAxis = d3.svg
                        .axis()
                        .scale(yScale)
                        .orient('left')
                        .tickSize(0);
                }

                function createContainer(width, height) {
                    svg = d3.select(containerId)
                        .append('svg')
                        .attr('class', 'horizontal-barchart-cls')
                        .attr('width', width)
                        .attr('height', height)
                        .append('g')
                        .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');

                    svg.call(tooltip);
                }

                function configureAxisScales(statData) {
                    xScale.domain([0, d3.max(statData, getPrimaryValue)]);
                    yScale.domain(statData.map(getKey));
                }

                function drawAxis() {
                    svg.append('g')
                        .attr('class', 'x axis')
                        .call(xAxis);

                    svg.selectAll('.tick text').style('text-anchor', 'end');

                    svg.append('g')
                        .attr('class', 'y axis')
                        .call(yAxis);
                }

                function drawBars(containerClassName, statData, getValue, className) {
                    svg.insert('g', '.labels')
                        .attr('class', containerClassName)
                        .selectAll(className)
                        .data(statData)
                        .enter()
                        .append('rect')
                        .attr('class', className)
                        .attr('transform', function (d) {
                            return 'translate(0,' + yScale(getKey(d)) + ')';
                        })
                        .attr('height', yScale.rangeBand())
                        .attr('width', xScale(0))
                        .transition().delay(function (d, i) {
                            return i * 30;
                        })
                        .attr('width', function (d) {
                            return xScale(getValue(d));
                        });
                }

                function drawKeysLabels(statData, width) {
                    svg.append('g')
                        .attr('class', 'labels')
                        .selectAll('label')
                        .data(statData)
                        .enter()

                        //label container
                        .append('foreignObject')
                        .attr('width', width)
                        .attr('height', yScale.rangeBand())
                        .attr('transform', function (d) {
                            return 'translate(0,' + yScale(getKey(d)) + ')';
                        })

                        //label
                        .append('xhtml:div')
                        .attr('class', 'label ' + secondaryClassName)
                        .html(function (d) {
                            return getKey(d) || '(EMPTY)';
                        });
                }

                function drawHoverBars(statData, width) {
                    svg.selectAll('g.bg-rect')
                        .data(statData)
                        .enter()
                        .append('g')
                        .attr('class', 'hover')
                        .attr('transform', function (d) {
                            return 'translate(0, ' + (yScale(getKey(d)) - 2) + ')';
                        })
                        .append('rect')
                        .attr('width', width)
                        .attr('height', yScale.rangeBand() + 4)
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
                            scope.onClick()(d); //TODO
                        });
                }

                function renderHBarchart(statData) {
                    // Chart sizes dynamically computed (it depends on the bars number)
                    var containerHeight = Math.ceil(((+attrs.height) / 15) * (statData.length + 1));
                    var width = containerWidth - margin.left - margin.right;
                    var height = containerHeight - margin.top - margin.bottom;

                    initScales(width, height);
                    initAxis(height);
                    createContainer(containerWidth, containerHeight);
                    configureAxisScales(statData);
                    drawAxis();

                    drawBars('bar', statData, getPrimaryValue, 'bar');
                    if (typeof statData[0][scope.valueField2] !== 'undefined') { //TODO change the condition
                        drawBars('frontBar', statData, getSecondaryValue, secondaryClassName); //TODO change the passed data
                    }
                    drawKeysLabels(statData, width);
                    drawHoverBars(statData, width);
                }


                scope.$watch('visuData',
                    function (statData) {
                        element.empty();
                        //because the tooltip is not a child of the horizontal barchart element
                        d3.selectAll('.horizontal-barchart-cls.d3-tip').remove();
                        if (statData) {
                            clearTimeout(renderTimeout);
                            renderTimeout = setTimeout(renderHBarchart.bind(this, statData), 100);
                        }
                    });

                scope.$on('$destroy', function() {
                    d3.selectAll('.horizontal-barchart-cls.d3-tip').remove();
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('horizontalBarchart', HorizontalBarchart);
})();