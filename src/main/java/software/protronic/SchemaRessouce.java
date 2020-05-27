package software.protronic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import io.vertx.core.json.JsonObject;

@Path("/schema/{id}")
@Produces(MediaType.APPLICATION_JSON)
public class SchemaRessouce {

    @GET
    public JsonObject get(@PathParam("id") int specifiedId) {
      return new JsonObject(SchemaTempEnum.getContentById(specifiedId));
    }

}
