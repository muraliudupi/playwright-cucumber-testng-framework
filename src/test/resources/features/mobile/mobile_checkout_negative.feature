Feature: Mobile Checkout Field Validation

  @mobile @checkout @negative @require_login
  Scenario: Shipping form shows validation errors when submitted empty
    When the user adds a product to the cart using data key "Cart_001" sheet "MobileCartData"
    And the user proceeds to checkout using the cart's current contents
    And the user submits the shipping form without entering any values
    Then the following checkout validation messages are displayed:
      | Please provide your full name. |
      | Please provide your address.   |
      | Please provide your city.      |
      | Please provide your zip        |
      | Please provide your            |

  @mobile @checkout @negative @require_login
  Scenario: Payment form shows validation errors when submitted empty, billing same as shipping
    When the user adds a product to the cart using data key "Cart_001" sheet "MobileCartData"
    And the user proceeds to checkout using the cart's current contents
    And the user enters valid shipping details
    And the user submits the payment form without entering any values
    Then the following checkout validation messages are displayed:
      | Value looks invalid. |

  @mobile @checkout @negative @require_login
  Scenario: Payment form shows both card and billing address errors when billing differs and form is empty
    When the user adds a product to the cart using data key "Cart_001" sheet "MobileCartData"
    And the user proceeds to checkout using the cart's current contents
    And the user enters valid shipping details
    And the user unchecks billing same as shipping
    And the user submits the payment form without entering any values
    Then the following checkout validation messages are displayed:
      | Value looks invalid.           |
      | Please provide your full name. |
      | Please provide your address.   |
      | Please provide your city.      |
      | Please provide your zip        |
      | Please provide your            |

  @mobile @checkout @negative @guest
  Scenario Outline: Guest User: Guest checkout, log in mid-flow, validation error on empty username/password
    Given the user has a product already added to the cart using data key "CheckoutGuest_001" sheet "MobileCheckoutGuestData"
    When the user reaches mobile login screen
    When the user attempts login with username "<username>" and password "<password>"
    Then the login error "<message>" should be displayed

    Examples:
      | username | password | message              |
      |          |          | Username is required |
      |          | demo     | Username is required |
      | john     |          | Enter Password       |