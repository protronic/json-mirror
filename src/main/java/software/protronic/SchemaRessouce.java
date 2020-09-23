package software.protronic;

import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import io.quarkus.runtime.StartupEvent;
import io.vertx.mutiny.core.Vertx;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import io.vertx.core.json.JsonObject;

@Path("/schema/{id}")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class SchemaRessouce {

  // private Map<SchemaTempEnum, JsonObject> schemaMap = new HashMap<>();

  @Inject
  Vertx vertx;

  private DatabaseConnector dbc;

  @PostConstruct
  void initialize() {
    dbc = new DatabaseConnector(vertx, 8084, "10.19.28.94", "/query?database=formly", "schemas");
    Logger log = Logger.getLogger("quarkus-json-mirror");
    log.info("Quarkus-Json-Mirror was started.");
  }

  // private Logger log = Logger.getLogger(SchemaRessouce.class.getName());

  @GET
  public Response get(@PathParam("id") int specifiedId) {
    // return schemaMap.get(SchemaTempEnum.getSchemaById(specifiedId));

    try {
      JsonObject schema = dbc.get(specifiedId).subscribe().asCompletionStage().get();
      if (schema == null)
        return ErrorResponseEnum.NOT_FOUND.getResponse();
      else
        return Response.ok(schema).build();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return ErrorResponseEnum.DB_REQUEST_FAILED.getResponse();
    }
  }

  void startup(@Observes StartupEvent event) {
    // vertx.fileSystem().readDir("./classes/META-INF/resources/schema", ar -> {
    // if (ar.succeeded()) {
    // List<String> res = ar.result();
    // res.stream().map(File::new).forEach(f -> {
    // SchemaTempEnum schema = SchemaTempEnum.getIdByName(f.getName());
    // schemaMap.put(schema,
    // vertx.fileSystem().readFileBlocking(f.getAbsolutePath()).toJsonObject());
    // });
    // } else {
    // log.log(Level.SEVERE, "readDir failed", ar.cause());
    // }
    // });
  }

  public int getIdByParentForm(String parentForm) {
    // try {
    // return schemaMap.entrySet().stream()
    // .filter(e -> e.getValue().getString("formular").equalsIgnoreCase(parentForm))
    // .findAny().get().getKey().getSchemaId();
    // } catch (IndexOutOfBoundsException exception) {
    // return SchemaTempEnum.ERROR.getSchemaId();
    // }
    // try {
    //   JsonArray schemaList = dbc.list().subscribe().asCompletionStage().get();
    //   schemaList.getList().stream().filter(schema -> ((JsonObject) schema).getString("formular").equalsIgnoreCase(parentForm)).toArray()[0];
    // } catch (InterruptedException | ExecutionException e) {
    //   // TODO Auto-generated catch block
    //   e.printStackTrace();
    //   return null;
    // }
    return 0;
  }
}
