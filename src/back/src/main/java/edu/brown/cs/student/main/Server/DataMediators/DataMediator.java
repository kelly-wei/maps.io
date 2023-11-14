package edu.brown.cs.student.main.Server.DataMediators;

import java.util.Map;

/**
 * Interface to package all data-mediating and storing functions. Using the adaptor pattern and
 * strategy pattern, two different caches have been implemented as well as a cache bypass option.
 * Future developers can provide their own caches as well by implementing this interface.
 */
public interface DataMediator {

  /**
   * Takes in a state and a county, and obtains a map holding the broadband percentage of that
   * county and the date/time of retrieval
   *
   * @param state state of interest
   * @param county county of interest
   * @return a map holding the broadband percentage of that county and the date/time of retrieval
   * @throws Exception generic exception thrown by cache-loading strategies
   */
  Map<String, Object> getResponse(String state, String county) throws Exception;
}
