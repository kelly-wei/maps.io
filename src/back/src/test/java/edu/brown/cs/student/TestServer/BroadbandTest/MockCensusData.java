package edu.brown.cs.student.TestServer.BroadbandTest;

import edu.brown.cs.student.main.Server.BroadbandData.BroadbandDatasource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/** This class is a mock Datasource that implements BradbandDatasource. */
public class MockCensusData implements BroadbandDatasource {
  private LocalDateTime dateTime;
  private Double broadbandPercent;

  public MockCensusData(LocalDateTime dateTime, Double broadbandPercent) {
    this.dateTime = dateTime;
    this.broadbandPercent = broadbandPercent;
  }

  /** Implemented as part of the interface. */
  @Override
  public void getBroadband() {}

  /**
   * This method is implemented from the interface and mirrors the data that is passed into the
   * constructor.
   *
   * @param state is the state name.
   * @param county is the county name.
   * @return a Hashmap
   */
  @Override
  public Map<String, Object> packageResponse(String state, String county) {
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("date/time request received", this.dateTime.toString());
    responseMap.put("data: ", this.broadbandPercent);
    return responseMap;
  }
}
