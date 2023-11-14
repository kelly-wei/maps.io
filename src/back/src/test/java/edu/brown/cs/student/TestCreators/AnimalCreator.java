package edu.brown.cs.student.TestCreators;

import edu.brown.cs.student.main.Parsing.Creators.CreatorFromRow;
import edu.brown.cs.student.main.Parsing.FactoryFailureException;
import java.util.List;

/** Class allowing for the creation of an Animal from a List<String> */
public class AnimalCreator implements CreatorFromRow<Animal> {

  /** Constructor for AnimalCreator */
  public AnimalCreator() {}

  /**
   * Creates an Animal object from a List<String>
   *
   * @param row List<String> representing row to be converted
   * @return Animal object created
   * @throws FactoryFailureException Error with creating an Animal using the provided List<String>
   */
  @Override
  public Animal create(List<String> row) throws FactoryFailureException {
    // Row contains the wrong number of discrete values for an Animal object
    if (row.size() != 4) {
      String errMsg = "Row must have 4 values: " + row;
      throw new FactoryFailureException(errMsg, row);
    }

    String name = row.get(0);
    String species = row.get(2);

    Integer age = null;
    Integer numTag = null;
    try {
      age = Integer.valueOf(row.get(1));
      numTag = Integer.valueOf(row.get(3));
    } catch (NumberFormatException e) { // The Age/ID Number is not an integer
      String errMsg = "Value in age/numTag column must be an integer: " + row;
      throw new FactoryFailureException(errMsg, row);
    }

    return new Animal(name, age, species, numTag);
  }
}
