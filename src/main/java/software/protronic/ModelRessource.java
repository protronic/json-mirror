package software.protronic;

import java.util.HashMap;
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

  @Inject
  Vertx vertx;

  private DatabaseConnector dbc;
  private DatabaseConnector dbca;
  private HashMap<String, DatabaseConnector> dbcs;

  @PostConstruct
  void initialize() {
    dbcs = new HashMap<String, DatabaseConnector>();
    dbc = new DatabaseConnector(vertx, 8084, "10.19.28.29", "/query?database=formly", "model");
    dbcs.put("model", dbc);
    dbca = new DatabaseConnector(vertx, 8084, "10.19.28.29", "/query?database=formly", "modelArchiv");
    dbcs.put("modelArchiv", dbca);
  }

  private String initializeTableIfNotExists(String table){
    String tableName = "model" + table;
    if(!dbcs.containsKey(tableName)){
      DatabaseConnector newDbc = new DatabaseConnector(vertx, 8084, "10.19.28.29", "/query?database=formly", tableName);
      dbcs.put(tableName, newDbc);
    } 
    return tableName;
  }

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
  public Response add(JsonObject obj, @QueryParam("table") String altTable) {
    try {
      JsonObject newId = null;
      if(altTable == null){
        newId = dbc.add(obj).subscribe().asCompletionStage().get();
      } else {
        String tableName = initializeTableIfNotExists(altTable);
        newId = dbcs.get(tableName).add(obj).subscribe().asCompletionStage().get();
      }      
      if (newId == null)
        return ErrorResponseEnum.BAD_REQUEST.getResponse();
      else
        return Response.ok(newId).build();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return ErrorResponseEnum.DB_REQUEST_FAILED.getResponse();
    }
  }

  @POST
  @Path("/{mid}")
  public Response replace(JsonObject obj, @PathParam("mid") int suppliedId, @QueryParam("table") String altTable) {
    try {
      JsonObject model = null;
      if(altTable == null){
        model = dbc.set(suppliedId, obj).subscribe().asCompletionStage().get();
      } else {
        String tableName = initializeTableIfNotExists(altTable);
        model = dbcs.get(tableName).set(suppliedId, obj).subscribe().asCompletionStage().get();
      }      
      return Response.ok(model).build();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return ErrorResponseEnum.DB_REQUEST_FAILED.getResponse();
    }
  }

  @GET
  @Path("/{mid}")
  public Response getModel(@PathParam("mid") int suppliedId, @QueryParam("table") String altTable) {
    try {
      // System.out.println(altTable);
      JsonObject model = null;
      if(altTable == null){
        model = dbc.get(suppliedId).subscribe().asCompletionStage().get();
      } else {
        String tableName = initializeTableIfNotExists(altTable);
        model = dbcs.get(tableName).get(suppliedId).subscribe().asCompletionStage().get();
      }
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
  public Response removeModel(@PathParam("mid") int suppliedId, @QueryParam("table") String altTable) {
    try {
      if(altTable == null){
        dbc.remove(suppliedId).subscribe().asCompletionStage().get();
      } else {
        String tableName = initializeTableIfNotExists(altTable);
        dbcs.get(tableName).remove(suppliedId).subscribe().asCompletionStage().get();
      }
      return Response.ok().build();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return ErrorResponseEnum.DB_REQUEST_FAILED.getResponse();
    }
  }
}
