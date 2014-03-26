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

   var possibleMove = null;

   // just pick my strongest region with enemy neighbours with more than one die
   _.chain(gameState.myRegions())
      .filter(function(elem) { return elem[1].strength > 1; })
      .sortBy(function(elem) { return elem[1].strength; })
      .each(function(elem) {
         // get its neighbours that are enemies
         var mahNeighbours = _.difference(
            gameState.getRegion(elem[0]).neighbours, // myRegions() returns pairs of regionId, regionData
            gameState.myRegionIds()
         );

         if (mahNeighbours.length > 0) {
            possibleMove = {
               "moveType": "MOVE",
               "from": elem[0],
               "to": randomElement(mahNeighbours),
               "comment": randomElement(quotes)
            };
         }

      });

   possibleMove = possibleMove || {
      "moveType": "DONE",
      "comment": "All in the game yo, all in the game."
   };
   console.log("Sending ", possibleMove);

   return possibleMove;
};