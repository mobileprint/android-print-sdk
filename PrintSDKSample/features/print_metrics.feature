Feature: Print metrics feature

  As an user
	I want to take and verify print metrics for different options
    for Print SDK Sample app


 @done
@printmetrics
Scenario Outline: Print an Image/PDF with metrics option and verify print metrics
    Given I am on Home screen
    Then I tap on "<Content>" option
    And I select layout as "Center"
    Then I tap on "With Metrics" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I tap on plugin helper button
    Then I get the enabled plugin count
    Then I navigate back
    And I select preview button 
    And I tap on Print in Print Preview screen
    Then I select the printer "_QA Photosmart 6510 series [FD90EC]" if available
    Then I select paper size as "<Paper Size>"
    Then I get black and white filter value and number of copies
    Then I tap on Print
    Then Fetch metrics details
    And I check the paper size
    And I check the paper type is "<Paper Type>"
   # And I check the manufacturer name
    And I check the os_type
   # And I check the version
    And I check the print_plugin_tech is "com.hp.android.printservice"
    And I check the print_result is "Success"
    And I check the product name is "PrintSDKSample"
	#And I check the device brand
	And I check the off ramp is "Android Print"
	And I check the device type
	And I check the os version
    And I check the device id
    And I check the wifi ssid
    And I check the black and white filter
    And I check the number of copies
    And I check the print library version
    And I check the content type is "<Content>"
    And I check the app_type is "Partner"
    And I check print result is "Success"
    And I check the number of installed plugins
    And I check the number of enabled plugins
    
     #PDF print for 4x6 and 5x7 print has crash issues(defect DE3532)
        
     Examples:
        |Content| Paper Size  | Paper Type|
        |PDF     | 4x6 in     | Document  |
        |PDF     | Letter     | Document  |
        |Image   | 4x6 in     | Photo     |
        |Image   | 5x7 in     | Photo     |



    @done
Scenario Outline: Verify print metrics for Cancel print
    Given I am on Home screen
    Then I tap on "<Content>" option
    And I select layout as "Center"
    Then I tap on "With Metrics" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I select preview button
    And I cancel the print
    Then Fetch metrics details
    And I check print result is "Cancel"
   
        
     Examples:
        |Content| 
        |PDF    | 
        |Image  | 
        
@printmetrics
@done
Scenario Outline: Print an Image/PDF with Without Metrics option and verify print metrics
    Given I am on Home screen
    Then I tap on "<Content>" option
    And I select layout as "Center"
    Then I tap on "Without Metrics" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I select preview button 
    And I tap on Print in Print Preview screen
    Then I select the printer "_QA Photosmart 6510 series [FD90EC]" if available
    Then I select paper size as "<Paper Size>"
    Then I get black and white filter value and number of copies
    Then Fetch metrics details
    Then I tap on Print
    And I verify metrics not generated for current print
   
        
     Examples:
        |Content| Paper Size|
        |PDF    | Letter|
        |Image  | Letter|


    @done
    @printmetrics
    Scenario Outline: Print an image/PDF device and verify device id
    Given I am on Home screen
    Then I tap on "<Content>" option
    And I select layout as "Center"
    Then I tap on "With Metrics" option
    Then I tap on "<Device_ip_per_app>" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I select preview button 
    And I tap on Print in Print Preview screen
    Then I select the printer "_QA Photosmart 6510 series [FD90EC]" if available
    Then I select paper size as "<Paper Size>"
    Then I tap on Print
    Then Fetch metrics details
    And I check the device id
   
    
     Examples:
        |Content| Paper Size  |Device_ip_per_app|
        |PDF     | 4x6 in     | True            |
        |Image   | 5x7 in     | False           |


    @done
    @printmetrics
    Scenario: Print a PDF with unique device id on & off options and verify device id
    Given I am on Home screen
    Then I tap on "PDF" option
    And I select layout as "Center"
    Then I tap on "With Metrics" option
    Then I tap on "True" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I select preview button 
    And I tap on Print in Print Preview screen
    Then I select the printer "_QA Photosmart 6510 series [FD90EC]" if available
    Then I select paper size as "4x6 in"
    Then I tap on Print
    Then Fetch metrics details
    And I check the device id
    Then I navigate to home screen
    Then I tap on "False" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I select preview button 
    And I tap on Print in Print Preview screen
    Then I select the printer "_QA Photosmart 6510 series [FD90EC]" if available
    Then I select paper size as "5x7 in"
    Then I tap on Print
    Then Fetch metrics details
    And I check the device id
   
    