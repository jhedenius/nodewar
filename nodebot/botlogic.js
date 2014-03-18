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

   // just pick my strongest region with enemy neighbours
   var mahRegionsYo = _.chain(gameState.myRegions())
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
               "fromNode": elem[0],
               "toNode": randomElement(mahNeighbours),
               "comment": randomElement(quotes)
            };
         }

      })
      .value();

   return possibleMove || {
      "moveType": "DONE"
   };
};