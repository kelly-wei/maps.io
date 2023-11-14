package edu.brown.cs.student.main.Server;

import static spark.Spark.after;

import edu.brown.cs.student.main.Parsing.ProcessedFile;
import edu.brown.cs.student.main.Server.BroadbandData.BroadbandDatasource;
import edu.brown.cs.student.main.Server.BroadbandData.CensusDatasource;
import edu.brown.cs.student.main.Server.DataMediators.DataMediator;
import edu.brown.cs.student.main.Server.DataMediators.SizeCache;
import edu.brown.cs.student.main.Server.GeoJson.AreaHandler;
import edu.brown.cs.student.main.Server.GeoJson.BoxFilter.GeoFilterDatasource;
import edu.brown.cs.student.main.Server.GeoJson.BoxFilter.GeoSizeCache;
import edu.brown.cs.student.main.Server.GeoJson.GeoFilterHandler;
import edu.brown.cs.student.main.Server.GeoJson.GeojsonHandler;
import edu.brown.cs.student.main.Server.Handlers.BroadBandHandler;
import edu.brown.cs.student.main.Server.Handlers.LoadHandler;
import edu.brown.cs.student.main.Server.Handlers.SearchHandler;
import edu.brown.cs.student.main.Server.Handlers.ViewHandler;
import spark.Spark;

/** Server class to accept user requests and pass them to the appropriate user */
public class Server {
  private static ProcessedFile csvFile;

  /**
   * Main class to handle user input
   *
   * @param args No args should be provided directly
   */
  public static void main(String[] args) {
    // Start a port, perform setup
    int port = 323;
    Spark.port(port);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Initialise csv-related endpoints with appropriate handlers
    Spark.get("loadcsv", new LoadHandler());
    Spark.get("viewcsv", new ViewHandler());
    Spark.get("searchcsv", new SearchHandler());
    Spark.get("geojson", new GeojsonHandler("data/geoData.geojson"));
    Spark.get("filterarea", new AreaHandler("data/geoData.geojson"));

    GeoFilterDatasource boundingCache = new GeoSizeCache(100, "data/geoData.geojson");
    Spark.get("filtergeo", new GeoFilterHandler(boundingCache));

    // Specify datasource, caching information, and set up broadband endpoint
    // Currently configuring with a specific cache and datasource that the user can change
    BroadbandDatasource externalData = new CensusDatasource();
    DataMediator broadbandCache = new SizeCache(1000, externalData);
    Spark.get("broadband", new BroadBandHandler(broadbandCache));

    // Wait for initialisation
    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }

  /**
   * Setter for shared state
   *
   * @param parsedFile new value to set the shared state to
   */
  public static void setCSV(ProcessedFile parsedFile) {
    csvFile = parsedFile;
  }

  /**
   * Getter for shared state
   *
   * @return ProcessedFile representing the shared state
   */
  public static ProcessedFile getCSV() {
    return csvFile;
  }
}
