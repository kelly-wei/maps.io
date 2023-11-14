package edu.brown.cs.student.main.Server.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.DataMediators.DataMediator;
import edu.brown.cs.student.main.Server.Exceptions.BadJsonException;
import edu.brown.cs.student.main.Server.Exceptions.BadRequestException;
import edu.brown.cs.student.main.Server.Exceptions.DataSourceException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Class that handles request from /broadband endpoint. Implements the Route interface in order to
 * retrieve endpoints.
 */
public class BroadBandHandler implements Route {

  private DataMediator apiCache;

  /**
   * Constructor takes in type DataMediator so it can take in any cache.
   *
   * @param apiCache is a generic cache as long as it implements DataMediator.
   */
  public BroadBandHandler(DataMediator apiCache) {
    this.apiCache = apiCache;
  }

  /**
   * Method that handles the /broadband endpoint. This method will catch all custom exceptions and
   * print an informative message to the user. Returns a Hashmap that contains the data if the query
   * was successful. Otherwise, it returns a Hashmap with the specific error and details about the
   * error.
   *
   * @param request is the request. Implemented as part of interface signature.
   * @param response is the response. Implemented as part of interface signature.
   * @return an Object that will be a Hashmap that contains the data or an informative message if
   *     there was an error.
   * @throws Exception if there is an issue with the data and prints informative message to user.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String state;
    String county;
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject =
        Types.newParameterizedType(Map.class, String.class, Object.class, LocalDateTime.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    try {
      state = request.queryParams("state");
      county = request.queryParams("county");
      if (state == null || county == null) {
        throw new BadRequestException();
      }
      Map<String, Object> responseMap = this.apiCache.getResponse(state, county);
      responseMap.put("result", "success");
      responseMap.put("state request received", state);
      responseMap.put("county request received", county);
      return adapter.toJson(responseMap);
    } catch (DataSourceException e) { // catches custom exception and prints helpful message
      Map badData = new HashMap();
      badData.put("result", "error_datasource");
      badData.put("details", "issue with data source");
      return adapter.toJson(badData);
    } catch (BadRequestException e) { // catches custom exception and prints helpful message
      Map badRequest = new HashMap<>();
      badRequest.put("result", "error_bad_request");
      badRequest.put("details", "issues with request. check that your input is correct");
      return adapter.toJson(badRequest);
    } catch (BadJsonException e) { // catches custom exception and prints helpful message
      Map badJson = new HashMap<>();
      badJson.put("result", "error_bad_json");
      badJson.put("details", "issue with converting the json object");
    } catch (ExecutionException e) { // catches exception and prints helpful message
      Map badRequest = new HashMap<>();
      badRequest.put("result", "error_bad_request");
      badRequest.put("details", e.getMessage());
      return adapter.toJson(badRequest);
    }
    return new HashMap<>();
  }
}
