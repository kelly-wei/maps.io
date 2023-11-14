package edu.brown.cs.student.TestServer.GeoTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.GeoJson.AreaHandler;
import edu.brown.cs.student.main.Server.GeoJson.BoxFilter.GeoFilterDatasource;
import edu.brown.cs.student.main.Server.GeoJson.BoxFilter.GeoSizeCache;
import edu.brown.cs.student.main.Server.GeoJson.GeoFilterHandler;
import edu.brown.cs.student.main.Server.GeoJson.GeoRecords.Feature;
import edu.brown.cs.student.main.Server.GeoJson.GeoRecords.FeatureCollection;
import edu.brown.cs.student.main.Server.GeoJson.GeojsonHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import okio.Buffer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestGeoHandlers {
  private static JsonAdapter<FeatureCollection> adapter;
  private static AreaHandler areaHandler;

  /** Creates a port and Moshi. */
  @BeforeAll
  public static void setupOnce() {
    Spark.port(100);
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(FeatureCollection.class);
  }

  /** Sets up a MockCensusData for every test and calls the /broadband endpoint. */
  @BeforeEach
  public void setup() {
    String mockPath = "data/TESTgeoData.geojson";

    Spark.get("geojson", new GeojsonHandler(mockPath));
    this.areaHandler = new AreaHandler(mockPath);
    Spark.get("filterarea", this.areaHandler);

    GeoFilterDatasource boundingCache = new GeoSizeCache(100, mockPath);
    Spark.get("filtergeo", new GeoFilterHandler(boundingCache));
    Spark.init();
    Spark.awaitInitialization();
  }

  /** Gracefully tears down the port. */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on endpoints
    Spark.unmap("/geojson");
    Spark.unmap("/filterarea");
    Spark.unmap("/filtergeo");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Shuts down thread.
   *
   * @throws InterruptedException if interrupted
   */
  @AfterAll
  public static void shutdown() throws InterruptedException {
    Spark.stop();
    Thread.sleep(3000);
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint (NOTE: this would be better if it had more
   *     structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestMethod("GET");
    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Basic endpoint tests
   *
   * @throws IOException
   */
  @Test
  public void testendpoint() throws IOException {
    HttpURLConnection clientConnection3 =
        tryRequest("filtergeo?minlong=0&&maxlong=100&&minlat=28&&maxlat=33");
    assertEquals(200, clientConnection3.getResponseCode());

    FeatureCollection body3 =
        adapter.fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));

    assertEquals(1, body3.features().size());
    assertEquals("FeatureCollection", body3.type());
    assertEquals("Feature", body3.features().get(0).type());
    assertEquals(
        "Feature[type=Feature, geometry=Geometry[type=MultiPolygon, "
            + "coordinates=[[[[4.232, 29.12312], [5.21312, 30.2312], [6.12312, 28.12312]]]]], "
            + "properties=Properties[state=AL, city=MyCity, name=Random Spot, holc_id=A1, holc_grade=A, "
            + "neighborhood_id=244, area_description_data={5=policy prayer help me}]]",
        body3.features().get(0).toString());

    HttpURLConnection clientConnection = tryRequest("geojson");
    assertEquals(200, clientConnection.getResponseCode());

    FeatureCollection body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals(2, body.features().size());
    assertEquals("FeatureCollection", body.type());
    assertEquals("Feature", body.features().get(1).type());
    assertEquals(
        "Feature[type=Feature, geometry=Geometry[type=MultiPolygon, "
            + "coordinates=[[[[4.232, 29.12312], [5.21312, 30.2312], [6.12312, 28.12312]]]]], "
            + "properties=Properties[state=AL, city=MyCity, name=Random Spot, holc_id=A1, holc_grade=A, "
            + "neighborhood_id=244, area_description_data={5=policy prayer help me}]]",
        body.features().get(1).toString());

    HttpURLConnection clientConnection2 =
        tryRequest("filtergeo?minlong=0&&maxlong=100&&minlat=28&&maxlat=33");
    assertEquals(200, clientConnection2.getResponseCode());

    FeatureCollection body2 =
        adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    assertEquals(1, body2.features().size());
    assertEquals("FeatureCollection", body2.type());
    assertEquals("Feature", body2.features().get(0).type());
    assertEquals(
        "Feature[type=Feature, geometry=Geometry[type=MultiPolygon, "
            + "coordinates=[[[[4.232, 29.12312], [5.21312, 30.2312], [6.12312, 28.12312]]]]], "
            + "properties=Properties[state=AL, city=MyCity, name=Random Spot, holc_id=A1, holc_grade=A, "
            + "neighborhood_id=244, area_description_data={5=policy prayer help me}]]",
        body2.features().get(0).toString());

    HttpURLConnection clientConnection4 = tryRequest("filterarea?keyword=HelpMe");
    assertEquals(200, clientConnection4.getResponseCode());

    FeatureCollection body4 =
        adapter.fromJson(new Buffer().readFrom(clientConnection4.getInputStream()));

    assertEquals(0, body4.features().size());
    assertEquals("FeatureCollection", body4.type());

    HttpURLConnection clientConnection5 = tryRequest("filterarea?keyword=policy");
    assertEquals(200, clientConnection5.getResponseCode());

    FeatureCollection body5 =
        adapter.fromJson(new Buffer().readFrom(clientConnection5.getInputStream()));

    assertEquals(2, body5.features().size());
    assertEquals("FeatureCollection", body5.type());
    assertEquals(
        "Feature[type=Feature, geometry=Geometry[type=MultiPolygon, "
            + "coordinates=[[[[4.232, 29.12312], [5.21312, 30.2312], [6.12312, 28.12312]]]]], "
            + "properties=Properties[state=AL, city=MyCity, name=Random Spot, holc_id=A1, holc_grade=A, "
            + "neighborhood_id=244, area_description_data={5=policy prayer help me}]]",
        body5.features().get(1).toString());

    clientConnection2.disconnect();
    clientConnection.disconnect();
    clientConnection3.disconnect();
    clientConnection4.disconnect();
    clientConnection5.disconnect();
  }

  /**
   * Persistence and unit test
   *
   * @throws IOException
   */
  @Test
  public void testPersistence() throws IOException {
    assertEquals(0, areaHandler.getSearchHistory().size());

    HttpURLConnection clientConnection4 = tryRequest("filterarea?keyword=HelpMe");
    assertEquals(200, clientConnection4.getResponseCode());

    FeatureCollection body4 =
        adapter.fromJson(new Buffer().readFrom(clientConnection4.getInputStream()));

    assertEquals(0, body4.features().size());
    assertEquals("FeatureCollection", body4.type());
    assertEquals(1, areaHandler.getSearchHistory().size());
    assertTrue(areaHandler.getSearchHistory().containsKey("HelpMe"));

    HttpURLConnection clientConnection5 = tryRequest("filterarea?keyword=policy");
    assertEquals(200, clientConnection5.getResponseCode());

    FeatureCollection body5 =
        adapter.fromJson(new Buffer().readFrom(clientConnection5.getInputStream()));

    assertEquals(2, body5.features().size());
    assertEquals("FeatureCollection", body5.type());
    assertEquals(
        "Feature[type=Feature, geometry=Geometry[type=MultiPolygon, "
            + "coordinates=[[[[4.232, 29.12312], [5.21312, 30.2312], [6.12312, 28.12312]]]]], "
            + "properties=Properties[state=AL, city=MyCity, name=Random Spot, holc_id=A1, holc_grade=A, "
            + "neighborhood_id=244, area_description_data={5=policy prayer help me}]]",
        body5.features().get(1).toString());
    assertEquals(2, areaHandler.getSearchHistory().size());
    assertTrue(areaHandler.getSearchHistory().containsKey("HelpMe"));
    assertTrue(areaHandler.getSearchHistory().containsKey("policy"));

    clientConnection4.disconnect();
    clientConnection5.disconnect();
  }

  /**
   * unit test for bounding filtering
   *
   * @throws IOException
   */
  @Test
  public void testBounding() throws IOException, ExecutionException {
    GeoSizeCache testCache = new GeoSizeCache(5, "data/TESTgeoData.geojson");

    assertEquals(1, testCache.getFilteredGeojson(0, 100, 28, 33).features().size());
    assertEquals(2, testCache.getFilteredGeojson(-10000, 1000, -10000, 100).features().size());
  }

  /**
   * Method to get a random longditude within -180 to 0 (general range of geojson)
   *
   * @return a double representing longditude
   */
  private double getLong() {
    return ThreadLocalRandom.current().nextDouble(-180, 0);
  }

  /**
   * Method to get a random latitude within -180 to 0 (general range of geojson)
   *
   * @return a double representing latitude
   */
  private double getLat() {
    return ThreadLocalRandom.current().nextDouble(0, 90);
  }

  /**
   * Fuzz and PBT for bounding Note that there is no requirement that the minimum value < maximum
   * value – the method should still exit gracefully if this isn't true
   *
   * @throws IOException
   */
  @Test
  public void testRandBounding() throws IOException, ExecutionException {
    GeoSizeCache testCache = new GeoSizeCache(5, "data/geoData.geojson");

    for (int counter = 0; counter < 50; counter++) {
      double minLong = getLong();
      double maxLong = getLong();
      double minLat = getLat();
      double maxLat = getLat();

      FeatureCollection retVal = testCache.getFilteredGeojson(minLong, maxLong, minLat, maxLat);

      assertEquals("FeatureCollection", retVal.type());

      for (Feature oneFeat : retVal.features()) {
        for (List oneCoord : oneFeat.geometry().coordinates().get(0).get(0)) {
          List<Double> newList = oneCoord;
          assert (newList.size() == 2);
          assert (minLong <= newList.get(0));
          assert (maxLong >= newList.get(0));
          assert (minLat <= newList.get(1));
          assert (maxLat >= newList.get(1));
        }
      }
    }
  }
}
