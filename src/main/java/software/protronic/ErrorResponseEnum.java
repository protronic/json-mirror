package software.protronic;

import javax.ws.rs.core.Response;
import io.vertx.core.json.JsonObject;

public enum ErrorResponseEnum {
  NOT_FOUND ("{\"error\": \"not found\"}", 404),
  AMBIGUOUS_MATCH ("{\"error\": \"ambiguous IDs\"}", 406),
  BAD_REQUEST ("{\"error\": \"bad request\"}", 400);

  private String errorMessage;
  private int errorStatus;

  ErrorResponseEnum(String message, int status) {
    this.errorMessage = message;
    this.errorStatus = status;
  }

  public Response getResonse() {
    return Response
      .status(this.errorStatus)
      .entity(new JsonObject(this.errorMessage))
      .build();
  }
}
