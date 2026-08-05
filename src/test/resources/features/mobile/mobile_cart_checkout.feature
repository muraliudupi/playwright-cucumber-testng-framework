Feature: Mobile - Cart and Checkout

  # Logged in User
  @mobile @cart @require_login
  Scenario: User Logged In: Add a product to the cart with label and quantity from test data
    When the user adds a product to the cart using data key "Cart_001" sheet "MobileCartData"
    Then the product should be visible in the cart

  @mobile @cart @remove @require_login
  Scenario: User Logged In: Remove a product from the cart
    Given the user has a product already added to the cart using data key "Cart_001" sheet "MobileCartData"
    When the user removes the added product from the cart
    Then the removed product should no longer be visible in the cart

  @mobile @cart @checkout @require_login
  Scenario: User Logged In: Complete checkout & Order products.
    When the user proceeds to checkout and completes the order using data key "Checkout_001" sheet "MobileCheckoutData"
    Then the order confirmation should be displayed

  @mobile @cart @checkout @require_login @diff
  Scenario: User Logged In: Complete checkout, different billing address & Order products.
    When the user proceeds to checkout with different billing address and completes the order using data key "Checkout_002" sheet "MobileCheckoutData"
    Then the order confirmation should be displayed

  # Guest User
  @mobile @cart @guest
  Scenario: Guest User: Add a product to the cart with label and quantity from test data
    When the user adds a product to the cart using data key "Cart_001" sheet "MobileCartData"
    Then the product should be visible in the cart

  @mobile @cart @remove @guest
  Scenario: Guest User: Remove a product from the cart
    Given the user has a product already added to the cart using data key "Cart_001" sheet "MobileCartData"
    When the user removes the added product from the cart
    Then the removed product should no longer be visible in the cart

  @mobile @cart @checkout @guest
  Scenario: Guest User: Guest checkout, log in mid-flow, and complete the order
    Given the user has a product already added to the cart using data key "CheckoutGuest_001" sheet "MobileCheckoutGuestData"
    When the user proceeds to checkout as a guest and completes the order using data key "CheckoutGuest_001" sheet "MobileCheckoutGuestData"
    Then the order confirmation should be displayed