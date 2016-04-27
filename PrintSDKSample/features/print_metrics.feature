Feature: Print metrics feature

  As an user
	I want to take and verify print metrics for different options
    for Print SDK Sample app


@printmetrics
Scenario Outline: Print an Image/PDF and verify print metrics
    Given I am on Home screen
    Then I tap on "<Content>" option
    And I select layout as "Center"
    Then I tap on "<Device_id>" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I tap on "PRINTING HELP" option
    And I tap on print plugin manager
    Then I get installed and enabled plugin count
    Then I navigate back
     And I tap on "PRINT SETTINGS" option
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
    And I check the product name is "Print SDK"
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
        |Content| Paper Size  | Paper Type| Device_id        |
        |PDF     | Letter     | Document  | Not Encrypted    |
        |Image   | 4x6 in     | Photo     | Unique Per App   |
        |Image   | 5x7 in     | Photo     | Unique Per Vendor|



    @done
    @printmetrics
Scenario Outline: Verify print metrics for Cancel print
    Given I am on Home screen
    Then I tap on "<Content>" option
    And I select layout as "Center"
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I select preview button
    And I cancel the print
    Then Fetch metrics details
    And I check print result is "Cancel"
   
        
     Examples:
        |Content| 
        |PDF    | 
        |Image  |

 #feature removed from sample app
@blocked
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



    @printmetrics
    Scenario Outline: Print an image/PDF device and verify device id
    Given I am on Home screen
    Then I tap on "<Content>" option
    Then I tap on "<Device_id>" option
    And I select preview button 
    And I tap on Print in Print Preview screen
    Then I select the printer "_QA Photosmart 6510 series [FD90EC]" if available
    Then I tap on Print
    Then Fetch metrics details
    And I check the device id
   
    
     Examples:
        |Content |  Device_id       |
        | Image  | Not Encrypted    |
        | PDF    | Unique Per App   |
        | Image  | Unique Per Vendor|



@reset
@done
@printmetrics
Scenario Outline: Save to PDF and verify print metrics
    Given I am on Home screen
    Then I tap on "<Content>" option
    And I select layout as "Center"
    Then I tap on "<Device_id>" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I tap on "PRINTING HELP" option
    And I tap on print plugin manager
    Then I get installed and enabled plugin count
    Then I navigate back
     And I tap on "PRINT SETTINGS" option
    And I select preview button 
    And I check the pop up - Print plugin is present or not
    And I tap on Print in Print Preview screen
    Then I select the printer "Save as PDF" if available
   # Then I select paper size as "<Paper Size>"
    Then I tap on Print
    Then I save the pdf
    Then Fetch metrics details
    And I check the device id
    And I check the off ramp is "Android Print"
    And I check timestamp is not null
    And I check the product name is "Print SDK"
    And I check the version
    And I check the os_type
    And I check the os version
    And I check the print_plugin_tech is "com.android.printspooler"
    And I check the printer id   
    And I check the wifi ssid
    And I check the product id is "com.hp.mss.printsdksample"
    And I check the print library version
    And I check the app_type is "Partner"
    And I check print result is "Success"
    And I check the route taken is "print-metrics-test.twosmiles.com"
    And I check the country code
    And I check the language code
    And I check the number of installed plugins
    And I check the number of enabled plugins
    And I check the custom data
    And I delete the generated pdf
    
        
     Examples:
        |Content| Device_id           |Paper Size  |
        |PDF    | Not Encrypted       |Letter     |
        |Image  | Unique Per App      |4x6 in     |
        |Image  | Unique Per Vendor   |4x6 in     |

    