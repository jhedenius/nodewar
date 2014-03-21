package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.state.Game;

public interface PlayerController {

   /**
    * Generate your next move.
    * This method will be called over and over again in your turn.
    * @param gameState The current state of the game
    * @return The move you want. Your turn ends when you return a move with MoveType.DONE
    */
   public Move getNextMove(Game gameState);

}
