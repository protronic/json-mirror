package software.protronic;

import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import io.vertx.mutiny.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@Path("/model")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ModelRessource {

  public static final String ID_FIELD = "#modelID";
  // private static final Logger LOG = Logger.getLogger(ModelRessource.class);

  // private List<JsonObject> modelList = Collections.synchronizedList(new LinkedList<>());
  // private AtomicLong id = new AtomicLong(0);
  // private Logger log = Logger.getLogger("model path");

  @Inject
  Vertx vertx;
  // @Inject
  // SchemaRessouce schemaRessouce;

  private DatabaseConnector dbc;

  @PostConstruct
  void initialize() {
    dbc = new DatabaseConnector(vertx, 8084, "10.19.28.94", "/query?database=formly", "model");
  }

  // public JsonArray list() {
  // return new JsonArray(modelList);
  // }
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response list() {
    try {
      JsonArray models = dbc.list().subscribe().asCompletionStage().get();

      return Response.ok(models).build();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      // LOG.error(ErrorResponseEnum.DB_REQUEST_FAILED.getMessage());
      return ErrorResponseEnum.DB_REQUEST_FAILED.getResponse();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response list(@QueryParam("parentForm") String parentForm) {
    try {
      JsonArray models = dbc.list(parentForm).subscribe().asCompletionStage().get();

      return Response.ok(models).build();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return ErrorResponseEnum.DB_REQUEST_FAILED.getResponse();
    }
  }

  @POST
  public Response add(JsonObject obj) {
    // obj.put(ID_FIELD, id.incrementAndGet());
    // modelList.add(obj);
    // return obj;

    try {
      JsonObject newId = dbc.add(obj).subscribe().asCompletionStage().get();
      if (newId == null)
        return ErrorResponseEnum.BAD_REQUEST.getResponse();
      else
        return Response.ok(newId).build();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return ErrorResponseEnum.DB_REQUEST_FAILED.getResponse();
    }

  }

  // @POST
  // @Consumes(MediaType.WILDCARD)
  // @Path("/save")
  // public void save() {
  // // // Write a file
  // // System.out.println("huhu");
  // // vertx.fileSystem().writeFile("./modelList.json", new
  // // JsonArray(modelList).toBuffer(), result -> {
  // // if (result.succeeded()) {
  // // log.log(Level.INFO, "File written");
  // // } else {
  // // log.log(Level.SEVERE, "Oh oh ..." + result.cause());
  // // }
  // // });

  // }

  // @POST
  // @Consumes(MediaType.WILDCARD)
  // @Path("/load")
  // public JsonArray load() {
  // JsonParser parser = JsonParser.newParser();
  // parser.objectValueMode().handler(e -> {
  // if (e.type() == VALUE) {
  // JsonObject obj = e.objectValue();
  // Long suppliedId = obj.getLong(ID_FIELD);
  // if (id.get() < suppliedId)
  // id.set(suppliedId);
  // replace(obj, suppliedId);
  // }
  // });
  // // parser.handle(vertx.fileSystem().readFileBlocking("./modelList.json"));
  // parser.end();
  // return new JsonArray(modelList);
  // }

  @POST
  @Path("/{mid}")
  public Response replace(JsonObject obj, @PathParam("mid") int suppliedId) {
    // List<JsonObject> filteredModel = filterByID(suppliedId);
    // if (filteredModel.size() < 1) {
    // this.log.log(Level.INFO, ("Model " + suppliedId + " Wurde nicht in der Liste
    // gefunden. Liste aller modelle: \n\n"
    // + modelList.toString()));
    // modelList.add(obj);
    // return Response.ok(obj).build();
    // } else if (filteredModel.size() > 1) {
    // return ErrorResponseEnum.AMBIGUOUS_MATCH.getResonse();
    // } else {
    // overrideModel(suppliedId, obj);
    // return Response.ok(obj).build();
    // }
    try {
      JsonObject model = dbc.set(suppliedId, obj).subscribe().asCompletionStage().get();
      return Response.ok(model).build();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return ErrorResponseEnum.DB_REQUEST_FAILED.getResponse();
    }
  }

  @GET
  @Path("/{mid}")
  public Response getModel(@PathParam("mid") int suppliedId) {
    // List<JsonObject> requestedModel = filterByID(suppliedId);
    // if (requestedModel.size() < 1) {
    // return ErrorResponseEnum.NOT_FOUND.getResonse();
    // } else if (requestedModel.size() > 1) {
    // return ErrorResponseEnum.AMBIGUOUS_MATCH.getResonse();
    // } else {
    // return Response.ok(requestedModel.get(0)).build();
    // }
    try {
      JsonObject model = dbc.get(suppliedId).subscribe().asCompletionStage().get();
      if (model == null)
        return ErrorResponseEnum.NOT_FOUND.getResponse();
      else
        return Response.ok(model).build();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return ErrorResponseEnum.DB_REQUEST_FAILED.getResponse();
    }

  }

  @DELETE
  @Path("/{mid}")
  public Response removeModel(@PathParam("mid") int suppliedId) {
    try {
      dbc.remove(suppliedId).subscribe().asCompletionStage().get();
      return Response.ok().build();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return ErrorResponseEnum.DB_REQUEST_FAILED.getResponse();
    }
  }

  // @GET
  // @Path("/list/schema/{parentForm}")
  // public JsonArray listModels(@PathParam("parentForm") String parentForm) {
  //   return new JsonArray(getDataWithModelLinks(parentForm));
  // }

  // private List<JsonObject> filterByID(long filterID) {
  //   return modelList.stream().filter(m -> m.getLong(ID_FIELD).equals(filterID)).collect(Collectors.toList());
  // }

  // private Boolean filterOutID(long filterID) {
  //   modelList = modelList.stream().filter(m -> !m.getLong(ID_FIELD).equals(filterID)).collect(Collectors.toList());
  //   return true;
  // }

  // private List<JsonObject> filterByKey(String key, Object value) {
  //   return modelList.stream().filter(m -> m.getValue(key).equals(value)).collect(Collectors.toList());
  // }

  // private void overrideModel(long replaceId, JsonObject replacement) {
  //   replacement.put(ID_FIELD, replaceId);
  //   modelList = modelList.stream().map(m -> m.getLong(ID_FIELD).equals(replaceId) ? replacement : m).collect(Collectors.toList());
  // }

  // private List<JsonObject> getDataWithModelLinks(String parentForm) {
  //   return filterByKey("#parentForm", parentForm).stream()
  //       .map(m -> m.put("link", linkBuilder(m.getString("#parentForm"), m.getLong(ID_FIELD))))
  //       .collect(Collectors.toList());
  // }

  // private String linkBuilder(String schemaParentForm, long modelId) {
  //   String render = a("link").withTarget("_blank")
  //       // .withHref(
  //       //     "http:/index.html?schema=%d&mid=%d".formatted(schemaRessouce.getIdByParentForm(schemaParentForm), modelId))
  //       .render();
  //   log.info(render);
  //   return render;
  // }
}
