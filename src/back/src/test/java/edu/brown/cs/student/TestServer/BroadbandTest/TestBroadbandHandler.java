package edu.brown.cs.student.TestServer.BroadbandTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.BroadbandData.BroadbandDatasource;
import edu.brown.cs.student.main.Server.BroadbandData.CensusDatasource;
import edu.brown.cs.student.main.Server.DataMediators.DataMediator;
import edu.brown.cs.student.main.Server.DataMediators.SizeCache;
import edu.brown.cs.student.main.Server.Exceptions.BadJsonException;
import edu.brown.cs.student.main.Server.Exceptions.BadRequestException;
import edu.brown.cs.student.main.Server.Exceptions.DataSourceException;
import edu.brown.cs.student.main.Server.Handlers.BroadBandHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

/** Integration tests that involve our API. */
public class TestBroadbandHandler {
  private static JsonAdapter<Map<String, Object>> adapter;
  private static BroadBandHandler handler;

  /** Creates a port and Moshi. */
  @BeforeAll
  public static void setupOnce() {
    Spark.port(100);
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    adapter = moshi.adapter(mapStringObject);
  }

  /** Sets up a MockCensusData for every test and calls the /broadband endpoint. */
  @BeforeEach
  public void setup() {
    LocalDateTime mockTime = LocalDateTime.of(2023, 02, 06, 14, 33, 48, 6400);
    BroadbandDatasource mockedSource = new MockCensusData(mockTime, 60.2);

    DataMediator testCache = new SizeCache(10000, mockedSource);
    this.handler = new BroadBandHandler(testCache);
    Spark.get("/broadband", new BroadBandHandler(testCache));
    Spark.init();
    Spark.awaitInitialization();
  }

  /** Gracefully tears down the port. */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/broadband");
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
   * Tests data from a mocked data source.
   *
   * @throws IOException is a Java method signature.
   */
  @Test
  public void testSendsBack() throws IOException {
    // State and county can be ignored by the mocked data since it's just mocked!
    // (Might have to deal with it when we test the cache)
    HttpURLConnection clientConnection =
        tryRequest("broadband?state=California&&county=Kings%20County,%20California");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    assertEquals(60.2, body.get("data: "));
    assertEquals("California", body.get("state request received"));
    assertEquals(
        LocalDateTime.of(2023, 02, 06, 14, 33, 48, 6400).toString(),
        body.get("date/time request received"));
    assertEquals("Kings County, California", body.get("county request received"));
    assertEquals(5, body.size());

    clientConnection.disconnect();
  }

  /**
   * Tests that the connection is not successful if it calls a random endpoint.
   *
   * @throws IOException is a Java method signature.
   */
  @Test
  public void testInvalidURL() throws IOException {
    HttpURLConnection clientConnection = tryRequest("cit");
    Assert.assertNotEquals(200, clientConnection.getResponseCode());
  }

  /**
   * Tests that a BadRequestException is thrown if missing a parameter.
   *
   * @throws IOException is a Java method signature.
   */
  @Test
  public void testBadRequest() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband?state=California");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request", body.get("result"));
  }

  /**
   * Calls the API census.
   *
   * @throws DataSourceException issue with retrieving data
   * @throws BadRequestException issue with request parameters
   * @throws BadJsonException issue with json conversion
   */
  @Test
  public void testAlamedaCABroadBand()
      throws DataSourceException, BadRequestException, BadJsonException {
    BroadbandDatasource source = new CensusDatasource();
    Map<String, Object> responesMap =
        source.packageResponse("California", "Alameda County, California");
    System.out.println(responesMap);
    Object percent = responesMap.get("data");
    Assert.assertEquals(percent, 89.9);
  }

  /**
   * Calls the Census API on an invalid request.
   *
   * @throws DataSourceException issue with retrieving data
   * @throws BadRequestException issue with request parameters
   * @throws BadJsonException issue with json conversion
   */
  @Test
  public void testBadActualACSBroadBand()
      throws DataSourceException, BadRequestException, BadJsonException {
    BroadbandDatasource source = new CensusDatasource();
    Assert.assertThrows(
        BadRequestException.class,
        () -> {
          source.packageResponse("California", "Alameda County");
        });
  }
}
