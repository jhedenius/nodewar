'use strict';

var _ = require('underscore');
var GameState = require('./gamestate');

var quotes = [
   "You come at the king, you best not miss.",
   "It’s play or get played. That simple.",
   "You gon' git got.",
   "Have at you!"
];

function randomElement(arr) {
   return arr[Math.floor(Math.random()*arr.length)];
}

exports.process = function(request) {
   var gameState = GameState.GameState(request.body);

   // just pick my strongest region
   var mahHeadQuartersYo = _.max(gameState.myRegions(), function(elem) { return elem[1].strength; });

   // get its neighbours that are enemies
   var mahHQNeighbours = _.difference(
      gameState.getRegion(mahHeadQuartersYo[0]).neighbours,
      gameState.myRegionIds()
   );

   return {
      "moveType": "MOVE",
      "from": mahHeadQuartersYo[0],
      "to": randomElement(mahHQNeighbours),
      "comment": randomElement(quotes)
   };
};