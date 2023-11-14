package edu.brown.cs.student.main.Server.GeoJson.GeoRecords;

import com.squareup.moshi.Moshi;
import java.util.List;

/**
 * Record for geometry of a geojson
 *
 * @param type type of shape
 * @param coordinates coordinates of shape points
 */
public record Geometry(String type, List<List<List<List<Double>>>> coordinates) {

  /**
   * @return this response, serialized as Json
   */
  String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(Geometry.class).toJson(this);
  }
}
