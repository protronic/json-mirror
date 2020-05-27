package software.protronic;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@Path("/model")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VertxJsonResource {

    private JsonArray objs = new JsonArray();
    private AtomicLong id = new AtomicLong(0);
    // @GET
    // @Path("{name}/object")
    // public JsonObject jsonObject(@PathParam String name) {
    //     return new JsonObject().put("Hello", name);
    // }

    // @GET
    // @Path("{name}/array")
    // public JsonArray jsonArray(@PathParam String name) {
    //     return new JsonArray().add("Hello").add(name);
    // }
    public VertxJsonResource() {
        // objs.add(new JsonObject().put("key", "value").put("key1", "value"));
        // objs.add(new JsonObject().put("key", 1));
    }

    @GET
    public JsonArray list() {
        return objs;
    }

    @POST
    public JsonArray add(JsonObject obj) {
        obj.put("_ID", id.incrementAndGet());
        objs.add(obj); 
        return objs;
    }

    // @DELETE
    // public Set<JsonObject> delete(JsonObject obj) {
    //     objs.removeIf(existingJsonObject -> existingJsonObject.name.contentEquals(obj.name));
    //     return objs;
    // }
}