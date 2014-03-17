'use strict';

var express = require("express");
var logic = require("./botlogic");
var app = express();

app.use(express.urlencoded());
app.use(express.json());
app.use(express.methodOverride());
app.use(app.router);

app.post('/move', function(req, res){
   res.send(logic.process(req));
});

app.use(logErrors);
app.use(clientErrorHandler);
app.use(errorHandler);

app.use(express.static(__dirname + '/public'));

function logErrors(err, req, res, next) {
   console.error(err.stack);
   next(err);
}

function clientErrorHandler(err, req, res, next) {
   if (req.xhr) {
      res.send(500, { error: 'Something blew up!' });
   } else {
      next(err);
   }
}

function errorHandler (err, req, res, next){
   res.status(500);
   res.send('error', { error: err.message });
}

app.listen(8000);