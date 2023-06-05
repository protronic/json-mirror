package software.protronic;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface DataInterface {
  public Uni<JsonObject> set(int id, JsonObject obj);
  public Uni<JsonObject> add(JsonObject obj);
  public Uni<JsonObject> get(int id);
  public Uni<JsonArray> list();
  public Uni<JsonArray> list(String parentForm);
  public Uni<Object> remove(int id);
}
