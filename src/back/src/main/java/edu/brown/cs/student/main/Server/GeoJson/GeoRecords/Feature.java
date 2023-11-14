package edu.brown.cs.student.main.Server.GeoJson.GeoRecords;

import com.squareup.moshi.Moshi;

/**
 * Record for feature in geojson
 *
 * @param type type (feature)
 * @param geometry coordinates and shape of polygon
 * @param properties Properties of the area
 */
public record Feature(String type, Geometry geometry, Properties properties) {
  /**
   * @return this response, serialized as Json
   */
  String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(Feature.class).toJson(this);
  }
}
