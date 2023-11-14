package edu.brown.cs.student.main.Server.BroadbandData;

import edu.brown.cs.student.main.Server.Exceptions.BadJsonException;
import edu.brown.cs.student.main.Server.Exceptions.BadRequestException;
import edu.brown.cs.student.main.Server.Exceptions.DataSourceException;
import java.util.Map;

/** Interface that enforces being able to access the broadband data and getting a response. */
public interface BroadbandDatasource {

  /**
   * Enforces finding broadband data in calsses that implement BroadbandDatasource.
   *
   * @throws DataSourceException if there is an error with the data source (Census API).
   * @throws BadRequestException if something is wrong with the request.
   * @throws BadJsonException if the Json object cannot be found.
   */
  void getBroadband() throws DataSourceException, BadRequestException, BadJsonException;

  /**
   * @param state is the state name.
   * @param county is the county name.
   * @return a Map of a response and the data.
   * @throws DataSourceException if there is an error with the data source (Census API).
   * @throws BadRequestException if something is wrong with the request.
   * @throws BadJsonException if Json object cannot be found.
   */
  Map<String, Object> packageResponse(String state, String county)
      throws DataSourceException, BadRequestException, BadJsonException;
}
