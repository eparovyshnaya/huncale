/*******************************************************************************
 * Copyright (c) 2019, 2020 CleverClover
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT which is available at
 * https://spdx.org/licenses/MIT.html#licenseText
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *     CleverClover - initial API and implementation
 *******************************************************************************/
var HuntingCalendar = function (cData, uiTargetId, resetCallbackFunction, resourceSelectionCallback) {
    var calendarData = cData;
    var resourceIdToName = [];
    var resetCallback = resetCallbackFunction;
    var resourceSelectedListener = resourceSelectionCallback;

    var namingBoxWidth = 150, namingBoxHeight = 30;
    var paddingLeft = 40, paddingRight = 40, paddingTop = 60, paddingBottom = 40;
    var x0 = paddingLeft, y0 = paddingTop;

    var windowPortion = $(window).width() <= 1170 ? 1.7 : 3.0;
    var pointsPerDay = Math.max(($(window).width() / windowPortion - paddingLeft - paddingRight - 50) / calendarData.beacons.maxScope,
            ($(window).width() / windowPortion - paddingLeft - paddingRight - 50) / calendarData.beacons.distance);
    var monthRowHeight = 10, monthRowMargin = 5;
    var resourceRowHeight = 6, resourceRowMargin = 4;
    var categoryMarkWidth = 10, categoryMarkMargin = 10;
    var beaconWidth = 20, beaconHeight = 30, beaconMargin = 3;

    var width0 = calendarData.beacons.distance * pointsPerDay;
    var height0 = calendarData.resourcesInScope * (resourceRowHeight + resourceRowMargin) +
        (monthRowHeight + monthRowMargin);

    var resourceY = {};

    var calendarPaper = Raphael(uiTargetId, width0 + paddingLeft + paddingRight, height0 + paddingTop + paddingBottom);
    var namer, namingBox, namingTextBox;

    drawMonthRuler();
    drawBeacons();
    drawResources();
    drawBeaconLine();
    drawNamer();

    function drawMonthRuler() {
        var x = x0;
        for (var year in calendarData.years) {
            if (!calendarData.years.hasOwnProperty(year)) {
                continue;
            }
            var thisYearData = calendarData.years[year];
            for (var monthNo in thisYearData) {
                if (!thisYearData.hasOwnProperty(monthNo)) {
                    continue;
                }
                var monthData = thisYearData[monthNo];
                var monthLength = (monthData.maxInScope - monthData.minInScope + 1) * pointsPerDay;
                var result = calendarPaper.rect(x, y0, monthLength, monthRowHeight, 0).attr('title', year + ' | ' + monthData.name);
                $(result.node).attr('class', 'month month-' + monthNo + '-bg');
                x += monthLength;
            }
        }
    }

    function drawBeacons() {
        var startLabel = calendarData.beacons.start.label;
        var endLabel = calendarData.beacons.end.label;

        drawBeacon(x0 + calendarData.beacons.now.offset * pointsPerDay, beaconHeight * 1.4, 'beacon-now', calendarData.beacons.now.label);
        drawBeacon(x0, beaconHeight, 'beacon-bound', startLabel);
        drawBeacon(x0 + width0, beaconHeight, 'beacon-bound', endLabel);

        if (calendarData.beacons.start.canMoveFromNow) {
            drawTimeMachineArm(true, true, startLabel, endLabel);
        }
        if (calendarData.beacons.start.canMoveToNow) {
            drawTimeMachineArm(true, false, startLabel, endLabel);
        }
        if (calendarData.beacons.end.canMoveToNow) {
            drawTimeMachineArm(false, true, startLabel, endLabel);
        }
        if (calendarData.beacons.end.canMoveFromNow) {
            drawTimeMachineArm(false, false, startLabel, endLabel);
        }
    }

    function drawBeacon(x, height, cls, label) {
        var xb0 = x, yb0 = y0 - beaconMargin;
        var figure = calendarPaper.path("M" + xb0 + " " + (yb0) +
            " L" + (xb0 + beaconWidth / 2) + " " + (yb0 - height) +
            " L" + (xb0 - beaconWidth / 2) + " " + (yb0 - height) +
            " L" + xb0 + " " + yb0)
            .attr('title', 'Восстановить обзор')
            .click(function () {
                discardTimeMoving();
            });

        $(figure.node).attr('class', 'a-tool ' + cls);

        var fontSize = 10;
        var text = calendarPaper.text(xb0, yb0 - height - fontSize, label)
            .attr({"font-family": "Tahoma", "font-size": fontSize, "font-weight": 'normal', "fill": 'gray'});
        $(text.node).attr('class', cls + '-text');
        return figure;
    }

    function drawTimeMachineArm(start, left, startLabel, endLabel) {
        var xb0 = x0 + (start ? 0 : width0) + beaconWidth / 2 * (left ? -1 : +1),
            yb0 = y0 - beaconMargin - beaconHeight / 3;
        var figure = calendarPaper.path("M" + xb0 + " " + yb0 +
            " L" + (xb0 + beaconWidth * (left ? -1 : +1)) + " " + yb0 +
            " L" + xb0 + " " + (yb0 - beaconHeight / 3) +
            " L" + xb0 + " " + yb0)
            .attr('title', (start == left ? 'Расширить' : 'Сузить') + ' обзор на ' + calendarData.beacons.moveStepLabel)
            .click(function () {
                runTimeMachine(start, left, startLabel, endLabel);
            });

        $(figure.node).attr('class', 'a-tool time-machine-arm');
    }

    function drawResources() {
        var y = monthRowHeight + monthRowMargin + y0;
        var hRect, needle, prohibition;
        var flagLetterLeft = null, flagLetterRight = null;
        for (var resourceName in calendarData.resources) {
            if (!calendarData.resources.hasOwnProperty(resourceName)) {
                continue
            }
            var resData = calendarData.resources[resourceName];
            if (!resData.inScope) {
                continue;
            }
            resourceIdToName[resData.id] = resourceName;

            var flagLetterClaimant = resourceName.charAt(0).toLocaleUpperCase();
            flagLetterLeft = drawResourceTools(true, y, resourceName, resData, flagLetterLeft);

            prohibition = calendarPaper.rect(x0, y, width0, resourceRowHeight, 3);
            $(prohibition.node).attr('class', 'restriction-prohibited');

            for (var i in resData.restrictions) {
                var restriction = resData.restrictions[i];
                if (!restriction.inScope) {
                    continue;
                }
                hRect = calendarPaper.rect(x0 + (restriction.startsAt * pointsPerDay), y, restriction.lasts * pointsPerDay, resourceRowHeight, 3);
                $(hRect.node).attr('class', restriction.limited ? 'restriction-limited' : 'restriction-unlimited');
            }

            needle = calendarPaper.rect(x0 - 3, y - 2, width0 + 8, resourceRowHeight + 4, 4);
            $(needle.node).attr('class', 'a-tool needle resource' + resData.id);
            resourceSelector($(needle.node), resData.id, resourceName);

            flagLetterRight = drawResourceTools(false, y, resourceName, resData, flagLetterRight);

            resourceY['resource' + resData.id] = y;
            y += resourceRowHeight + resourceRowMargin;
        }
    }

    function drawResourceTools(left, y, resourceName, resData, flagLetter) {
        var x = left ? (x0 - categoryMarkMargin - categoryMarkWidth) : (x0 + width0 + categoryMarkMargin);
        var categoryMark = $(calendarPaper.rect(x, y, categoryMarkWidth, resourceRowHeight, 2).attr('title', resData.category).node);
        categoryMark.attr('class', 'a-tool category-marker category-' + resData.categoryId);
        resourceSelector(categoryMark, resData.id, resourceName);

        var firstLetter = resourceName.charAt(0).toLocaleUpperCase();
        if (flagLetter != firstLetter) {
            flagLetter = firstLetter;
            var tx = left ? (x - categoryMarkMargin ) : (x + categoryMarkWidth + categoryMarkMargin );
            var text = calendarPaper.text(tx, y + resourceRowMargin, flagLetter)
                .attr({"font-size": resourceRowHeight + resourceRowMargin - 1, "font-family": "consolas", "font-weight": "bold"});
            $(text.node).attr('class', 'catalog-letter');
        }

        return flagLetter;
    }

    function drawBeaconLine() {
        var xb0 = x0 + calendarData.beacons.now.offset * pointsPerDay, yb0 = y0;
        var figure = calendarPaper.path("M" + xb0 + " " + (yb0) +
            " L" + xb0 + " " + (yb0 + height0));
        $(figure.node).attr('class', 'beacon-now-line');
    }

    function drawNamer() {
        calendarPaper.setStart();

        namingBox = calendarPaper.rect(0, 0, namingBoxWidth, namingBoxHeight, 3);
        $(namingBox.node).attr('class', 'namer');

        namingTextBox = calendarPaper.text(8, 15, '')
            .attr({'text-anchor': 'start', "font-family": "consolas", "font-size": 14});
        $(namingTextBox.node).attr('class', 'naming-text');

        namer = calendarPaper.setFinish();
        namer.hide();
    }

    function resourceSelector(node, id, name) {
        var cls = 'resource' + id;
        node.hover(function () {
                namingTextBox.attr({'text': name});
                var textWidth = namingTextBox.getBBox().width + 16;
                namingBox.attr({'width': textWidth});
                namer.transform('t' + (x0 + width0 / 2 - textWidth / 2) + ',' + (resourceY[cls] - namingBoxHeight - resourceRowMargin * 2));
                namer.show();
                $('.' + cls).attr('style', 'stroke-width: 2');
            },
            function () {
                namer.hide();
                $('.' + cls).attr('style', '');
            });

        if (resourceSelectedListener) {
            node.click(function () {
                resourceSelectedListener(calendarData.resources[name]);
            });
        }
    }

    function runTimeMachine(moveStart, moveBack, timestampStart, timestampEnd) {
        var params = {moveStart: moveStart, moveBack: moveBack, timestampStart: timestampStart, timestampEnd: timestampEnd};
        resetCallback(params);
    }

    function discardTimeMoving() {
        resetCallback(null);
    }

    this.getResourceData = function (id) {
        var resourceName = resourceIdToName[id];
        return calendarData.resources[resourceName];
    };
};