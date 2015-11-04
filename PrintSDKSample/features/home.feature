Feature: Home feature

  As an user
	I want to view home screen for Print SDK Sample app 
    and select different options and do print
	
@done
@TA12015
	Scenario: Navigate to home screen
		Given I am on "Home" screen
		Then I select "Image" option
        And I select "Preview" option 
        Then I select "I have one" option 
        
@done
@TA12015
	Scenario: Navigate to home screen using appium
		Given I am on Home screen
		Then I tap on "Image" option
        And I select preview button 
        Then I tap on "I have one" option