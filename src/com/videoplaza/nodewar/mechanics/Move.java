package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.state.Node;

import java.util.List;

public class Move {

   private MoveType type;
   private String comment;
   private Integer from;
   private Integer to;
   private Integer player;
   private List<Integer> attackerRoll = null;
   private List<Integer> defenderRoll = null;
   private List<Reinforcement> reinforcements = null;

   public Move() {}

   public Move(Node fromNode, Node toNode, String comment, MoveType moveType) {
      this.from = fromNode != null ? fromNode.getId() : null;
      this.to = toNode != null ? toNode.getId() : null;
      this.comment = comment;
      this.type = moveType;
   }

   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public MoveType getMoveType() {
      return type;
   }

   public void setMoveType(MoveType moveType) {
      this.type = moveType;
   }

   public String toString() {
      return player + " from " + from + " to " + to;
   }

   public List<Integer> getAttackerRoll() {
      return attackerRoll;
   }

   public void setAttackerRoll(List<Integer> attackerRoll) {
      this.attackerRoll = attackerRoll;
   }

   public List<Integer> getDefenderRoll() {
      return defenderRoll;
   }

   public void setDefenderRoll(List<Integer> defenderRoll) {
      this.defenderRoll = defenderRoll;
   }

   public List<Reinforcement> getReinforcements() {
      return reinforcements;
   }

   public void setReinforcements(List<Reinforcement> reinforcements) {
      this.reinforcements = reinforcements;
   }

   public void setPlayer(Integer player) {
      this.player = player;
   }

   public Integer getPlayer() {
      return player;
   }

   public Integer getFrom() {
      return from;
   }

   public Integer getTo() {
      return to;
   }
}
