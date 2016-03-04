Feature: Pre-condition: Print Plugin needs to be disabled/uninstalled to verify this feature from preview option

         
@done
	Scenario: verify plugin helper screen 
		Given I am on Home screen
        And I tap on "PRINTING HELP" option
        And I tap on plugin helper button 
        Then I should see "Print Service Manager" screen
        And I should see the following
        
        |HP Print Service Plugin    | 
        |Mopria Print Service       |
        |Brother Print Service Plugin|
        |Canon Print Service|
        |Epson Print Enabler|
        |Other PrintServicePlugin|

        @regression
	Scenario: verify plugin helper screen  from Preview
		Given I am on Home screen
        And I select preview button
        Then I should see "Print Service Manager" screen
        Then I should see the skip option
        And I should see the following
        
        |HP Print Service Plugin    | 
        |Mopria Print Service       |
        |Brother Print Service Plugin|
        |Canon Print Service|
        |Epson Print Enabler|
        |Other PrintServicePlugin|

        @done
	Scenario Outline: Verify enable plugin pop up
		Given I am on Home screen
        And I tap on "PRINTING HELP" option
        And I tap on plugin helper button 
        Then I should see "Print Service Manager" screen
        Then I tap to enable "<plugin_name>" if installed
        Then I should see "Enable Your Plugin" pop up
        
        Examples:
        
        |plugin_name|
        |HP Print Service Plugin    | 
        |Mopria Print Service       |
#        |Brother Print Service Plugin|
#        |Canon Print Service|
#        |Epson Print Enabler|
#        |Other PrintServicePlugin|
        
        