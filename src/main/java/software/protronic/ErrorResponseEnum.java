package software.protronic;

import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import io.vertx.core.json.JsonObject;

public enum ErrorResponseEnum {
  NOT_FOUND ("{\"error\": \"not found\"}", 404),
  AMBIGUOUS_MATCH ("{\"error\": \"ambiguous IDs\"}", 406),
  BAD_REQUEST ("{\"error\": \"bad request\"}", 400),
  DB_REQUEST_FAILED ("{\"error\": \"database request failed\"}", 500);

  private static final Logger LOG = Logger.getLogger("quarkus-json-mirror");

  private String errorMessage;
  private int errorStatus;

  ErrorResponseEnum(String message, int status) {
    this.errorMessage = message;
    this.errorStatus = status;
  }

  public Response getResponse() {
    LOG.error(this.errorMessage);
    return Response
      .status(this.errorStatus)
      .entity(new JsonObject(this.errorMessage))
      .build();
  }

  // private JsonObject getMessage(){
  //   return new JsonObject(this.errorMessage);
  // }
}
