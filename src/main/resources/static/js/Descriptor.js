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
var Descriptor = function (target, defaultContent) {
    var targetSelector = target;
    var defaultContent = defaultContent;
    $(targetSelector).html(defaultContent);

    function genSetter(target, content) {
        return function () {
            console.log(this);
            console.log(this.className);
            $(target).html(content);
        }
    }

    this.append = function (contextContents){
        for (var contextSelector in contextContents) {
            $(contextSelector).hover(
                genSetter(targetSelector, contextContents[contextSelector]),
                genSetter(targetSelector, defaultContent));
        }
    }
};