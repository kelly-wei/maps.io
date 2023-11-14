package edu.brown.cs.student.main.Server.GeoJson;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.GeoJson.GeoRecords.Feature;
import edu.brown.cs.student.main.Server.GeoJson.GeoRecords.FeatureCollection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class to handle area description filter requests */
public class AreaHandler implements Route {

  private final String filepath;
  private String keyword;
  private HashMap<String, FeatureCollection> searchHistory;

  /**
   * Constructor for areahandler
   *
   * @param filepath filepath to geojson file
   */
  public AreaHandler(String filepath) {
    this.searchHistory = new HashMap<>();
    this.filepath = filepath;
  }

  /**
   * Handle method to return filtered area results
   *
   * @param request User request
   * @param response User response
   * @return geojson representing filtered results
   * @throws IOException if geojson file is inaccessible
   */
  @Override
  public Object handle(Request request, Response response) throws IOException {

    Path filePath = Path.of(this.filepath);
    String content = Files.readString(filePath);
    Moshi moshi = new Moshi.Builder().build();

    FeatureCollection featureCollection = moshi.adapter(FeatureCollection.class).fromJson(content);

    this.keyword = request.queryParams("keyword");

    List<Feature> filteredFeatures = new ArrayList<Feature>();

    for (Feature oneFeature : featureCollection.features()) {
      // LONGDITUDE-LATITUDE
      Boolean isWithin = false;
      if ((oneFeature.properties().area_description_data() == null)
          || (oneFeature.properties().area_description_data().size() == 0)) {
        isWithin = false;
      } else {
        for (String onDesc : oneFeature.properties().area_description_data().values()) {
          if (onDesc.toLowerCase().contains(this.keyword.toLowerCase())) {
            isWithin = true;
          }
        }
      }
      if (isWithin) {
        filteredFeatures.add(oneFeature);
      }
    }

    FeatureCollection filteredCollection =
        new FeatureCollection(featureCollection.type(), filteredFeatures);

    if (!(this.searchHistory.containsKey(this.keyword))) {
      if (this.searchHistory.size() > 100) {
        this.searchHistory.clear();
      }
      this.searchHistory.put(this.keyword, filteredCollection);
    }

    return filteredCollection.serialize();
  }

  /**
   * Getter for searchhistory
   *
   * @return the searchhistory
   */
  public HashMap<String, FeatureCollection> getSearchHistory() {
    return this.searchHistory;
  }
}
