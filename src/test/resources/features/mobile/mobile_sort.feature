Feature: Mobile - Product Sorting

  # Guest User
  @mobile @sort
  Scenario Outline: Guest User: Sort products by <sortOption>
    When the user sorts products by "<sortOption>"
    Then the visible products should be sorted by "<sortOption>"

    Examples:
      | sortOption       |
      | Name - Ascending |
      | Name - Descending|
      | Price - Ascending|
      | Price - Descending|

  # Logged in User
  @mobile @sort @require_login
  Scenario Outline: User Logged In: Sort products by <sortOption>
    When the user sorts products by "<sortOption>"
    Then the visible products should be sorted by "<sortOption>"

    Examples:
      | sortOption       |
      | Name - Ascending |
      | Name - Descending|
      | Price - Ascending|
      | Price - Descending|