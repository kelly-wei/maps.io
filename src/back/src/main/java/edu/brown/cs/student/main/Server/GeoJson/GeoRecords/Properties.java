package edu.brown.cs.student.main.Server.GeoJson.GeoRecords;

import com.squareup.moshi.Moshi;
import java.util.Map;

/**
 * Record for properties in a geojson
 *
 * @param state state
 * @param city city
 * @param name name of the area
 * @param holc_id id of property
 * @param holc_grade grade of property
 * @param neighborhood_id id of neighbourhood
 * @param area_description_data area description
 */
public record Properties(
    String state,
    String city,
    String name,
    String holc_id,
    String holc_grade,
    Integer neighborhood_id,
    Map<String, String> area_description_data) {

  /**
   * @return this response, serialized as Json
   */
  String serialize() {
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(Properties.class).toJson(this);
  }
}
