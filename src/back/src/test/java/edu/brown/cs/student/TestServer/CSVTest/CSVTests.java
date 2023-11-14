package edu.brown.cs.student.TestServer.CSVTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.Handlers.LoadHandler;
import edu.brown.cs.student.main.Server.Handlers.SearchHandler;
import edu.brown.cs.student.main.Server.Handlers.ViewHandler;
import edu.brown.cs.student.main.Server.Server;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** Testing class for loading, viewing, and searching a CSV */
public class CSVTests {
  private static JsonAdapter<Map<String, Object>> adapter;

  /** Set up port and json adaptor */
  @BeforeAll
  public static void setupOnce() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    adapter = moshi.adapter(mapStringObject);
  }

  /** Set up endpoints */
  @BeforeEach
  public void setup() {
    Spark.get("loadcsv", new LoadHandler());
    Spark.get("viewcsv", new ViewHandler());
    Spark.get("searchcsv", new SearchHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  /** Unmap endpoints, wait for stop */
  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening
    Spark.unmap("/loadcsv");
    Spark.unmap("/viewcsv");
    Spark.unmap("/searchcsv");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Stop server, with buffer time built in to prevent clashes with other testing servers
   *
   * @throws InterruptedException
   */
  @AfterAll
  public static void shutdown() throws InterruptedException {
    Spark.stop();
    Thread.sleep(3000);
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint
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
   * Test to ensure that file with headers can be loaded
   *
   * @throws IOException
   */
  @Test
  public void loadHeader() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/census/dol_ri_earnings_disparity.csv&&headers=true");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    assertEquals("data/census/dol_ri_earnings_disparity.csv", body.get("filepath"));
    assertEquals(2, body.size());

    List earningsHeaderList =
        List.of(
            "State",
            "Data Type",
            "Average Weekly Earnings",
            "Number of Workers",
            "Earnings Disparity",
            "Employed Percent");
    assertEquals(earningsHeaderList, Server.getCSV().getHeaders());
    assertEquals(6, Server.getCSV().getContent().size());
    assertEquals(
        "[[RI, White,  $1,058.47 , 395773.6521, $1.00, 75%], "
            + "[RI, Black, $770.26, 30424.80376, $0.73, 6%], [RI, Native American/American Indian,"
            + " $471.07, 2315.505646, $0.45, 0%], [RI, Asian-Pacific Islander,  $1,080.09 , "
            + "18956.71657, $1.02, 4%], [RI, Hispanic/Latino, $673.14, 74596.18851, $0.64, 14%], "
            + "[RI, Multiracial, $971.89, 8883.049171, $0.92, 2%]]",
        Server.getCSV().getContent().toString());
  }

  /**
   * Test to ensure file without headers can be loadedf
   *
   * @throws IOException
   */
  @Test
  public void loadNoHeader() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/census/dol_ri_earnings_disparity.csv&&headers=false");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    assertEquals("data/census/dol_ri_earnings_disparity.csv", body.get("filepath"));
    assertEquals(2, body.size());

    assertEquals(null, Server.getCSV().getHeaders());
    assertEquals(7, Server.getCSV().getContent().size());
  }

  /**
   * Test to ensure that if load is called with headers, then without headers, the headers from the
   * first load do not persist
   *
   * @throws IOException
   */
  @Test
  public void headerLoadSuccession() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/census/dol_ri_earnings_disparity.csv&&headers=true");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    assertEquals("data/census/dol_ri_earnings_disparity.csv", body.get("filepath"));
    assertEquals(2, body.size());

    List earningsHeaderList =
        List.of(
            "State",
            "Data Type",
            "Average Weekly Earnings",
            "Number of Workers",
            "Earnings Disparity",
            "Employed Percent");
    assertEquals(earningsHeaderList, Server.getCSV().getHeaders());
    assertEquals(6, Server.getCSV().getContent().size());

    HttpURLConnection clientConnection2 =
        tryRequest("loadcsv?filepath=data/census/dol_ri_earnings_disparity.csv&&headers=false");
    assertEquals(200, clientConnection2.getResponseCode());

    Map<String, Object> body2 =
        adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    assertEquals("success", body2.get("result"));
    assertEquals("data/census/dol_ri_earnings_disparity.csv", body2.get("filepath"));
    assertEquals(2, body2.size());

    assertEquals(null, Server.getCSV().getHeaders());
    assertEquals(7, Server.getCSV().getContent().size());
  }

  /**
   * Test to ensure that if load is called without headers, then with headers, the headers are
   * present in the second load but not the first
   *
   * @throws IOException
   */
  @Test
  public void revHeaderLoadSuccession() throws IOException {
    HttpURLConnection clientConnection2 =
        tryRequest("loadcsv?filepath=data/census/dol_ri_earnings_disparity.csv&&headers=false");
    assertEquals(200, clientConnection2.getResponseCode());

    Map<String, Object> body2 =
        adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    assertEquals("success", body2.get("result"));
    assertEquals("data/census/dol_ri_earnings_disparity.csv", body2.get("filepath"));
    assertEquals(2, body2.size());

    assertEquals(null, Server.getCSV().getHeaders());
    assertEquals(7, Server.getCSV().getContent().size());

    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/census/dol_ri_earnings_disparity.csv&&headers=true");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    assertEquals("data/census/dol_ri_earnings_disparity.csv", body.get("filepath"));
    assertEquals(2, body.size());

    List earningsHeaderList =
        List.of(
            "State",
            "Data Type",
            "Average Weekly Earnings",
            "Number of Workers",
            "Earnings Disparity",
            "Employed Percent");
    assertEquals(earningsHeaderList, Server.getCSV().getHeaders());
    assertEquals(6, Server.getCSV().getContent().size());
  }

  /**
   * Test to ensure that different files can be loaded
   *
   * @throws IOException
   */
  @Test
  public void loadMany() throws IOException {
    HttpURLConnection clientConnection2 =
        tryRequest("loadcsv?filepath=data/census/dol_ri_earnings_disparity.csv&&headers=false");
    assertEquals(200, clientConnection2.getResponseCode());

    Map<String, Object> body2 =
        adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    assertEquals("success", body2.get("result"));
    assertEquals("data/census/dol_ri_earnings_disparity.csv", body2.get("filepath"));
    assertEquals(2, body2.size());

    assertEquals(null, Server.getCSV().getHeaders());
    assertEquals(7, Server.getCSV().getContent().size());

    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/census/postsecondary_education.csv&&headers=true");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    assertEquals("data/census/postsecondary_education.csv", body.get("filepath"));
    assertEquals(2, body.size());

    assertEquals(10, Server.getCSV().getHeaders().size());
    assertEquals(16, Server.getCSV().getContent().size());
  }

  /**
   * Test to ensure that an error response is returned in case of an invalid filename
   *
   * @throws IOException
   */
  @Test
  public void badFilepath() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=data/ewnsus/dol_ri_earnings_disparity.csv&&headers=false");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_datasource", body.get("result"));
    assertEquals("data/ewnsus/dol_ri_earnings_disparity.csv", body.get("filepath"));
    assertEquals("error with filename", body.get("details"));
    assertEquals(3, body.size());
  }

  /**
   * Test to ensure that an error response is returned in case of a file not within searchable data
   * folder.
   *
   * @throws IOException
   */
  @Test
  public void noPermissionFile() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest(
            "loadcsv?filepath=src/main/java/edu/brown/cs/student/main/Server/Server.java&&headers=false");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", body.get("result"));
    assertEquals(
        "src/main/java/edu/brown/cs/student/main/Server/Server.java", body.get("filepath"));
    assertEquals("file must be in the data folder to be loaded", body.get("details"));
    assertEquals(3, body.size());
  }

  /**
   * Test to check viewing after load
   *
   * @throws IOException
   */
  @Test
  public void viewLoad() throws IOException {
    HttpURLConnection clientConnection0 =
        tryRequest("loadcsv?filepath=data/census/dol_ri_earnings_disparity.csv&&headers=true");
    assertEquals(200, clientConnection0.getResponseCode());

    HttpURLConnection clientConnection = tryRequest("viewcsv");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    List earningsHeaderList =
        List.of(
            "State",
            "Data Type",
            "Average Weekly Earnings",
            "Number of Workers",
            "Earnings Disparity",
            "Employed Percent");
    assertEquals(earningsHeaderList, body.get("headers"));
    assertEquals(
        "[[RI, White,  $1,058.47 , 395773.6521, $1.00, 75%], "
            + "[RI, Black, $770.26, 30424.80376, $0.73, 6%], [RI, Native American/American Indian,"
            + " $471.07, 2315.505646, $0.45, 0%], [RI, Asian-Pacific Islander,  $1,080.09 , "
            + "18956.71657, $1.02, 4%], [RI, Hispanic/Latino, $673.14, 74596.18851, $0.64, 14%], "
            + "[RI, Multiracial, $971.89, 8883.049171, $0.92, 2%]]",
        body.get("data").toString());
  }

  /**
   * Test to check searching after load, without identifiers
   *
   * @throws IOException
   */
  @Test
  public void searchLoad() throws IOException {
    HttpURLConnection clientConnection0 =
        tryRequest("loadcsv?filepath=data/census/postsecondary_education.csv&&headers=false");
    assertEquals(200, clientConnection0.getResponseCode());

    HttpURLConnection clientConnection = tryRequest("searchcsv?searchterm=Asian");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    assertEquals("Asian", body.get("search term"));
    assertEquals(
        "[[Asian, 2020, 2020, 217156, Brown University, 214, brown-university, 0.069233258,"
            + " Men, 1], [Asian, 2020, 2020, 217156, Brown University, 235, brown-university, "
            + "0.076027176, Women, 2]]",
        body.get("data").toString());
    assertEquals(3, body.size());
  }

  /**
   * Test to check searching after load, with column headers
   *
   * @throws IOException
   */
  @Test
  public void searchHeaderName() throws IOException {
    HttpURLConnection clientConnection0 =
        tryRequest("loadcsv?filepath=data/census/postsecondary_education.csv&&headers=true");
    assertEquals(200, clientConnection0.getResponseCode());

    HttpURLConnection clientConnection =
        tryRequest("searchcsv?searchterm=Asian&&" + "columnidentifier=IPEDS%20Race&&isindex=false");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    assertEquals("Asian", body.get("search term"));
    assertEquals(false, body.get("is index"));
    assertEquals("IPEDS Race", body.get("col identifier"));
    assertEquals(
        "[[Asian, 2020, 2020, 217156, Brown University, 214, brown-university, 0.069233258,"
            + " Men, 1], [Asian, 2020, 2020, 217156, Brown University, 235, brown-university, "
            + "0.076027176, Women, 2]]",
        body.get("data").toString());
    assertEquals(5, body.size());
  }

  /**
   * Test to check searching after load, with column index and headers
   *
   * @throws IOException
   */
  @Test
  public void searchHeaderIndex() throws IOException {
    HttpURLConnection clientConnection0 =
        tryRequest("loadcsv?filepath=data/census/postsecondary_education.csv&&headers=true");
    assertEquals(200, clientConnection0.getResponseCode());

    HttpURLConnection clientConnection =
        tryRequest("searchcsv?searchterm=Asian&&" + "columnidentifier=0&&isindex=true");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    assertEquals("Asian", body.get("search term"));
    assertEquals(true, body.get("is index"));
    assertEquals("0", body.get("col identifier"));
    assertEquals(
        "[[Asian, 2020, 2020, 217156, Brown University, 214, brown-university, 0.069233258,"
            + " Men, 1], [Asian, 2020, 2020, 217156, Brown University, 235, brown-university, "
            + "0.076027176, Women, 2]]",
        body.get("data").toString());
    assertEquals(5, body.size());
  }

  /**
   * Test to check searching after load, with column index and no headers
   *
   * @throws IOException
   */
  @Test
  public void searchIndex() throws IOException {
    HttpURLConnection clientConnection0 =
        tryRequest("loadcsv?filepath=data/census/postsecondary_education.csv&&headers=false");
    assertEquals(200, clientConnection0.getResponseCode());

    HttpURLConnection clientConnection =
        tryRequest("searchcsv?searchterm=Asian&&" + "columnidentifier=0&&isindex=true");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("success", body.get("result"));
    assertEquals("Asian", body.get("search term"));
    assertEquals(true, body.get("is index"));
    assertEquals("0", body.get("col identifier"));
    assertEquals(
        "[[Asian, 2020, 2020, 217156, Brown University, 214, brown-university, 0.069233258,"
            + " Men, 1], [Asian, 2020, 2020, 217156, Brown University, 235, brown-university, "
            + "0.076027176, Women, 2]]",
        body.get("data").toString());
    assertEquals(5, body.size());
  }

  /**
   * Test to check error for missing search term
   *
   * @throws IOException
   */
  @Test
  public void searchMissingParam() throws IOException {
    HttpURLConnection clientConnection0 =
        tryRequest("loadcsv?filepath=data/census/postsecondary_education.csv&&headers=true");
    assertEquals(200, clientConnection0.getResponseCode());

    HttpURLConnection clientConnection = tryRequest("searchcsv");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", body.get("result"));
    assertEquals("a search term must be provided", body.get("details"));
    assertEquals(2, body.size());
  }

  /**
   * Test to check error for searching by colname when there are no headers
   *
   * @throws IOException
   */
  @Test
  public void searchNoHeaders() throws IOException {
    HttpURLConnection clientConnection0 =
        tryRequest("loadcsv?filepath=data/census/postsecondary_education.csv&&headers=false");
    assertEquals(200, clientConnection0.getResponseCode());

    HttpURLConnection clientConnection =
        tryRequest("searchcsv?searchterm=Asian&&" + "columnidentifier=IPEDS%20Race&&isindex=false");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", body.get("result"));
    assertEquals("file needs to have headers to search by column name", body.get("details"));
    assertEquals(2, body.size());
  }

  /**
   * Test to check error for searching by column index and providing a non-integer
   *
   * @throws IOException
   */
  @Test
  public void searchNoInt() throws IOException {
    HttpURLConnection clientConnection0 =
        tryRequest("loadcsv?filepath=data/census/postsecondary_education.csv&&headers=false");
    assertEquals(200, clientConnection0.getResponseCode());

    HttpURLConnection clientConnection =
        tryRequest("searchcsv?searchterm=Asian&&" + "columnidentifier=IPEDS%20Race&&isindex=true");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", body.get("result"));
    assertEquals("column index must be an integer", body.get("details"));
    assertEquals(2, body.size());
  }

  /**
   * Test to check error for searching by a missing column
   *
   * @throws IOException
   */
  @Test
  public void searchNoCol() throws IOException {
    HttpURLConnection clientConnection0 =
        tryRequest("loadcsv?filepath=data/census/postsecondary_education.csv&&headers=true");
    assertEquals(200, clientConnection0.getResponseCode());

    HttpURLConnection clientConnection =
        tryRequest("searchcsv?searchterm=Asian&&" + "columnidentifier=aweqw&&isindex=false");
    assertEquals(200, clientConnection.getResponseCode());

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", body.get("result"));
    assertEquals("column requested is not found", body.get("details"));
    assertEquals(2, body.size());
  }
}
