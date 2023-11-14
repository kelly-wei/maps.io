package edu.brown.cs.student.main.Server.GeoJson;

import edu.brown.cs.student.main.Server.GeoJson.BoxFilter.GeoFilterDatasource;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class to handle bounding box requests */
public class GeoFilterHandler implements Route {

  private Double minLong;
  private Double maxLong;
  private Double minLat;
  private Double maxLat;
  private static GeoFilterDatasource boundingCache;

  /**
   * Constructor for GeoFilterHandler
   *
   * @param boundingCache datasource for bounding replies
   */
  public GeoFilterHandler(GeoFilterDatasource boundingCache) {
    this.boundingCache = boundingCache;
  }

  /**
   * Method to handle user request and produce filtered results
   *
   * @param request user request
   * @param response user response
   * @return geojson object representing filtered results in bounding box
   * @throws IOException if geojson file cannot be accessed
   * @throws ExecutionException if cache request fails
   */
  @Override
  public Object handle(Request request, Response response) throws IOException, ExecutionException {

    this.minLong = Double.valueOf(request.queryParams("minlong"));
    this.maxLong = Double.valueOf(request.queryParams("maxlong"));
    this.minLat = Double.valueOf(request.queryParams("minlat"));
    this.maxLat = Double.valueOf(request.queryParams("maxlat"));

    return this.boundingCache.getFilteredGeojson(minLong, maxLong, minLat, maxLat).serialize();
  }
}
