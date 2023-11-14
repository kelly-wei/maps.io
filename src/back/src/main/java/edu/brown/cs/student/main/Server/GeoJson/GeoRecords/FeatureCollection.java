package edu.brown.cs.student.main.Server.GeoJson.GeoRecords;

import com.squareup.moshi.Moshi;
import java.util.List;

/**
 * Record for featurecollection of geojson
 *
 * @param type type (featurecollection)
 * @param features list of features in the geojson
 */
public record FeatureCollection(String type, List<Feature> features) {

  /**
   * @return this response, serialized as Json
   */
  public String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(FeatureCollection.class).toJson(this);
  }
}
