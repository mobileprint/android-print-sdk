Feature: Event metrics feature

  As an user
	I want to verify event metrics for different options and events for Print SDK Sample app
        
@reset
@TA13810
@lollipop
Scenario: Print an Image/PDF and verify event metrics
    Given I am on Home screen
    Then I tap on "Image" option
    And I select layout as "Center"
    Then I tap on "Unique Per App" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I tap on "PRINT SETTINGS" option
    And I select preview button 
    And I tap on Print in Print Preview screen
    Then I Fetch event metrics details
    And I get the event metrics for "entered_print_sdk"
    And I check the print session id is "1"
    And I check the event count is "1"
    And I check the event type id is "1"
    And I check the device id
    And I check the device type
    And I check the os_type
    And I check the os version
    And I check the app_type is "Partner"
    And I check timestamp is not null
    And I check the product name is "Print SDK"
    And I check the product id is "com.hp.mss.printsdksample"
    And I check the print library version
    And I get the event metrics for "sent_to_print_dialog"
    And I check the print session id is "1"
    And I check the event count is "1"
    And I check the event type id is "5"
    And I check the device id
    And I check the device type
    And I check the os_type
    And I check the os version
    And I check the app_type is "Partner"
    And I check timestamp is not null
    And I check the product name is "Print SDK"
    And I check the product id is "com.hp.mss.printsdksample"
    And I check the print library version
    Then I navigate back to PrintPod screen
    And I select preview button 
    And I tap on Print in Print Preview screen
    Then I Fetch event metrics details
    And I get the event metrics for "sent_to_print_dialog"
    And I check the print session id is "2"
    And I check the event count is "2"
    And I check the event type id is "5"
    And I check the device id
    And I check the device type
    And I check the os_type
    And I check the os version
    And I check the app_type is "Partner"
    And I check timestamp is not null
    And I check the product name is "Print SDK"
    And I check the product id is "com.hp.mss.printsdksample"
    And I check the print library version
    

@reset
@kitkat
@TA13810
Scenario: Print an Image/PDF and verify event metrics
    Given I am on Home screen
    Then I tap on "Image" option
    And I select layout as "Center"
    Then I tap on "Unique Per App" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I tap on "PRINT SETTINGS" option
    And I select preview button 
    Then I Fetch event metrics details
    And I get the event metrics for "entered_print_sdk"
    And I check the print session id is "1"
    And I check the event count is "1"
    And I check the event type id is "1"    
    And I check the device id
    And I check the device type
    And I check the os_type
    And I check the os version
    And I check the app_type is "Partner"
    And I check timestamp is not null
    And I check the product name is "Print SDK"
    And I check the product id is "com.hp.mss.printsdksample"
    And I check the print library version
    And I get the event metrics for "opened_preview"
    And I check the print session id is "1"
    And I check the event count is "1"
    And I check the event type id is "4"    
    And I check the device id
    And I check the device type
    And I check the os_type
    And I check the os version
    And I check the app_type is "Partner"
    And I check timestamp is not null
    And I check the product name is "Print SDK"
    And I check the product id is "com.hp.mss.printsdksample"
    And I check the print library version
    And I tap on Print in Print Preview screen
    Then I Fetch event metrics details
    And I get the event metrics for "sent_to_print_dialog"
    And I check the print session id is "1"
    And I check the event count is "1"
    And I check the event type id is "5"
    And I check the device id
    And I check the device type
    And I check the os_type
    And I check the os version
    And I check the app_type is "Partner"
    And I check timestamp is not null
    And I check the product name is "Print SDK"
    And I check the product id is "com.hp.mss.printsdksample"
    And I check the print library version
    Then I navigate back to PrintPod screen
    And I select preview button 
    And I tap on Print in Print Preview screen
    Then I Fetch event metrics details
    And I get the event metrics for "sent_to_print_dialog"
    And I check the print session id is "2"
    And I check the event count is "2"
    And I check the event type id is "5"
    And I check the device id
    And I check the device type
    And I check the os_type
    And I check the os version
    And I check the app_type is "Partner"
    And I check timestamp is not null
    And I check the product name is "Print SDK"
    And I check the product id is "com.hp.mss.printsdksample"
    And I check the print library version
    
		
#Need to run on a device where atleast one plugin need to be enabled 
@reset
@TA13810
  Scenario: Verify event metrics for plugin manager
    Given I am on Home screen
    Then I tap on "Image" option
    And I select layout as "Center"
    Then I tap on "Not Encrypted" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I tap on "PRINTING HELP" option
    And I tap on print plugin manager
    Then I Fetch event metrics details
    And I get the event metrics for "opened_plugin_helper"
    And I check the print session id is "1"
    And I check the event count is "1"
    And I check the event type id is "2"
    And I check the device id
    And I check the device type
    And I check the os_type
    And I check the os version
    And I check the app_type is "Partner"
    And I check timestamp is not null
    And I check the product name is "Print SDK"
    And I check the product id is "com.hp.mss.printsdksample"
    And I check the print library version
    Then I should see "Print Service Manager" screen
    Then I tap to "enable" a plugin
    Then I should see "Enable Your Plugin" pop up
    And I tap on "GO TO SETTINGS" option
    Then I Fetch event metrics details
    And I get the event metrics for "sent_to_print_setting"
    And I check the print session id is "1"
    And I check the event count is "1"
    And I check the event type id is "6"
    And I check the device id
    And I check the device type
    And I check the os_type
    And I check the os version
    And I check the app_type is "Partner"
    And I check timestamp is not null
    And I check the product name is "Print SDK"
    And I check the product id is "com.hp.mss.printsdksample"
    And I check the print library version
    
    
#Need to run on a device where atleast one plugin need to be installed 
@reset
@TA13810
  Scenario: Verify event metrics for plugin manager
    Given I am on Home screen
    Then I tap on "Image" option
    And I select layout as "Center"
    Then I tap on "Unique Per App" option
    And I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand
    And I tap on "PRINTING HELP" option
    And I tap on print plugin manager
    Then I tap to "download" a plugin
    Then I Fetch event metrics details
    And I get the event metrics for "sent_to_google_play_store"
    And I check the print session id is "1"
    And I check the event count is "2"
    And I check the event type id is "3"
    And I check the device id
    And I check the device type
    And I check the os_type
    And I check the os version
    And I check the app_type is "Partner"
    And I check timestamp is not null
    And I check the product name is "Print SDK"
    And I check the product id is "com.hp.mss.printsdksample"
    And I check the print library version

