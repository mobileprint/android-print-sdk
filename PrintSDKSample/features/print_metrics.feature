Feature: Print metrics feature

  As an user
	I want to take and verify print metrics for different options
    for Print SDK Sample app 
    

@lollipop
@TA12260
Scenario Outline: Print a card in Lollipop device and verify print metrics for print with metrics option
    Given I am on Home screen
    Then I tap on "<Content>" option
    And I select layout as "Center"
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I select preview button 
    Then I tap on "I have one" option
    Then I select the printer "HP ENVY 5540 series" if available
    Then I select paper size as "<Paper Size>"
    Then I get black and white filter value and number of copies
    Then I tap on Print
    Then Fetch metrics details
    And I check the paper size
    And I check the paper type is "<Paper Type>"
    #And I check the manufacturer name
    And I check the os_type
    #And I check the version
    And I check the print_plugin_tech is "com.hp.android.printservice"
    And I check the print_result is "Success"
    And I check the product name is "PrintSDKSample"
	#And I check the device brand
	And I check the off ramp is "Android Print"
	And I check the device type
	And I check the os version
    And I check the device id
   # And I check the wifi ssid
    And I check the black and white filter
    And I check the number of copies
    
     #PDF print for 4x6 and 5x7 print has crash issues(defect DE3532)
        
     Examples:
        |Content| Paper Size  | Paper Type|
        #|PDF     | 4x6 in     | Document  |
        |PDF     | Letter     | Document  |
        |Image   | 4x6 in     | Photo     |
        |Image   | 5x7 in     | Photo     |
        
        
        
@lollipop
@TA12260
Scenario Outline: Print a card in Lollipop device and verify print metrics for Cancel print
    Given I am on Home screen
    Then I tap on "<Content>" option
    And I select layout as "Center"
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I select preview button 
    Then I tap on "I have one" option
    And I cancel the print
    Then Fetch metrics details
    And I check print result is "Cancel"
   
        
     Examples:
        |Content| 
        |PDF    | 
        |Image  | 
        
@lollipop
Scenario Outline: Print a card in Lollipop device and verify print metrics not generated for "without metrics" option
    Given I am on Home screen
    Then I tap on "<Content>" option
    And I select layout as "Center"
    Then I select Without Metrics option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I select preview button 
    Then I tap on "I have one" option
    Then I select the printer "HP ENVY 5540 series" if available
    Then I select paper size as "<Paper Size>"
    Then I get black and white filter value and number of copies
    Then Fetch metrics details
    Then I tap on Print
    And I verify metrics not generated for current print
   
        
     Examples:
        |Content| Paper Size|
        |PDF    | Letter|
        |Image  | Letter|
