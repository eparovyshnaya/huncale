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