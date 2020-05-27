package software.protronic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;


@Path("/model")
@Produces(MediaType.APPLICATION_JSON)
public class ModelRessource {

    public static final String ID_FIELD = "#modelID";

    private JsonArray objs = new JsonArray();
    private AtomicLong id = new AtomicLong(0);
    private Logger log = Logger.getLogger("model path");

    public ModelRessource() {}

    @SuppressWarnings("unchecked")
    private List<JsonObject> filterByID(int filterID) {
      List<JsonObject> temp = new ArrayList<JsonObject>();
      try {
        temp = ((List<JsonObject>) objs.getList())
          .stream()
          .filter(m -> ((JsonObject) m).getInteger(ID_FIELD).equals(filterID))
          .collect(Collectors.toList());
      } catch (ClassCastException e) {
        log.log(Level.SEVERE, "One of the models is not a JSON Object.");
        return List.of();
      }
      return temp;
    }

    private void overrideModel(int replaceId, JsonObject replacement) {
      replacement.put(ID_FIELD, replaceId);
      objs = new JsonArray(
          objs.stream()
            .map(m -> (((JsonObject) m).getInteger(ID_FIELD).equals(replaceId) ? replacement : m))
            .collect(Collectors.toList())
        );
    }

    @GET
    public JsonArray list() {
        return objs;
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public JsonObject add(JsonObject obj) {
        obj.put(ID_FIELD, id.incrementAndGet());
        objs.add(obj);
        return obj;
    }

    @POST
    @Path("/{mid}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response replace(JsonObject obj, @PathParam("mid") int suppliedId) {
      List<JsonObject> filteredModel = filterByID(suppliedId);
      if (filteredModel.size() < 1) {
        this.log.log(Level.SEVERE, ("Model " + suppliedId + " Wurde nicht in der Liste gefunden. Liste aller modelle: \n\n" + objs.toString()));
        return ErrorResponseEnum.NOT_FOUND.getResonse();
      } else if (filteredModel.size() > 1) {
        return ErrorResponseEnum.AMBIGUOUS_MATCH.getResonse();
      } else {
        overrideModel(suppliedId, obj);
        return Response.ok(obj).build();
      }
    }

    @GET
    @Path("/{mid}")
    @Produces("application/json")
    public Response getModel(@PathParam("mid") int suppliedId){

      List<JsonObject> requestedModel = filterByID(suppliedId);

      if (requestedModel.size() < 1){
        return ErrorResponseEnum.NOT_FOUND.getResonse();
      } else if (requestedModel.size() > 1) {
        return ErrorResponseEnum.AMBIGUOUS_MATCH.getResonse();
      } else {
        return Response.ok(requestedModel.get(0)).build();
      }
    }
}
