package edu.brown.cs.student.main.Server.GeoJson;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.GeoJson.GeoRecords.FeatureCollection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class to handle a direct geojson request */
public class GeojsonHandler implements Route {

  private final String filepath;

  /**
   * Constructor for GeojsonHandler
   *
   * @param filepath filepath to json to produce
   */
  public GeojsonHandler(String filepath) {
    this.filepath = filepath;
  }

  /**
   * Method to handle user request for entire json
   *
   * @param request user request
   * @param response response
   * @return Geojson representing the entire file
   * @throws IOException if there's an issue accessing the geojson file
   */
  @Override
  public Object handle(Request request, Response response) throws IOException {
    Path filePath = Path.of(this.filepath);
    String content = Files.readString(filePath);
    Moshi moshi = new Moshi.Builder().build();

    FeatureCollection featureCollection = moshi.adapter(FeatureCollection.class).fromJson(content);

    return featureCollection.serialize();
  }
}
