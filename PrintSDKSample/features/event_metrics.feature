Feature: Event metrics feature

  As an user
	I want to take and verify event metrics for different options
    for Print SDK Sample app
        
@reset
@TA13810
Scenario Outline: Print an Image/PDF and verify event metrics
    Given I am on Home screen
    Then I tap on "<Content>" option
    And I select layout as "Center"
    Then I tap on "<Device_id>" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I tap on "PRINT SETTINGS" option
    And I select preview button 
    And I tap on Print in Print Preview screen
    Then I Fetch event metrics details
    And I check the print session id is "1"
    And I check the event count is "1"
    And I check the event type id is "1"
    Then I select the printer "_QA Photosmart 6510 series [FD90EC]" if available
    Then I select paper size as "<Paper Size>"
    Then I tap on Print
    Then I Fetch event metrics details
    And I check the paper size
    And I check the paper type is "<Paper Type>"
    And I check the device id   
    And I check the product name is "Print SDK"    
    And I check the version
    And I check the os_type
    And I check the os version   
    And I check the product id is "com.hp.mss.printsdksample"
    And I check the print library version
    And I check the print session id is "1"
    And I check the event count is "1"
    And I check the event type id is "5"
    And I check the os version
          
     Examples:
        |Content| Paper Size  | Paper Type| Device_id        |
        |PDF     | Letter     | Document  | Not Encrypted    |
        |Image   | 4x6 in     | Photo     | Unique Per App   |
        |Image   | 5x7 in     | Photo     | Unique Per Vendor|



