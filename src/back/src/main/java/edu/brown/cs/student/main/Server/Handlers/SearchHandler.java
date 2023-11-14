package edu.brown.cs.student.main.Server.Handlers;

import static org.testng.AssertJUnit.assertFalse;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Parsing.ProcessedFile;
import edu.brown.cs.student.main.Searching.SearcherAll;
import edu.brown.cs.student.main.Searching.SearcherIdentifier;
import edu.brown.cs.student.main.Server.Server;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Handler class to handle search requests */
public class SearchHandler implements Route {
  private ProcessedFile loadedFile;
  private String reqSearchTerm;
  private String columnIdentifier;
  private Boolean isIndex;
  private List<List<String>> searchResult;

  /**
   * Accepts a user-provided request. If valid, searches a piece of shared state for the requested
   * information.
   *
   * @param request User request
   * @param response response
   * @return a json object representing the reply to the user's search query
   * @throws Exception general exception thrown by handle
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    // Initialises json adaptor and response datastructure
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    this.loadedFile = Server.getCSV();

    // Shared state has not been initialised by loadcsv
    if (this.loadedFile == null) {
      responseMap.put("result", "error_missing_json");
      responseMap.put("details", "loadcsv must be called before searching");
      return adapter.toJson(responseMap);
    }

    // Check param validity
    try {
      this.reqSearchTerm = request.queryParams("searchterm");
      this.columnIdentifier = request.queryParams("columnidentifier");
    } catch (Exception e) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("details", "search term or col identifier is invalid");
      return adapter.toJson(responseMap);
    }

    if (this.reqSearchTerm == null) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("details", "a search term must be provided");
      return adapter.toJson(responseMap);
    }

    if (this.columnIdentifier == null) {
      // No column identifier, search everything
      SearcherAll searchAll = new SearcherAll(this.loadedFile, this.reqSearchTerm);
      List<List<String>> searchResult = searchAll.search();
      responseMap.put("result", "success");
      responseMap.put("data", searchResult);
      responseMap.put("search term", this.reqSearchTerm);
      return adapter.toJson(responseMap);
    }

    // Else, validate that it is possible to search by identifier, and search
    this.isIndex = Boolean.valueOf(request.queryParams("isindex"));
    if (!this.isIndex && (this.loadedFile.getHeaders() == null)) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("details", "file needs to have headers to search by column name");
      return adapter.toJson(responseMap);
    }
    SearcherIdentifier searchCol =
        new SearcherIdentifier(
            this.loadedFile, this.reqSearchTerm, this.columnIdentifier, this.isIndex);
    try {
      searchResult = searchCol.search();
      assert (this.isIndex || this.loadedFile.getHeaders().contains(this.columnIdentifier));
    } catch (IOException e) {

      assertFalse(this.loadedFile.getHeaders().contains(this.columnIdentifier));

      responseMap.put("result", "error_bad_request");
      responseMap.put("details", "column requested is not found");
      return adapter.toJson(responseMap);
    } catch (NumberFormatException e) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("details", "column index must be an integer");
      return adapter.toJson(responseMap);
    }

    responseMap.put("result", "success");
    responseMap.put("data", searchResult);
    responseMap.put("search term", this.reqSearchTerm);
    responseMap.put("col identifier", this.columnIdentifier);
    responseMap.put("is index", this.isIndex);
    return adapter.toJson(responseMap);
  }
}
