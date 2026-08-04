Feature: Mobile - Product Color Selection

  # Guest User
  @mobile @colorselection
  Scenario Outline: Guest User: Selecting "<color>" - color carries through to the cart
    When the user adds "Sauce Labs Backpack" in color "<color>" to the cart with quantity <quantity>
    Then a color indicator should be displayed for the product in the cart

    Examples:
      | color | quantity |
      | Blue  | 1        |
      | Gray  | 2        |
      | Green | 3        |
      | Black | 4        |

  # Logged in User
  @mobile @colorselection @require_login
  Scenario Outline: User Logged In: Selecting "<color>" - color carries through to the cart
    When the user adds "Sauce Labs Backpack" in color "<color>" to the cart with quantity <quantity>
    Then a color indicator should be displayed for the product in the cart

    Examples:
      | color | quantity |
      | Blue  | 4        |
      | Gray  | 3        |
      | Green | 2        |
      | Black | 1        |