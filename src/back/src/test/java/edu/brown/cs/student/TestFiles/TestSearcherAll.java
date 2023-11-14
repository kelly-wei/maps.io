package edu.brown.cs.student.TestFiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.Parsing.Parser;
import edu.brown.cs.student.main.Searching.SearcherAll;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Testing class for SearcherAll
 *
 * @author karismajn
 */
public class TestSearcherAll {

  /**
   * Test to ensure that the searcher can find multiple rows with a basic search term that is
   * present
   *
   * @throws IOException – IOException thrown by packageFile(), passed up to main() in full code
   */
  @Test
  public void basicSearch() throws IOException {
    Reader educTest = new FileReader("data/census/postsecondary_education.csv");
    Parser educParser = new Parser(educTest, true);
    educParser.packageFile();

    SearcherAll educSearcher = new SearcherAll(educParser.getFinalFile(), "Asian");
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
   * Test to ensure case insensitivity in the search term
   *
   * @throws IOException – IOException thrown by packageFile(), passed up to main() in full code
   */
  @Test
  public void caseSearch() throws IOException {
    Reader caseTest = new FileReader("data/census/postsecondary_education.csv");
    Parser caseParser = new Parser(caseTest, true);
    caseParser.packageFile();
    SearcherAll caseSearcher = new SearcherAll(caseParser.getFinalFile(), "aSiAn");
    List<List<String>> caseResult = caseSearcher.search();

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
    assertEquals(caseResult, List.of(educR1, educR2));
  }

  /**
   * Test to ensure that the correct result (an empty list) is returned when the search term is
   * missing
   *
   * @throws IOException – IOException thrown by packageFile(), passed up to main() in full code
   */
  @Test
  public void missingSearch() throws IOException {
    Reader noneTest = new FileReader("data/census/postsecondary_education.csv");
    Parser noneParser = new Parser(noneTest, true);
    noneParser.packageFile();

    SearcherAll noneSearcher = new SearcherAll(noneParser.getFinalFile(), "French");
    List<List<String>> noneResult = noneSearcher.search();

    assertEquals(noneResult, List.of());
  }
}
