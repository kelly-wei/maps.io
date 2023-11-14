package edu.brown.cs.student.TestServer.BroadbandTest;

import edu.brown.cs.student.main.Server.BroadbandData.CensusDatasource;
import edu.brown.cs.student.main.Server.Exceptions.BadJsonException;
import edu.brown.cs.student.main.Server.Exceptions.BadRequestException;
import edu.brown.cs.student.main.Server.Exceptions.DataSourceException;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

/** Tests methods from CensusDataSource, the class tha handles calling to Census API. */
public class TestCensusDatasource {

  /**
   * Tests that a list of states and state IDs can be generated.
   *
   * @throws DataSourceException issue with Census API.
   * @throws BadRequestException issue with request parameters.
   * @throws BadJsonException issue with converting to/from Json object.
   */
  @Test
  public void testStateList() throws DataSourceException, BadRequestException, BadJsonException {
    CensusDatasource source = new CensusDatasource();
    source.getBroadbandPercent("California", "Alameda County, California");
    Assert.assertNotNull(source.getStateIDList());
  }

  /**
   * Tests getting state IDs for valid and invalid states.
   *
   * @throws DataSourceException issue with Census API.
   * @throws BadRequestException issue with request parameters.
   */
  @Test
  public void testStateID() throws DataSourceException, BadRequestException {
    CensusDatasource source = new CensusDatasource();
    int id = source.getStateID("California");
    Assert.assertEquals(06, id);
    id = source.getStateID("Rhode Island");
    Assert.assertEquals(44, id);
    Assert.assertThrows(
        BadRequestException.class,
        () -> {
          source.getStateID("CIT");
        });
  }

  /**
   * Tests getting a county list for a valid state and an invalid stae.
   *
   * @throws DataSourceException issue with Census API.
   * @throws BadRequestException issue with request parameters.
   * @throws BadJsonException issue with converting to/from Json object.
   */
  @Test
  public void testCountyList() throws DataSourceException, BadRequestException, BadJsonException {
    CensusDatasource source = new CensusDatasource();
    Assert.assertNotNull(source.getCountyList(06));
    Assert.assertThrows(
        DataSourceException.class,
        () -> {
          source.getCountyList(781);
        });
  }

  /**
   * Tests getting broadband for valid and invalid states.
   *
   * @throws DataSourceException issue with Census API.
   * @throws BadRequestException issue with request parameters.
   * @throws BadJsonException issue with converting to/from Json object.
   */
  @Test
  public void testBroadBand() throws DataSourceException, BadRequestException, BadJsonException {
    CensusDatasource source = new CensusDatasource();
    Assert.assertEquals(
        89.9, source.getBroadbandPercent("California", "Alameda County, California"));

    Assert.assertThrows(
        BadRequestException.class,
        () -> {
          source.getBroadbandPercent("Califor", "Alameda County, California");
        });

    Assert.assertThrows(
        BadRequestException.class,
        () -> {
          source.getBroadbandPercent("California", "Alameda County");
        });
  }
}
