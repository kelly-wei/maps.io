package edu.brown.cs.student.main.Server.DataMediators;

import edu.brown.cs.student.main.Server.BroadbandData.BroadbandDatasource;
import edu.brown.cs.student.main.Server.Exceptions.BadJsonException;
import edu.brown.cs.student.main.Server.Exceptions.BadRequestException;
import edu.brown.cs.student.main.Server.Exceptions.DataSourceException;
import java.util.Map;

/** Data mediation option that does not cache the data */
public class NoCache implements DataMediator {

  private final BroadbandDatasource datasource;

  /**
   * Constructor for noCahce
   *
   * @param datasource
   */
  public NoCache(BroadbandDatasource datasource) {
    this.datasource = datasource;
  }

  /**
   * Takes in a state and a county, and obtains a map holding the broadband percentage of that
   * county and the date/time of retrieval
   *
   * @param state state of interest
   * @param county county of interest
   * @return a map holding the broadband percentage of that county and the date/time of retrieval
   * @throws DataSourceException issue with datasource
   * @throws BadRequestException issue with user request
   * @throws BadJsonException issue with json conversion
   */
  @Override
  public Map<String, Object> getResponse(String state, String county)
      throws DataSourceException, BadRequestException, BadJsonException {
    try {
      Map<String, Object> responseMap = this.datasource.packageResponse(state, county);
      return responseMap;
    } catch (DataSourceException e) {
      throw e;
    } catch (BadRequestException e) {
      throw e;
    }
  }
}
