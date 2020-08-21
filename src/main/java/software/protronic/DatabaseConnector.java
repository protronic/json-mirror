package software.protronic;

import io.vertx.mutiny.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.smallrye.mutiny.Uni;

public class DatabaseConnector implements DataInterface {

  private WebClient webClient;
  private String path;
  private String tableName;

  private final String formatSetQuery = "UPDATE %s SET log = '%s' WHERE _id = %d";
  private final String formatGetQuery = "{\"query\": \"SELECT log FROM %s WHERE _id = %d\"}";
  private final String formatListQuery = "{\"query\": \"SELECT * FROM %s\"}";
  private final String formatAddQuery = "INSERT INTO %s (log) VALUES ('%s'); SELECT SCOPE_IDENTITY() as _id;";
  private final String formatRemoveQuery = "DELETE TOP 1 FROM %s WHERE _id = %d";

  public DatabaseConnector(Vertx vertx, int port, String host, String path, String tableName) {
    this.path = path;
    this.tableName = tableName;
    this.webClient = WebClient.create(vertx, new WebClientOptions().setDefaultHost(host).setDefaultPort(port).setSsl(true).setTrustAll(true));
  }

  private JsonObject createError (int code, String msg){
    return new JsonObject()
      .put("code", code)
      .put("message", msg);
  }

  @Override
  public Uni<JsonObject> set(int id, JsonObject obj) {
    return webClient
        .post(path)
        .sendJsonObject(new JsonObject().put("query", String.format(formatSetQuery, tableName, obj.toString(), id)))
        .map(resp -> {
          if(resp.statusCode() == 200){
            return new JsonObject();
          } else {
            return createError(resp.statusCode(), resp.bodyAsString());
          }
        });
  }

  @Override
  public Uni<JsonObject> add(JsonObject obj) {
    return webClient
        .post(path)
        .sendJsonObject(new JsonObject().put("query", String.format(formatAddQuery, tableName, obj.toString())))
        .map(resp -> {
          if(resp.statusCode() == 200){
            JsonObject respData = resp.bodyAsJsonArray().getJsonObject(0);
            System.out.println(respData);
            return respData;
          } else {
            return createError(resp.statusCode(), resp.bodyAsString());
          }
        });
  }

  @Override
  public Uni<JsonArray> list() {
    return webClient
      .post(path)
      .sendJsonObject(new JsonObject(String.format(formatListQuery, tableName)))
      .map(resp -> {
          if(resp.statusCode() == 200){
            System.out.println(String.format("Response: %s", resp.bodyAsString()));
            return resp.bodyAsJsonArray();
          } else {
            System.out.println(String.format("Failed Response: %s\n\n%s", resp.statusCode(), resp.bodyAsString()));
            return new JsonArray().add(createError(resp.statusCode(), resp.bodyAsString()));
          }
      });
  }

  @Override
  public Uni<JsonObject> get(int id) {
    return webClient
        .post(path)
        .sendJsonObject(new JsonObject(String.format(formatGetQuery, tableName, id)))
        .map(resp -> {
          if(resp.statusCode() == 200){
            JsonObject firstMatch = resp.bodyAsJsonArray().getJsonObject(0);
            if (firstMatch == null) return null;
            else return new JsonObject(firstMatch.getValue("log").toString());
          } else {
            return createError(resp.statusCode(), resp.bodyAsString());
          }
        });
  }

  @Override
  public Uni<Object> remove(int id) {
    return webClient
      .post(path)
      .sendJsonObject(new JsonObject().put("query", String.format(formatRemoveQuery, tableName, id)))
      .map(resp -> {
        if(resp.statusCode() == 200){
          return null;
        } else {
          return createError(resp.statusCode(), resp.bodyAsString());
        }
      });
  }
}
