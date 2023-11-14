package edu.brown.cs.student.main.Server.Handlers;

import static org.testng.AssertJUnit.assertNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Parsing.Parser;
import edu.brown.cs.student.main.Parsing.ProcessedFile;
import edu.brown.cs.student.main.Server.Server;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler class to handle requests for a csv to be loaded */
public class LoadHandler implements Route {

  private Parser<String> fileParser;
  private String reqFilepath;
  private Boolean reqHeaders;
  private ProcessedFile loadedFile;

  /**
   * Accepts a user-provided request, and loads the csv contents into shared state if valid request.
   *
   * @param request User request
   * @param response Response
   * @return a json object representing the success/failure of the query
   */
  @Override
  public Object handle(Request request, Response response) {
    // Initialises json adaptor and response datastructure
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    // Accesses request parameters
    try {
      this.reqFilepath = request.queryParams("filepath");
      this.reqHeaders = Boolean.valueOf(request.queryParams("headers"));
    } catch (Exception e) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("details", "filepath and/or header parameter is incorrect");
      responseMap.put("filepath", reqFilepath);
      return adapter.toJson(responseMap);
    }

    // Checks that the file requested is within the area of permissions (data folder)
    if (!(this.reqFilepath.startsWith("data/"))) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("details", "file must be in the data folder to be loaded");
      responseMap.put("filepath", reqFilepath);
      return adapter.toJson(responseMap);
    }

    // Parse the input file, store it
    this.loadedFile = null;
    try {
      Reader fileReader = new FileReader(reqFilepath);
      this.fileParser = new Parser<>(fileReader, reqHeaders);
      this.fileParser.packageFile();
      this.loadedFile = fileParser.getFinalFile();
    } catch (Exception e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("details", "error with filename");
      responseMap.put("filepath", reqFilepath);
      return adapter.toJson(responseMap);
    }

    Server.setCSV(this.loadedFile);
    if (this.loadedFile.getHeaders() == null) {
      assertNull(Server.getCSV().getHeaders());
    } else {
      assert (this.loadedFile.getHeaders().equals(Server.getCSV().getHeaders()));
    }

    // Return success response
    responseMap.put("result", "success");
    responseMap.put("filepath", reqFilepath);
    return adapter.toJson(responseMap);
  }
}
