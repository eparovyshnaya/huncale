var HuntingResourceInformer = function (informerSelector, sourceUrl) {
    var parentSelector = informerSelector;
    var dataGrabUrl = sourceUrl;
    var data = null;

    toggleInformer(false);
    $('.close-tool').click(function () {
        toggleInformer(false);
    });

    function showResourceData(resourceData) {
        toggleInformer(true);

        var parent = $(parentSelector).find('table tbody');
        var category = parent.find('.category-mark');
        category.find('div').remove();
        category.append('<div class="mark category-' + resourceData.categoryId + '" title="' + resourceData.category + '"></div>');

        parent.find('.value-name').text(resourceData.name);
        parent.find('.value-latin-name').text(resourceData.latinName);

        var rateSection = $('.section-rate');
        if (resourceData.rate) {
            rateSection.show();
            rateSection.find('.value-rate').text(resourceData.rate);
        } else {
            rateSection.hide();
        }

        parent.find('tr.section-restriction').remove();
        for (var i in resourceData.restrictions) {
            var restriction = resourceData.restrictions[i];
            var markTdHtml = '<td><div class="mark season-' + (restriction.limited ? 'limited' : 'unlimited') + '"></div></td>';
            var conditionTdHtml = '<td class="condition">' + restriction.condition + '</td>';
            parent.append('<tr class="section-restriction">' + markTdHtml + conditionTdHtml + '</tr>');
        }
    }

    function toggleInformer(showNotHide) {
        $('.informer').hide();
        if (showNotHide) {
            $(parentSelector).show();
        } else {
            $(parentSelector).hide();
        }
    }

    /** For those clients who manage data itself and does not set 'grbUrl' in constructor */
    this.inplaceResourceData = function (resourceData) {
        showResourceData(resourceData);
    };

    /** Blind clients must take care of grbUlr in constructor to make me load data on request */
    this.grabResourceData = function (resourceId) {
        if (!data) {
            $.ajax({
                url: dataGrabUrl,
                success: function (jsonData) {
                    data = jsonData;
                    showResourceData(data[resourceId]);
                }
            });
        } else {
            showResourceData(data[resourceId]);
        }
    };


};