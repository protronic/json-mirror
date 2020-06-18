package software.protronic;

import static j2html.TagCreator.a;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.JsonParser;
import io.vertx.core.Vertx;

import static io.vertx.core.parsetools.JsonEventType.*;

@Path("/model")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ModelRessource {

  public static final String ID_FIELD = "#modelID";

  private JsonArray objs = new JsonArray();
  private AtomicLong id = new AtomicLong(0);
  private Logger log = Logger.getLogger("model path");

  @Inject
  Vertx vertx;

  public ModelRessource() {

  }

  @GET
  public JsonArray list() {
    return objs;
  }

  @POST
  public JsonObject add(JsonObject obj) {
    obj.put(ID_FIELD, id.incrementAndGet());
    objs.add(obj);
    return obj;
  }

  @POST
  @Consumes(MediaType.WILDCARD)
  @Path("/save")
  public void save() {
    // Write a file
    System.out.println("huhu");
    vertx.fileSystem().writeFile("./objs.json", objs.toBuffer(), result -> {
      if (result.succeeded()) {
        log.log(Level.INFO, "File written");
      } else {
        log.log(Level.SEVERE, "Oh oh ..." + result.cause());
      }
    });
  }

  @POST
  @Consumes(MediaType.WILDCARD)
  @Path("/load")
  public JsonArray load() {
    JsonParser parser = JsonParser.newParser();
    parser.objectValueMode().handler(e -> {
      if (e.type() == VALUE) {
        JsonObject obj = e.objectValue();
        Long suppliedId = obj.getLong(ID_FIELD);
        if (id.get() < suppliedId)
          id.set(suppliedId);
        replace(obj, suppliedId);
      }
    });
    parser.handle(vertx.fileSystem().readFileBlocking("./objs.json"));
    parser.end();
    return objs;
  }

  @POST
  @Path("/{mid}")
  public Response replace(JsonObject obj, @PathParam("mid") long suppliedId) {
    List<JsonObject> filteredModel = filterByID(suppliedId);
    if (filteredModel.size() < 1) {
      this.log.log(Level.INFO,
          ("Model " + suppliedId + " Wurde nicht in der Liste gefunden. Liste aller modelle: \n\n" + objs.toString()));
       objs.add(obj);
       return Response.ok(obj).build();
    } else if (filteredModel.size() > 1) {
      return ErrorResponseEnum.AMBIGUOUS_MATCH.getResonse();
    } else {
      overrideModel(suppliedId, obj);
      return Response.ok(obj).build();
    }
  }

  @GET
  @Path("/{mid}")
  public Response getModel(@PathParam("mid") long suppliedId) {
    List<JsonObject> requestedModel = filterByID(suppliedId);
    if (requestedModel.size() < 1) {
      return ErrorResponseEnum.NOT_FOUND.getResonse();
    } else if (requestedModel.size() > 1) {
      return ErrorResponseEnum.AMBIGUOUS_MATCH.getResonse();
    } else {
      return Response.ok(requestedModel.get(0)).build();
    }
  }

  @DELETE
  @Path("/{mid}")
  public Response removeModel(@PathParam("mid") long suppliedId) {
    if (filterOutID(suppliedId)) {
      return Response.ok().build();
    } else {
      return ErrorResponseEnum.NOT_FOUND.getResonse();
    }
  }

  @GET
  @Path("/list/schema/{parentForm}")
  public JsonArray listModels(@PathParam("parentForm") String parentForm) {
    return getDataWithModelLinks(parentForm);
  }

  private String linkBuilder(String schemaParentForm, long modelId) {
    String render = a("link").withTarget("_blank")
        .withHref(
            "http:/index.html?schema=%d&mid=%d".formatted(SchemaTempEnum.getIdByParentForm(schemaParentForm), modelId))
        .render();
    log.info(render);
    return render;
  }

  @SuppressWarnings("unchecked")
  private List<JsonObject> filterByID(long filterID) {
    List<JsonObject> temp = new ArrayList<JsonObject>();
    try {
      temp = ((List<JsonObject>) objs.getList()).stream()
          .filter(m -> ((JsonObject) m).getLong(ID_FIELD).equals(filterID)).collect(Collectors.toList());
    } catch (ClassCastException e) {
      log.log(Level.SEVERE, "One of the models is not a JSON Object.");
      return List.of();
    }
    return temp;
  }

  @SuppressWarnings("unchecked")
  private Boolean filterOutID(long filterID) {
    try {
      objs = new JsonArray((List<JsonObject>) objs.getList().stream()
          .filter(m -> !((JsonObject) m).getLong(ID_FIELD).equals(filterID)).collect(Collectors.toList()));
    } catch (ClassCastException e) {
      log.log(Level.SEVERE, "Model " + filterID + " could not be deleted.");
      return false;
    }
    return true;
  }

  @SuppressWarnings("unchecked")
  private List<JsonObject> filterByKey(String key, Object value) {
    List<JsonObject> temp = new ArrayList<JsonObject>();
    try {
      temp = ((List<JsonObject>) objs.getList()).stream().filter(m -> ((JsonObject) m).getValue(key).equals(value))
          .collect(Collectors.toList());
    } catch (ClassCastException e) {
      log.log(Level.SEVERE, "One of the models is not a JSON Object. " + e.getMessage());
      return List.of();
    }
    return temp;
  }

  private void overrideModel(long replaceId, JsonObject replacement) {
    replacement.put(ID_FIELD, replaceId);
    objs = new JsonArray(objs.stream().map(m -> ((JsonObject) m).getLong(ID_FIELD).equals(replaceId) ? replacement : m)
        .collect(Collectors.toList()));
  }

  private JsonArray getDataWithModelLinks(String parentForm) {
    JsonArray result = new JsonArray(filterByKey("#parentForm", parentForm).stream()
        .map((JsonObject m) -> m.put("link", linkBuilder(m.getString("#parentForm"), m.getLong(ID_FIELD))))
        .collect(Collectors.toList()));
    return result;
  }
}
