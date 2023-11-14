package edu.brown.cs.student.main.Server.GeoJson.BoxFilter;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.GeoJson.GeoRecords.Feature;
import edu.brown.cs.student.main.Server.GeoJson.GeoRecords.FeatureCollection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Direct data access and filtering for area filter */
public class GeoAreaRaw implements GeoFilterDatasource {

  private final String datasource;

  /**
   * Constructor for GeoAreaRaw
   *
   * @param datasource filepath of geojson
   */
  public GeoAreaRaw(String datasource) {
    this.datasource = datasource;
  }

  /** As in interface */
  @Override
  public FeatureCollection getFilteredGeojson(
      double minLong, double maxLong, double minLat, double maxLat) throws IOException {
    Path filePath = Path.of(this.datasource);
    String content = Files.readString(filePath);
    Moshi moshi = new Moshi.Builder().build();
    FeatureCollection featureCollection = moshi.adapter(FeatureCollection.class).fromJson(content);

    List<Feature> filteredFeatures = new ArrayList<Feature>();

    for (Feature oneFeature : featureCollection.features()) {
      // LONGDITUDE-LATITUDE
      Boolean isWithin = true;
      if ((oneFeature.geometry() == null) || (oneFeature.geometry().coordinates().size() == 0)) {
        isWithin = false;
      } else {
        for (List oneCoord : oneFeature.geometry().coordinates().get(0).get(0)) {
          List<Double> newList = oneCoord;
          if (newList.size() != 2) {
            isWithin = false;
          } else {
            double currLong = newList.get(0);
            double currLat = newList.get(1);
            if (!(minLong <= currLong)
                || !(currLong <= maxLong)
                || !(minLat <= currLat)
                || !(currLat <= maxLat)) {
              isWithin = false;
            }
          }
        }
      }
      if (isWithin) {
        filteredFeatures.add(oneFeature);
      }
    }

    FeatureCollection filteredCollection =
        new FeatureCollection(featureCollection.type(), filteredFeatures);

    return filteredCollection;
  }
}
