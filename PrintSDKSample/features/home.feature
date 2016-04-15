Feature: Home feature

  As an user
	I want to view home screen for Print SDK Sample app 
    and select different options and do print

        
@done
	Scenario: Navigate to Print screen using appium
		Given I am on Home screen
		Then I tap on "Image" option
        And I select preview button 
        Then I tap on "I have one" option
        
        
@done
	Scenario: Verify all titles, values and button present in home screen
		Given I am on "Home" screen
        Then I should see the following options:
         |CONTENT TYPE|
         |PAGE LAYOUT|
         |PAGE MARGINS|
         |UNIQUE DEVICE ID|
        # |Metrics Options|
        # |Unique Device Id Per App|
         
@reset
@done
	Scenario Outline: Verify all titles, values and button present in home screen
		Given I am on "Home" screen
        And "<default_value>" value should be selected
        When I tap on "<new_selection>" value, it should be selected
        Examples:
         |default_value     |new_selection          |
         |Image             |PDF                    |
         |Center Top        |Center                 |
         |Center Top        |Fill                   |
         |Center Top        |Fit                    |
         |Center Top        |Top Left               |
         |None              |1/2 Inch               |
         |None              |Top Only               |
         |Not Encrypted     |Unique Per App          |
         |Not Encrypted     |Unique Per Vendor          |
       #  |Without Metrics   |With Metrics           |
        # |True              |False                  |
         
         
         