Feature: calculations

  Background: adding data into the database
    Given calculations table
      | firstNum | firstNumSystem | secondNum | secondNumSystem | operation | result | executionTime       |
      | 10       | 10             | 20        | 10              | +         | 30     | 2023-11-11T14:13:22 |
      | 101      | 2              | 11        | 2               | -         | 10     | 2023-12-11T16:13:22 |
      | 15       | 8              | 56        | 8               | *         | 1126   | 2023-12-11T17:13:22 |
      | 3242FD   | 16             | 435C      | 16              | /         | BF     | 2023-12-11T18:13:22 |

  @Before
  Scenario: get all calculations according to given data
    When I perform get request "http://localhost:8080/list"
    Then I should receive response status code 200
    And Body should contain calculations that are given into the table

  @Before
  Scenario Outline: get the result of calculation
    When I perform get request "http://localhost:8080/result/" with parameters <firstNum> and <secondNum> and <operation> and <system>
    Then I see the result as <result>
    Examples:
      | firstNum | secondNum | operation | system | result |
      | 10       | 20        | +         | 10     | 30     |

  @Before
  Scenario: post new calculation in database
    When I perform get request "http://localhost:8080/calculate" and I pass below body
      | firstNum | firstNumSystem | secondNum | secondNumSystem | operation | result | executionTime             |
      | 234F     | 16             | 45AD      | 16              | *         | 99c2763| 2023-12-11T18:42:22 |
    Then I should receive status code 200
    And I should receive body that given into the table

  @Before
  Scenario: post new calculation in database with specific date
    When I perform get request "http://localhost:8080/calculate" and I pass this date 2023-12-11T13:14:17 and below body
      | firstNum | firstNumSystem | secondNum | secondNumSystem | operation | result |
      | 48       | 10             | 6         | 10              | /         | 8      |
    Then I receive status code 200
    And I receive body that given into the table and with given date




    
