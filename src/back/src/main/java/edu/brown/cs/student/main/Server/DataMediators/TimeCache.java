package edu.brown.cs.student.main.Server.DataMediators;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.Server.BroadbandData.BroadbandDatasource;
import edu.brown.cs.student.main.Server.Exceptions.BadJsonException;
import edu.brown.cs.student.main.Server.Exceptions.BadRequestException;
import edu.brown.cs.student.main.Server.Exceptions.DataSourceException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Cache using adaptor strategy that interfaces between BroadBandHandler and a datasource. Evicts
 * when time exceeds a developer-provided period in seconds.
 */
public class TimeCache implements DataMediator {
  private final LoadingCache<String, Map> apiCache;
  private final BroadbandDatasource datasource;
  private Map<String, Object> resultMap;
  private String state;
  private String county;
  private Exception exception;

  /**
   * Constructor for the cache, initialising the cache and datsource
   *
   * @param expireSecs maximum time after last access of items in cache before eviction
   * @param datasource datasource to draw information from
   */
  public TimeCache(Integer expireSecs, BroadbandDatasource datasource) {
    this.datasource = datasource;
    LoadingCache<String, Map> apiCache =
        CacheBuilder.newBuilder()
            .expireAfterAccess(expireSecs, TimeUnit.SECONDS)
            .build(
                new CacheLoader<>() {
                  public Map load(String county) throws DataSourceException {
                    Map<String, Object> newMap = null;
                    return newMap;
                  }
                });
    this.apiCache = apiCache;
  }

  /**
   * Takes in a state and a county, and makes the call to the datasource for information
   *
   * @param state state of interest
   * @param county county of interest
   * @return a map holding the broadband percentage of that county and the date/time of retrieval
   * @throws DataSourceException issue with datasource
   * @throws BadRequestException issue with user request
   * @throws BadJsonException issue with json conversion
   */
  private Map<String, Object> makeCall(String state, String county)
      throws DataSourceException, BadRequestException, BadJsonException {
    Map<String, Object> responseMap;
    try {
      this.datasource.getBroadband();
      responseMap = this.datasource.packageResponse(state, county);
      return new HashMap<>(responseMap);
    } catch (DataSourceException e) {
      this.exception = e;
      throw e;
    } catch (BadRequestException e) {
      this.exception = e;
      throw e;
    }
  }

  /**
   * Takes in a state and a county, and obtains a map holding the broadband percentage of that
   * county and the date/time of retrieval
   *
   * @param state state of interest
   * @param county county of interest
   * @return a map holding the broadband percentage of that county and the date/time of retrieval
   * @throws Exception issue with loading data
   */
  public Map<String, Object> getResponse(String state, String county) throws Exception {
    this.state = state;
    this.county = county;
    try {
      this.resultMap = apiCache.get(county);
    } catch (ExecutionException e) {
      throw this.exception;
    }
    return this.resultMap;
  }
}
