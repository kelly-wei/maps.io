## Project Details
Project Name: Maps

Project Description: 
The Maps project tasks us to work with a fictional stakeholder, a real estate appraiser. It incorporates many elements from previous sprints (CSV, Server, Mock and REPL) as the end-user is able to submit commands through a REPL. Some of these commands require API calls so that the end-user can load, view, and search a CSV file.

Team Member(s): 
- Karis Ma (jma78): spent 16 hours
- Kelly Wei (kwei8): spent 16 hours

GitHub Repo: https://github.com/cs0320-f23/maps-jma78-kwei8.git

## Design Choices
The Maps project is split into two packages:
### 1. back
#### Server
This package hosts the Server class where there are the following different endpoints:
- <u>/load<u> which loads a CSV file given a pathway
- <u>/search<u> which searches within a loaded CSV file given a keyword and/or a column identifier
- <u>/view<u> which displays the CSV data 
- <u>/broadband<u> which retrieves the broadband data from an API
- <u>/filter_area<u> which retrieves the parsed geoJSON that has relevant data related to the keyword passed in
- <u>/geojson<u> which retrieves the geoJSON

Every Handler returns a Map that contains the requested data or a helpful error message if the data cannot be retrieved. There are three custom exceptions that can be thrown: 
- BadJSONException means there was trouble parsing to/from a Json object, 
- BadRequestException means there was an issue with the user input, and 
- DataSourceException means there was an issue with querying into the Census API. 

BroadBandHandler gets data from CensusDatasource, which implements BroadbandDatasource interface. CensusDatasource makes call the Census API and gets the broadband percentage. If there was an issue with the input or retrieving data form the Census API, it returns custom errors.

#### Parsing
This package also hosts a Parsing folder where the Parser class parses a CSV and ProcessedFile contains all the information about the CSV, including rows and headers. 

#### Searching
This packing hosts the Searching folder. SearcherAll and SearcherIdentifier implement the Searcher interface; SearcherAll searches all rows while SearcherIdentifier searches if the user inputs a header.

#### Caching
Results for user story 3 are cached using Guava, with a size-based cache that takes an argument corresponding to the maximum cache size. The cache is related to the datasource through the proxy pattern, such that the raw datasource can be accessed alone but is ordinarily wrapped by the cache.

### 2. map
Within the map package, you'll find the front-end implementation of our project. Breaking down the components:
- map: This folder hosts the MapBox and overlay information to properly display the graphics for the site
- Mocking: This folder hosts the information to mock data 
- REPL
    - REPL Function: Hosts functions related to the REPL that makes calls to the backend to complete through AccessBackend
        - BroadBandFunc
        - FilterGeoJSONFunc
        - LoadFunc
        - SearchFunc
        - ViewFunch
    - Controlled Input: represents the type of input passed through the input box
    - Input: handles the commands passed in and hosts the REPL History
    - REPL: represents the div hosting both Input and MapBox
    - REPL History: handles keeping track of the user's history and displaying it in a table

For the user's convienence, we pre-loaded the previous ACS_Survey data so it's overlayed using data points. By clicking on the data points, the user is also able to see the name of the area and the median household income for that area. 

Our front-end kept accessibility in mind by using the following strategies:
- Keyboard shortcuts: The user is able to use the following to engage with our app:
        - 'Enter' key - used to submit queries within the input command box,
        - 'Tab' key - used to page through the page, and
        - 'Arrow Up'/'Arrow Down' key - used to navigate the history by mimicking a mouse scrolling.
        - 'Equal' key - used to toggle the Show Commands buttons to view the valid commands.
- Aria-labels and aria-live: If using a screen reader, all elements of our page has attached aria-labels so that the user knows where they are currently at on the page. The aria-live will provide updates as the information comes and populates in.
- TabIndex: This ensures that a user can navigate through the page without having to use a mouse or trackpad.

## Errors/Bugs
There were no errors at this time.


## Tests
Our integration tests all new functionality not previously implemented in previous sprints using both mocked and real data, include state changes. Additionally, we had tests to ensure persistence to satisfy user story 4. Furthermore, we had unit tests on our functions that filtered and random tests on bounding boxes. These began with fuzz testing and broadening to property-based testing that verified accuracy.

We also used Jest and Playwright to make sure the front-end was properly displaying the rendering of commands. 

## How to...
To view the map, direct yourself into the map directory and run 'npm start' and open the back end using the pom.xml file to configure and start the Server class. 

To run tests, please run mvn test 

## SRC Reflection
Your finished Maps product is built using many systems: programming languages, development environments, software packages, hardware, etc. Whose labor do you rely on when you run your capstone demo? Enumerate at least 12 different packages, tools, hardware components, etc. that you implicitly or explicitly used during this week’s work. 

For us to have a successfuly product, we rely on the indivdiuals who contributed to the ideation, product development, and maintenance of the following tools, frameworks, and packages:
1) React, Javascript, Java, TypeScript, HTML/CSS, and any coding languages that the listed depend on
2) Google's Guava caching package
3) Maven
4) Playwright and Jest
5) JUnit Testing Framework
6) IDEs (i.e. Intellij and VSCode)
7) Web browsers to view our React apps on (i.e. Safari, Google Chrome)
8) Existing datasets (i.e. ACS_Survey)
9) Mapbox
10) Github
11) Compiler(s) used 
12) Underlying hardware of our computers (i.e. CPUs, chips, etc.)