package edu.brown.cs.student.main.Server.BroadbandData;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.Exceptions.BadJsonException;
import edu.brown.cs.student.main.Server.Exceptions.BadRequestException;
import edu.brown.cs.student.main.Server.Exceptions.DataSourceException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;

/** Class for calling the Census API and packaging the API response to our server. */
public class CensusDatasource implements BroadbandDatasource {
  private Integer stateID;
  private Integer countyID;
  private List<List<String>> stateIDList;
  private List<List<String>> countyList;
  private List<List<String>> resultList;
  private HashMap<String, Integer> stateToID;
  private LocalDateTime timeRetrieved;
  private Double broadBandPercent;

  /**
   * Helper method that retrieves all state IDs from the Census. Only called once at the beginning
   * when the server is opened. Populates a HashMap mapping each String to an Integer corresponding
   * to the state's ID.
   *
   * @throws DataSourceException if there is an issue with connecting to API census.
   */
  private void getStateIDs() throws DataSourceException {
    try {
      URL requestURL = new URL("https://api.census.gov/data/2010/acs/acs1?get=NAME&for=state:*");
      HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
      clientConnection.setRequestMethod("GET");
      if (clientConnection.getResponseCode() != 200) {
        throw new DataSourceException();
      }
      Moshi moshi = new Moshi.Builder().build();
      Type mapStateList = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(mapStateList);

      this.stateIDList = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      if (this.stateIDList.isEmpty()) {
        throw new DataSourceException();
      }
      this.stateToID = new HashMap<>();
      for (int i = 1; i < stateIDList.size(); i++) {
        this.stateToID.put(stateIDList.get(i).get(0), Integer.valueOf(stateIDList.get(i).get(1)));
      }
      clientConnection.disconnect();
    } catch (IOException e) {
      throw new DataSourceException();
    }
  }

  /**
   * Gets a state ID for a given state. Uses defensive copying so outside sources can't change the
   * state ID.
   *
   * @param state is a String of the state name.
   * @return an Integer of the state ID given a state name.
   * @throws DataSourceException if there is an issue with connecting to API census.
   * @throws BadRequestException if unable to find a state ID given the input.
   */
  public int getStateID(String state) throws DataSourceException, BadRequestException {
    int percent;
    if (this.stateToID == null) {
      this.getStateIDs();
      Integer stateId = this.stateToID.get(state);
      if (stateId != null) {
        this.stateID = stateId;
        percent = Integer.valueOf(stateId);
        return percent;
      } else {
        throw new BadRequestException();
      }
    } else {
      this.stateID = null;
      Integer stateId = this.stateToID.get(state);
      if (stateId != null) {
        this.stateID = stateId;
        percent = Integer.valueOf(stateId);
        return percent;
      } else {
        throw new BadRequestException();
      }
    }
  }

  /**
   * Getter that retrieves a list of counties for a given state ID. Uses defensive copying so
   * outside sources can't change the list of counties.
   *
   * @param state is an int of the state ID.
   * @return List of List of Strings of all the counties in a state.
   * @throws DataSourceException if there is an issue with connecting to API census.
   * @throws BadJsonException if the Json object cannot be found.
   * @throws BadRequestException if unable to find a state ID given the input.
   */
  public List<List<String>> getCountyList(int state)
      throws DataSourceException, BadJsonException, BadRequestException {
    this.getCounties(state);
    return new ArrayList<>(this.countyList);
  }

  /**
   * Helper method that gets all the counties for a given state ID. Populates of a list that
   * represents all the counties.
   *
   * @param stateID is the state ID integer.
   * @throws DataSourceException if there is an issue with connecting to API census.
   * @throws BadJsonException if the Json object cannot be found.
   */
  private void getCounties(int stateID) throws DataSourceException, BadJsonException {
    try {
      URL requestURL;
      if (stateID < 10) {
        requestURL =
            new URL(
                "https://api.census.gov/data/2010/acs/acs1?get=NAME&for=county:*&in=state:"
                    + 0
                    + stateID);
      } else {
        requestURL =
            new URL(
                "https://api.census.gov/data/2010/acs/acs1?get=NAME&for=county:*&in=state:"
                    + stateID); // REAL ONE
      }
      HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
      clientConnection.setRequestMethod("GET");
      if (clientConnection.getResponseCode() != 200) {
        throw new MalformedURLException();
      }
      Moshi moshi = new Moshi.Builder().build();
      Type countyList = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(countyList);
      this.countyList = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      if (this.countyList.isEmpty()) {
        throw new BadJsonException();
      }
      clientConnection.disconnect();
    } catch (ProtocolException e) {
      throw new DataSourceException();
    } catch (MalformedURLException e) {
      throw new DataSourceException();
    } catch (IOException e) {
      throw new DataSourceException();
    }
  }

  /**
   * Method inherited from the interface. Gets the broadband data for a state ID and county ID,
   * which have been found through other methods. Calls the API Census.
   *
   * @throws DataSourceException if there is an issue with connecting to API census.
   * @throws BadRequestException if unable to find a state ID given the input.
   * @throws BadJsonException if the Json object cannot be found.
   */
  @Override
  public void getBroadband() throws DataSourceException, BadRequestException, BadJsonException {
    try {
      URL requestURL;
      if (this.countyID < 10 && this.stateID < 10) {
        requestURL =
            new URL(
                "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                    + "00"
                    + this.countyID
                    + "&in=state:"
                    + 0
                    + this.stateID);
      } else if (this.countyID >= 10 && this.stateID < 100 && this.stateID < 10) {
        requestURL =
            new URL(
                "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                    + 0
                    + this.countyID
                    + "&in=state:"
                    + 0
                    + this.stateID);
      } else if (this.countyID >= 100 && this.stateID < 10) {
        requestURL =
            new URL(
                "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                    + this.countyID
                    + "&in=state:"
                    + 0
                    + this.stateID);
      } else if (this.countyID < 10 && this.stateID >= 10) {
        requestURL =
            new URL(
                "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                    + "00"
                    + this.countyID
                    + "&in=state:"
                    + this.stateID);
      } else if (this.countyID >= 10 && this.stateID < 100 && this.stateID >= 10) {
        requestURL =
            new URL(
                "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                    + 0
                    + this.countyID
                    + "&in=state:"
                    + this.stateID);
      } else if (this.countyID >= 100 && this.stateID >= 10) {
        requestURL =
            new URL(
                "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                    + this.countyID
                    + "&in=state:"
                    + this.stateID);
      } else {
        requestURL =
            new URL(
                "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:"
                    + this.stateID);
      }
      HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
      this.findTime();
      if (clientConnection.getResponseCode() != 200) {
        throw new MalformedURLException();
      }
      Moshi moshi = new Moshi.Builder().build();
      Type countyList = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(countyList);
      this.resultList = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      if (this.resultList.isEmpty()) {
        throw new BadJsonException();
      }
      clientConnection.disconnect();

    } catch (MalformedURLException e) {
      throw new DataSourceException();
    } catch (IOException e) {
      throw new DataSourceException();
    }
  }

  /** Helper method that finds the curren time. */
  private void findTime() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    this.timeRetrieved = LocalDateTime.now();
  }

  /**
   * Calls all the relevant helper methods to populate the data structures needed to get the
   * broadband data. Creates a defensive copy of percent so outside sources can't change it.
   *
   * @param state is the state name.
   * @param county is the county name.
   * @return a Double of the percent of broadband in a given state and county.
   * @throws DataSourceException if there is an issue with connecting to API census.
   * @throws BadRequestException if unable to find a state ID given the input.
   * @throws BadJsonException if the Json object cannot be found.
   */
  public Double getBroadbandPercent(String state, String county)
      throws DataSourceException, BadRequestException, BadJsonException {
    this.getCounties(this.getStateID(state));
    this.convertCountyID(county);
    this.getBroadband();
    this.broadBandPercent = Double.parseDouble(this.resultList.get(1).get(1));
    Double percent = Double.valueOf(this.broadBandPercent);
    return percent;
  }

  /**
   * Converts a county name to a county ID.
   *
   * @param county of the county name.
   * @throws BadRequestException if unable to find a state ID given the input.
   */
  public void convertCountyID(String county) throws BadRequestException {
    this.countyID = null;
    try {
      for (List<String> row : this.countyList) {
        if (row.get(0).equals(county)) {
          this.countyID = Integer.parseInt(row.get(2));
        }
      }
      if (this.countyID == null) {
        throw new BadRequestException();
      }
    } catch (BadRequestException e) {
      throw new BadRequestException();
    }
  }

  /**
   * Getter that creates a defensive copy of the state ID list so outside sources can't change it.
   *
   * @return a List of List of Strings of all the state IDs.
   */
  public List<List<String>> getStateIDList() {
    return new ArrayList<>(this.stateIDList);
  }

  /**
   * Packages a response using all the data in this class.
   *
   * @param state is the state name.
   * @param county is the county name.
   * @return a Hashmap mapping the data and time to the data in this class.
   * @throws DataSourceException
   * @throws BadRequestException if unable to find a state ID given the input.
   * @throws BadJsonException
   */
  @Override
  public Map<String, Object> packageResponse(String state, String county)
      throws DataSourceException, BadRequestException, BadJsonException {
    Map<String, Object> responseMap = new HashMap<>();
    this.findTime();
    responseMap.put("date/time request received", this.timeRetrieved.toString());
    responseMap.put("data", this.getBroadbandPercent(state, county));
    return new HashMap<>(responseMap);
  }
}
