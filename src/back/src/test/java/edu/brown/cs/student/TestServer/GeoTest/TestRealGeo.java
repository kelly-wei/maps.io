package edu.brown.cs.student.TestServer.GeoTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.GeoJson.AreaHandler;
import edu.brown.cs.student.main.Server.GeoJson.BoxFilter.GeoFilterDatasource;
import edu.brown.cs.student.main.Server.GeoJson.BoxFilter.GeoSizeCache;
import edu.brown.cs.student.main.Server.GeoJson.GeoFilterHandler;
import edu.brown.cs.student.main.Server.GeoJson.GeoRecords.FeatureCollection;
import edu.brown.cs.student.main.Server.GeoJson.GeojsonHandler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import okio.Buffer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestRealGeo {
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
    String mockPath = "data/geoData.geojson";

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
   * @throws IOException is a Java method signature.
   */
  @Test
  public void testendpoint() throws IOException {
    HttpURLConnection clientConnection3 =
        tryRequest("filtergeo?minlong=-89&&maxlong=-80&&minlat=33&&maxlat=34");
    assertEquals(200, clientConnection3.getResponseCode());

    FeatureCollection body3 =
        adapter.fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));

    assertEquals(214, body3.features().size());
    assertEquals("FeatureCollection", body3.type());
    assertEquals("Feature", body3.features().get(0).type());

    HttpURLConnection clientConnection = tryRequest("geojson");
    assertEquals(200, clientConnection.getResponseCode());

    FeatureCollection body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals(8878, body.features().size());
    assertEquals("FeatureCollection", body.type());
    assertEquals("Feature", body.features().get(1).type());

    HttpURLConnection clientConnection2 =
        tryRequest("filtergeo?minlong=0&&maxlong=100&&minlat=28&&maxlat=33");
    assertEquals(200, clientConnection2.getResponseCode());

    FeatureCollection body2 =
        adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    assertEquals(0, body2.features().size());
    assertEquals("FeatureCollection", body2.type());

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

    assertEquals(66, body5.features().size());
    assertEquals("FeatureCollection", body5.type());

    clientConnection2.disconnect();
    clientConnection.disconnect();
    clientConnection3.disconnect();
    clientConnection4.disconnect();
    clientConnection5.disconnect();
  }
}
