package software.protronic;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@Path("/schema/{id}")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class SchemaRessouce {

  private Map<SchemaTempEnum, JsonObject> schemaMap = new HashMap<>();

  @Inject
  Vertx vertx;
  private Logger log = Logger.getLogger(SchemaRessouce.class.getName());

  @GET
  public JsonObject get(@PathParam("id") int specifiedId) {
    return schemaMap.get(SchemaTempEnum.getSchemaById(specifiedId));
  }

  void startup(@Observes StartupEvent event) {
    vertx.fileSystem().readDir("./classes/META-INF/resources/schema", ar -> {
      if (ar.succeeded()) {
        List<String> res = ar.result();
        res.stream().map(File::new).forEach(f -> {
          SchemaTempEnum schema = SchemaTempEnum.getIdByName(f.getName());
          schemaMap.put(schema, vertx.fileSystem().readFileBlocking(f.getAbsolutePath()).toJsonObject());
        });
      } else {
        log.log(Level.SEVERE, "readDir failed", ar.cause());
      }
    });
  }

  public int getIdByParentForm(String parentForm) {
    try {
      return schemaMap.entrySet().stream()
          .filter(e -> e.getValue().getString("formular").equalsIgnoreCase(parentForm))
          .findAny().get().getKey().getSchemaId();
    } catch (IndexOutOfBoundsException exception) {
      return SchemaTempEnum.ERROR.getSchemaId();
    }
  }
}
