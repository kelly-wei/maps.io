package edu.brown.cs.student.main.Server.GeoJson.BoxFilter;

import edu.brown.cs.student.main.Server.GeoJson.GeoRecords.FeatureCollection;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/** Interface enforcing proxy pattern for area filtering data */
public interface GeoFilterDatasource {

  /**
   * Obtains filtered geojson based on a bounding box
   *
   * @param minLong minimum longditude
   * @param maxLong maximum longditude
   * @param minLat minimum latitude
   * @param maxLat maximum latitude
   * @return Featurecollection filtered by bounding box
   * @throws ExecutionException If cache access fails
   */
  FeatureCollection getFilteredGeojson(double minLong, double maxLong, double minLat, double maxLat)
      throws IOException, ExecutionException;
}
