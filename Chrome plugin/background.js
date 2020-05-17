'use strict';

chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
      if (request.contentScriptQuery == "queryPriceHistory") {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "http://localhost:8080/?url=" + request.currentUrl, true);
        xhr.send();

        xhr.onload=function() {
            sendResponse(xhr.responseText);
        }

        return true;
      }
    });
