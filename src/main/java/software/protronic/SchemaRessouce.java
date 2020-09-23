package software.protronic;

import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import io.vertx.mutiny.core.Vertx;
import javax.ws.rs.core.Response;
import org.jboss.logging.Logger;
import io.vertx.core.json.JsonObject;

@Path("/schema/{id}")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class SchemaRessouce {

  @Inject
  Vertx vertx;

  private DatabaseConnector dbc;

  @PostConstruct
  void initialize() {
    dbc = new DatabaseConnector(vertx, 8084, "10.19.28.94", "/query?database=formly", "schemas");
    Logger log = Logger.getLogger("quarkus-json-mirror");
    log.info("Quarkus-Json-Mirror was started.");
  }

  @GET
  public Response get(@PathParam("id") int specifiedId) {
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
}
