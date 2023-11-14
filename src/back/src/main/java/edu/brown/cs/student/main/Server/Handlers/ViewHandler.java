package edu.brown.cs.student.main.Server.Handlers;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Parsing.ProcessedFile;
import edu.brown.cs.student.main.Server.Server;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler class to handle requests to view a csv */
public class ViewHandler implements Route {

  private ProcessedFile<List<String>> loadedFile;

  /**
   * Accepts a user-provided request. If valid, displays a piece of shared state.
   *
   * @param request user request
   * @param response response
   * @return json object representing the success/failure of the operation
   */
  @Override
  public Object handle(Request request, Response response) {
    // Initialises json adaptor and response datastructure
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    // Obtains shared state
    this.loadedFile = Server.getCSV();

    // Shared state has not been initialised by loading => Error
    if (this.loadedFile == null) {
      assertNull(Server.getCSV());
      assertNull(Server.getCSV().getContent());

      responseMap.put("result", "error_missing_json");
      responseMap.put("details", "loadcsv must be called before viewing");
      return adapter.toJson(responseMap);
    }

    assertNotNull(Server.getCSV());
    assertNotNull(Server.getCSV().getContent());

    // Else, package and return shared state
    responseMap.put("result", "success");
    responseMap.put("data", loadedFile.getContent());
    responseMap.put("headers", loadedFile.getHeaders());

    return adapter.toJson(responseMap);
  }
}
