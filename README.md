#Node War#
Node War is a turn based strategy game. A map is divided into regions (or territories, or nodes) and each
territory is controlled by a player (a bot) who has 1-8 dice on it. The initial setup of territories and dice is random.

There are two possible actions for a bot to take: attack a neighbour or end its turn. A bot may execute as many attacks as it wants during its turn.

##Attack##
To be able to attack, a territory must have at least 2 dice. Battle is resolved by rolling all the dice on the attacking territory and all the dice on the
defending territory: the greatest sum wins (defender wins ties).
If the attacker wins, all but 1 of the dice from the attacking territory move to the captured territory and the defender loses all its dice.
If the defender wins, the attacker loses all but 1 die which is left on the attacking territory.

##End Turn##
When the turn ends the player will receive reinforcements.
The number of reinforcements received is the largest number of connected territories the player controls.
Reinforcements are placed randomly on player controlled territories.
Territories can never have more than 8 dice, any reinforcements which cannot be placed on a territory are lost.

##How to write your bot##
* Make a copy of com.videoplaza.nodewar.SimpleBot, implement the only method ```Move getNextMove(Game game)```.
* Use ```game.getNodes()``` to get a list of territories, including their owner, number of dice, neighbours, etc.
* Some methods of ```GameStateUtils``` may be useful.
* Configure a session in ```game_config.csv```, run ```com.videoplaza.nodewar.Main```
* Open ```viewer/index.html``` in a browser (over http, file does not work) to watch the games (if more than one, you can modify the URL to see other than the
firsts). You can change replay speed and turn off sounds using the controls below the map.
* The game uses a constant random seed so you can replay the same game and debug your bot. To see different games, change the first line of ```main```.

###Don'ts###
We wrote this game in a few evenings during the last couple of weeks. Cheating or crashing it is not very difficult or impressive. We ask you to:
* Not modify shared data, like the game map, player names or the list of moves (you may however change the owner and strength of Nodes if you want).
* Try your best to return valid moves (we check but haven't extensively tested)
* Not run into infinite loops/infinite recursions, allocate all of the heap, throw RuntimeExceptions etc

##Tournament##
Two groups of five will play 10 games each. The top two bots from each group and the top third place (in points) will go into the winners' final,
the rest into the losers' final. Start order in the finals is determined by rank in the qualifiers: the top qualifying bot will move last,
the second will move second to last and so on. Finals are one game each.

##The game that inspired us##
http://www.gamedesign.jp/flash/dice/dice.html

