package edu.brown.cs.student.TestFiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.brown.cs.student.main.Parsing.Parser;
import edu.brown.cs.student.main.Searching.SearcherIdentifier;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Testing class for SearcherIdentifier
 *
 * @author karismajn
 */
public class TestSearcherIdentifier {

  /**
   * Test to ensure that the searcher can find rows using a column index
   *
   * @throws IOException – IOException thrown by FileReader, passed up to main() in full code
   */
  @Test
  public void indexSearch() throws IOException {
    Reader educTest = new FileReader("data/census/postsecondary_education.csv");
    Parser educParser = new Parser(educTest, true);
    educParser.packageFile();

    SearcherIdentifier educSearcher =
        new SearcherIdentifier(educParser.getFinalFile(), "Asian", "0", true);
    List<List<String>> searchResult = educSearcher.search();

    List<String> educR1 =
        List.of(
            "Asian",
            "2020",
            "2020",
            "217156",
            "Brown University",
            "214",
            "brown-university",
            "0.069233258",
            "Men",
            "1");
    List<String> educR2 =
        List.of(
            "Asian",
            "2020",
            "2020",
            "217156",
            "Brown University",
            "235",
            "brown-university",
            "0.076027176",
            "Women",
            "2");
    assertEquals(searchResult, List.of(educR1, educR2));
  }

  /**
   * Test to ensure that the searcher can find rows using a column name
   *
   * @throws IOException – IOException thrown by FileReader, passed up to main() in full code
   */
  @Test
  public void nameSearch() throws IOException {
    Reader educTest = new FileReader("data/census/postsecondary_education.csv");
    Parser educParser = new Parser(educTest, true);
    educParser.packageFile();

    SearcherIdentifier educSearcher =
        new SearcherIdentifier(educParser.getFinalFile(), "Asian", "IPEDS Race", false);
    List<List<String>> searchResult = educSearcher.search();

    List<String> educR1 =
        List.of(
            "Asian",
            "2020",
            "2020",
            "217156",
            "Brown University",
            "214",
            "brown-university",
            "0.069233258",
            "Men",
            "1");
    List<String> educR2 =
        List.of(
            "Asian",
            "2020",
            "2020",
            "217156",
            "Brown University",
            "235",
            "brown-university",
            "0.076027176",
            "Women",
            "2");
    assertEquals(searchResult, List.of(educR1, educR2));
  }

  /**
   * Test for the case where the column identifier cannot be found
   *
   * @throws IOException – IOException thrown by FileReader, passed up to main() in full code
   */
  @Test
  public void missingColSearch() throws IOException {
    Reader starTest = new FileReader("data/stars/stardata.csv");
    Parser starParser = new Parser(starTest, true);
    starParser.packageFile();

    SearcherIdentifier starSearcher =
        new SearcherIdentifier(starParser.getFinalFile(), "no", "nope", false);

    Throwable colException =
        assertThrows(
            IOException.class,
            () -> {
              starSearcher.search();
            });
    assertEquals("Column requested is not found", colException.getMessage());
  }

  /**
   * Test for the case where the column identifier is wrongly stated to be an index when it is not
   * an integer
   *
   * @throws IOException – IOException thrown by FileReader, passed up to main() in full code
   */
  @Test
  public void indexWrongSearch() throws IOException {
    Reader starTest = new FileReader("data/stars/stardata.csv");
    Parser starParser = new Parser(starTest, true);
    starParser.packageFile();

    SearcherIdentifier starSearcher =
        new SearcherIdentifier(starParser.getFinalFile(), "no", "notnum", true);

    Throwable colException =
        assertThrows(
            NumberFormatException.class,
            () -> {
              starSearcher.search();
            });
    assertEquals("For input string: \"notnum\"", colException.getMessage());
  }

  /**
   * Test for the case where the search term exists in the wrong column
   *
   * @throws IOException – IOException thrown by FileReader, passed up to main() in full code
   */
  @Test
  public void wrongColSearch() throws IOException {
    Reader incTest = new FileReader("data/census/income_by_race_edited.csv");
    Parser incParser = new Parser(incTest, true);
    incParser.packageFile();

    SearcherIdentifier incSearcher =
        new SearcherIdentifier(incParser.getFinalFile(), "Total", "Year", false);
    List<List<String>> searchResult = incSearcher.search();

    assertEquals(searchResult, List.of());
  }
}
