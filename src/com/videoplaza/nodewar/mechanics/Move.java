package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.state.Node;

public class Move {

   private Type type;
   private Node fromNode;
   private Node toNode;
   private String comment;

   public Move(Node fromNode, Node toNode, String comment) {
      this.fromNode = fromNode;
      this.toNode = toNode;
      this.comment = comment;
   }

   public String getComment() {
      return comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public Node getFromNode() {
      return fromNode;
   }

   public void setFromNode(Node fromNode) {
      this.fromNode = fromNode;
   }

   public Node getToNode() {
      return toNode;
   }

   public void setToNode(Node toNode) {
      this.toNode = toNode;
   }


   public Type getType() {
      return type;
   }

   public void setType(Type type) {
      this.type = type;
   }

   public String toString() {
      return fromNode.getOccupier() + " from " + fromNode.getName() + " to " + toNode.getName();
   }
}
