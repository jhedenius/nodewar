package com.videoplaza.nodewar.bots;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.MoveType;
import com.videoplaza.nodewar.mechanics.PlayerController;
import com.videoplaza.nodewar.state.Node;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

public class HttpBot implements PlayerController {

   private final String url;
   private HttpClient httpClient;
   private ObjectMapper reader;

   public HttpBot() throws MalformedURLException {
      this(null);
   }

   public HttpBot(String url) throws MalformedURLException {
      this.url = url == null ? "http://localhost:8000/move" : url;
      this.httpClient = HttpClients.createDefault();
      this.reader = new ObjectMapper();
   }

   @Override
   public Move getNextMove(Game gameState) {
      try {
         StringEntity entity = new StringEntity(
            gameState.toJson(),
            ContentType.APPLICATION_JSON
         );

         HttpPost post = new HttpPost(url);
         post.setEntity(entity);

         HttpResponse response = httpClient.execute(post);

         BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

         StringBuilder sb = new StringBuilder();
         String temp;
         while ((temp = reader.readLine()) != null) {
            sb.append(temp);
         }

         return this.reader.readValue(sb.toString(), Move.class);
      } catch (Exception e) {
         return done();
      }
   }

   private Move done() {
      return new Move(null, null, null, MoveType.DONE);
   }
}
