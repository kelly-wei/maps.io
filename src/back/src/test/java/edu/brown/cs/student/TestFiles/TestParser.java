package edu.brown.cs.student.TestFiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.brown.cs.student.TestCreators.Animal;
import edu.brown.cs.student.TestCreators.AnimalCreator;
import edu.brown.cs.student.TestCreators.Dance;
import edu.brown.cs.student.TestCreators.DanceCreator;
import edu.brown.cs.student.main.Parsing.FactoryFailureException;
import edu.brown.cs.student.main.Parsing.Parser;
import edu.brown.cs.student.main.Parsing.ProcessedFile;
import java.io.CharArrayReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Testing class for Parser */
public class TestParser {

  /**
   * Test to ensure that the parser can process files with headers
   *
   * @throws IOException – IOException thrown by packageFile(), passed up to main() in full code
   */
  @Test
  public void headerParsing() throws IOException {
    Reader danceTest = new FileReader("src/test/java/edu/brown/cs/student/TestCSVs/DanceData.csv");
    Parser danceParser = new Parser(danceTest, true);
    danceParser.packageFile();
    ProcessedFile danceParsed = danceParser.getFinalFile();

    List danceHeaderList = List.of("Style", "Vibe", "Shoes");
    assertEquals(danceParsed.getHeaders(), danceHeaderList);

    List danceR1 = List.of("Contemporary", "Variable", "No");
    List danceR2 = List.of("Jazz", "Happy", "Yes");
    List danceR3 = List.of("Ballet", "Sad", "Yes");
    List danceR4 = List.of("Tap", "Happy", "Yes");
    List danceContentList = List.of(danceR1, danceR2, danceR3, danceR4);
    assertEquals(danceParsed.getContent(), danceContentList);
  }

  /**
   * Test to ensure that the parser can process files without headers
   *
   * @throws IOException – IOException thrown by packageFile(), passed up to main() in full code
   */
  @Test
  public void noHeaderParsing() throws IOException {
    Reader animalsTest =
        new FileReader("src/test/java/edu/brown/cs/student/TestCSVs/AnimalsData.csv");
    Parser animalsParser = new Parser(animalsTest, false);
    animalsParser.packageFile();
    ProcessedFile animalsParsed = animalsParser.getFinalFile();

    assertEquals(animalsParsed.getHeaders(), null);

    List animalsR1 = List.of("Bob", "30", "Tortoise", "123");
    List animalsR2 = List.of("Quack", "1", "Duck", "203");
    List animalsR3 = List.of("Moo", "3", "Elephant", "20");
    List animalsContentList = List.of(animalsR1, animalsR2, animalsR3);
    assertEquals(animalsParsed.getContent(), animalsContentList);
  }

  /**
   * Test to ensure that the parser can parse StringReaders
   *
   * @throws IOException – IOException thrown by packageFile(), passed up to main() in full code
   */
  @Test
  public void stringParsing() throws IOException {
    Reader stringTest =
        new StringReader("10,9,8,7,6" + "\n" + "1,2,3,4,5" + "\n" + "this,is,a,group,of,nums");
    Parser stringParser = new Parser(stringTest, false);
    stringParser.packageFile();
    ProcessedFile stringParsed = stringParser.getFinalFile();

    assertEquals(stringParsed.getHeaders(), null);

    List stringR1 = List.of("10", "9", "8", "7", "6");
    List stringR2 = List.of("1", "2", "3", "4", "5");
    List stringR3 = List.of("this", "is", "a", "group", "of", "nums");
    List stringContentList = List.of(stringR1, stringR2, stringR3);
    assertEquals(stringParsed.getContent(), stringContentList);
  }

  /**
   * Test to ensure that the parser can parse StringReaders
   *
   * @throws IOException – IOException thrown by packageFile(), passed up to main() in full code
   */
  @Test
  public void arrayParsing() throws IOException {
    char[] charArray = {'h', 'e', 'l', 'l', 'o', '\n', 'b', 'y', 'e'};
    Reader arrayTest = new CharArrayReader(charArray);
    Parser arrayParser = new Parser(arrayTest, false);
    arrayParser.packageFile();
    ProcessedFile arrayParsed = arrayParser.getFinalFile();

    assertEquals(arrayParsed.getHeaders(), null);

    List arrayR1 = List.of("hello");
    List arrayR2 = List.of("bye");
    List arrayContentList = List.of(arrayR1, arrayR2);
    assertEquals(arrayParsed.getContent(), arrayContentList);
  }

  /**
   * Test to ensure that the parser can convert each row into a different object (animal) Also
   * ensures that conversion works on a Reader object without headers
   *
   * @throws IOException – IOException thrown by packageFile(), passed up to main() in full code
   * @throws FactoryFailureException – Exception thrown by failed conversion
   */
  @Test
  public void animalConverting() throws IOException, FactoryFailureException {
    Reader animalsTest =
        new FileReader("src/test/java/edu/brown/cs/student/TestCSVs/AnimalsData.csv");
    AnimalCreator animalTransformer = new AnimalCreator();
    Parser animalsParser = new Parser(animalsTest, animalTransformer, false);
    animalsParser.packageFile();
    ProcessedFile animalsStringParsed = animalsParser.getFinalFile();
    ProcessedFile animalsParsed = animalsParser.stringToGeneric(animalsStringParsed);

    assertEquals(animalsParsed.getHeaders(), null);

    Animal bob = new Animal("Bob", 30, "Tortoise", 123);
    Animal quack = new Animal("Quack", 1, "Duck", 203);
    Animal moo = new Animal("Moo", 3, "Elephant", 20);
    List<Animal> animalList = List.of(bob, quack, moo);
    assertEquals(animalsParsed.getContent(), animalList);
  }

  /**
   * Test to ensure that the parser can convert each row into a different object (dance). Also
   * ensures that conversion works on a Reader object with headers
   *
   * @throws IOException – IOException thrown by packageFile(), passed up to main() in full code
   * @throws FactoryFailureException – Exception thrown by failed conversion
   */
  @Test
  public void danceConverting() throws IOException, FactoryFailureException {
    Reader danceTest = new FileReader("src/test/java/edu/brown/cs/student/TestCSVs/DanceData.csv");
    DanceCreator danceTransformer = new DanceCreator();
    Parser danceParser = new Parser(danceTest, danceTransformer, true);
    danceParser.packageFile();
    ProcessedFile danceStringParsed = danceParser.getFinalFile();
    ProcessedFile danceParsed = danceParser.stringToGeneric(danceStringParsed);

    List danceHeaderList = List.of("Style", "Vibe", "Shoes");
    assertEquals(danceParsed.getHeaders(), danceHeaderList);

    Dance contemp = new Dance("Contemporary", "Variable", false);
    Dance jazz = new Dance("Jazz", "Happy", true);
    Dance ballet = new Dance("Ballet", "Sad", true);
    Dance tap = new Dance("Tap", "Happy", true);
    List<Dance> danceList = List.of(contemp, jazz, ballet, tap);
    assertEquals(danceParsed.getContent(), danceList);
  }

  /**
   * Test to that the converter throws an error if conversion fails
   *
   * @throws IOException – IOException thrown by packageFile(), passed up to main() in full code
   */
  @Test
  public void failConverting() throws IOException {
    Reader animalsTest =
        new FileReader("src/test/java/edu/brown/cs/student/TestCSVs/BadAnimalData.csv");
    AnimalCreator animalTransformer = new AnimalCreator();
    Parser animalsParser = new Parser(animalsTest, animalTransformer, false);
    animalsParser.packageFile();
    ProcessedFile animalsStringParsed = animalsParser.getFinalFile();
    Throwable ffException =
        assertThrows(
            FactoryFailureException.class,
            () -> {
              animalsParser.stringToGeneric(animalsStringParsed);
            });
    assertEquals("Row must have 4 values: [Moo, 3, Elephant, 20, 16]", ffException.getMessage());
  }
}
