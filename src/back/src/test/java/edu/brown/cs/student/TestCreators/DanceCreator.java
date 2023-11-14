package edu.brown.cs.student.TestCreators;

import edu.brown.cs.student.main.Parsing.Creators.CreatorFromRow;
import edu.brown.cs.student.main.Parsing.FactoryFailureException;
import java.util.List;

/** Class allowing for the creation of a Dance from a List<String> */
public class DanceCreator implements CreatorFromRow<Dance> {

  /** Constructor for DanceCreator */
  public DanceCreator() {}

  /**
   * Creates a Dance object from a List<String>
   *
   * @param row List<String> representing row to be converted
   * @return Dance object created
   * @throws FactoryFailureException Error with creating a Dance using the provided List<String>
   */
  @Override
  public Dance create(List<String> row) throws FactoryFailureException {
    // The row has the wrong number of discrete values for a Dance
    if (row.size() != 3) {
      String errMsg = "Row must have 4 values: " + row;
      throw new FactoryFailureException(errMsg, row);
    }
    String style = row.get(0);
    String vibe = row.get(1);

    Boolean shoes;
    if ((row.get(2)).equalsIgnoreCase("Yes")) {
      shoes = true;
    } else if ((row.get(2)).equalsIgnoreCase("No")) {
      shoes = false;
    } else { // the value in the Shoes column must be a Boolean
      String errMsg = "Value in shoes column must be yes or no: " + row;
      throw new FactoryFailureException(errMsg, row);
    }

    return new Dance(style, vibe, shoes);
  }
}
