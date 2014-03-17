'use strict';

var _ = require('underscore');

// decorates the game state with useful functions
exports.GameState = function(gameState) {
   var that = gameState;

   var _cache = {};

   var cachedFunction = function(func, key) {
      return function() {
         if(!_cache.hasOwnProperty(key)) {
            console.log("Cache doesn't contain " + key +", running function");
            _cache[key] = func(arguments);
            console.log("Function returned " + _cache[key]);
         }
         return _cache[key];
      };
   };

   that.myRegions = cachedFunction(function() {
      if(that.hasOwnProperty('occupants')) {
         return _.chain(that.occupants)
            .pairs()
            .filter(function(element, index, list) {
               console.log("Area:", element[0]);
               console.log("Player:", element[1].player);
               console.log("Strength:", element[1].strength);

               return element[1].player === that.currentPlayer;
            })
            .value();
      } else {
         return [];
      }
   }, 'myRegions');

   that.myRegionIds = cachedFunction(function() {
      return _.map(that.myRegions(), function(area) {
         return parseInt(area[0]);
      });
   }, 'myRegionIds');

   that.myNeighbouringRegions = cachedFunction(function() {
      return _.chain(that.map.regions)
         .filter(function(element, index, list) {
            return _.contains(that.myRegionIds(), element.id);
         })
         .value();
   },'myNeighbouringRegions');

   that.getRegion = function(regionId) {
      regionId = parseInt(regionId); // this works even if regionId is a number

      return _.find(that.map.regions, function(region) {
         return region.id === regionId;
      });
   };

   return that;
};