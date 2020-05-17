'use strict';

const express = require('express');
const mysql = require('mysql');


// Constants
const PORT = 8080;
const HOST = '0.0.0.0';

// App
var db_conn = mysql.createConnection({
    host: "mysql",
    port: 3306,
    user: "real_estate",
    password: "real_estate",
    database: "real_estate",
    dateStrings: 'date'
  });

const getCircularReplacer = () => {
    const seen = new WeakSet();
    return (key, value) => {
        if (typeof value === "object" && value !== null) {
        if (seen.has(value)) {
            return;
        }
        seen.add(value);
        }
        return value;
    };
};

function filterList(json) {
    let filteredList = [];
    let last = {date_time: null, price:null};

    json.forEach(element => {
        if (filteredList.length == 0) {
            filteredList.push(element);
            filteredList[0].change = 0;
        }

        if (element.price != filteredList[filteredList.length-1].price) {
            
            let change = (element.price - filteredList[filteredList.length-1].price) / filteredList[filteredList.length-1].price * 100;
            element.change = change.toPrecision(3);
            filteredList.push(element);
        }
    });

    return filteredList.reverse();
}


function query(url, res) {
    console.log("URL " + url);

    if (db_conn.state == 'disconnected') {
        db_conn.connect(function(err) {
            if (err) throw err;
            console.log("Connected!");
        });
    }
//aHR0cHM6Ly93d3cuYXJ1b2Rhcy5sdC9idXRhaS10cmFrdW9zZS1sZW50dmFyaW8tbS1zYXVsZXMtZy0xMC1wcmllemFzY2l1LTEtaWxnYWFtemlhaS0xLTI4NjQwOTcv
    db_conn.query("select date_time, price from apartment where url = ? order by date_time asc;", url, function (err, result) {
        if (err) throw err;

        
        let filtered = filterList(result);
        console.log("Result: " + JSON.stringify(filtered, getCircularReplacer()));


        res.send(filtered);
      })
}

const app = express();


app.get('/', (req, res) => {
    console.log("Query param: " + req.query.url);
    var result = query(Buffer.from(req.query.url, 'base64').toString('binary'), res);
  });


app.listen(PORT, HOST);
console.log(`Running on http://${HOST}:${PORT}`);