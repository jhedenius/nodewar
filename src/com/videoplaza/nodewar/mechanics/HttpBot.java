package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.json.Game;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

public class HttpBot implements PlayerController {

   private final String url;
   private HttpClient httpClient;

   public HttpBot(String url) throws MalformedURLException {
      this.url = "http://localhost:8000";
      this.httpClient = HttpClients.createDefault();
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

         sb.toString(); // return Move.fromJSON(sb.toString()) or somesuch

         return new Move(null, null, null, MoveType.DONE);
      } catch (IOException e) {
         return new Move(null, null, null, MoveType.DONE);
      }
   }
}
