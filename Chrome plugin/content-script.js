let priceBlock = document.getElementsByClassName('price-block');

let createElement = function(json) {
    var priceHistoryDiv = document.createElement('div');
    var txt = "";
    txt += "<table border='1'>";
    var parsed = JSON.parse(json);
    for (x in parsed) {
      txt += "<tr><td>" + parsed[x].date_time + "</td><td>" + parsed[x].price + "</td><td>" +
      parsed[x].change + "</td></tr>";
    }
    txt += "</table>"
    priceHistoryDiv.innerHTML = txt;

    priceBlock[0].parentNode.insertBefore(priceHistoryDiv, priceBlock[0]);

}

if (priceBlock.length > 0) {
    chrome.runtime.sendMessage(
        {contentScriptQuery: "queryPriceHistory", currentUrl: btoa(document.URL)},
        function(response) {
            createElement(response);        }
    );
    
}
