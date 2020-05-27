package software.protronic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.vertx.core.json.JsonObject;

@Path("/schema/{id}")
@Produces(MediaType.APPLICATION_JSON)
public class SchemaResouce {

    @GET
    public JsonObject get() {            
        return new JsonObject("{\"formular\":\"seriennummervergabe\",\"felder\":[{\"name\":\"vorgangsnummern\",\"label\":\"Vorgangsnummern:\",\"feldtyp\":\"enumtextarea\",\"beschreibung\":\"Kommagetrennte Liste aller Vorgangsnummern\",\"platzhalter\":\"Bsp.: 10000, 10001, 10002, ...\"},{\"name\":\"platinennnummern\",\"label\":\"Platinennummern:\",\"feldtyp\":\"enumtextarea\",\"beschreibung\":\"Kommagetrennte Liste aller relevanter Platinennummern\",\"platzhalter\":\"Bsp.: 10000, 10001, 10002, ...\"},{\"name\":\"seriennummer\",\"label\":\"Seriennummer:\",\"feldtyp\":\"text\"},{\"name\":\"microcontrollerid\",\"label\":\"Microcontrollerid:\",\"feldtyp\":\"text\"}]}");
    }
    
}