Feature: Mobile Cart and Checkout

  @mobile @cart @require_login
  Scenario: User Logged In: Add a product to the cart with label and quantity from test data
    When the user adds a product to the cart using data key "Cart_001" sheet "MobileCartData"
    Then the product should be visible in the cart

  #Not Working
  @mobile @cart @remove @require_login
  Scenario: User Logged In: Remove a product from the cart
    Given the user has a product already added to the cart using data key "Cart_001" sheet "MobileCartData"
    When the user removes the added product from the cart
    Then the removed product should no longer be visible in the cart

  @mobile @cart @checkout @require_login
  Scenario: User Logged In: Complete checkout & Order products.
    When the user proceeds to checkout and completes the order using data key "Checkout_001" sheet "MobileCheckoutData"
    Then the order confirmation should be displayed


  @mobile @cart @guest
  Scenario: Guest User: Add a product to the cart with label and quantity from test data
    When the user adds a product to the cart using data key "Cart_001" sheet "MobileCartData"
    Then the product should be visible in the cart

  #Not Working
  @mobile @cart @remove @guest
  Scenario: Guest User: Remove a product from the cart
    Given the user has a product already added to the cart using data key "Cart_001" sheet "MobileCartData"
    When the user removes the added product from the cart
    Then the removed product should no longer be visible in the cart

  @mobile @cart @checkout @guest
  Scenario: Checkout as a guest, log in mid-flow, and complete the order
    Given the user has a product already added to the cart using data key "CheckoutGuest_001" sheet "MobileCheckoutGuestData"
    When the user proceeds to checkout as a guest and completes the order using data key "CheckoutGuest_001" sheet "MobileCheckoutGuestData"
    Then the order confirmation should be displayed