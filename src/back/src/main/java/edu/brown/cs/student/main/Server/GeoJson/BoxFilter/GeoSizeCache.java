package edu.brown.cs.student.main.Server.GeoJson.BoxFilter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.Server.Exceptions.BadJsonException;
import edu.brown.cs.student.main.Server.Exceptions.BadRequestException;
import edu.brown.cs.student.main.Server.Exceptions.DataSourceException;
import edu.brown.cs.student.main.Server.GeoJson.GeoRecords.FeatureCollection;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Cache for area filtered data */
public class GeoSizeCache implements GeoFilterDatasource {
  private final LoadingCache<List<Double>, FeatureCollection> geoCache;
  private final String datasource;

  /**
   * Constructor for cache
   *
   * @param maxSize max size before eviction
   * @param datasource path to geojson
   */
  public GeoSizeCache(Integer maxSize, String datasource) {
    this.datasource = datasource;
    LoadingCache<List<Double>, FeatureCollection> geoCache =
        CacheBuilder.newBuilder()
            .maximumSize(maxSize)
            .build(
                new CacheLoader<>() {

                  public FeatureCollection load(List<Double> boundingReq)
                      throws DataSourceException, BadJsonException, BadRequestException,
                          IOException {
                    try {
                      FeatureCollection newGeo = null;
                      newGeo = makeCall(boundingReq);
                      return newGeo;
                    } catch (IOException e) {
                      throw e;
                    }
                  }
                });
    this.geoCache = geoCache;
  }

  /**
   * Accesses GeoAreaRaw for direct data on request
   *
   * @param boundingReq bounding box
   * @return filtered FeatureCollection
   * @throws IOException if geojson path is inaccessible
   */
  private FeatureCollection makeCall(List<Double> boundingReq) throws IOException {
    GeoAreaRaw rawDatasource = new GeoAreaRaw(this.datasource);
    try {
      return rawDatasource.getFilteredGeojson(
          boundingReq.get(0), boundingReq.get(1), boundingReq.get(2), boundingReq.get(3));
    } catch (IOException e) {
      throw e;
    }
  }

  /** As in interface */
  @Override
  public FeatureCollection getFilteredGeojson(
      double minLong, double maxLong, double minLat, double maxLat) throws ExecutionException {
    List<Double> reqList = Arrays.asList(minLong, maxLong, minLat, maxLat);
    return geoCache.get(reqList);
  }
}
