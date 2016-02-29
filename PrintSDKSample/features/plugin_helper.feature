Feature: Plugin feature

         
@TA13019
	Scenario: verify plugin helper screen 
		Given I am on Home screen
        And I tap on plugin helper button 
        Then I should see "Print Service Manager" screen
        #Then I should see the skip option
        And I should see the following
        
        |HP Print Service Plugin    | 
        |Mopria Print Service       |
        |Brother Print Service Plugin|
        |Canon Print Service|
        |Epson Print Enabler|
        |Other PrintServicePlugin|
    
@TA13019
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
    
@TA13019
	Scenario Outline: Verify enable plugin pop up
		Given I am on Home screen
        And I tap on plugin helper button 
        Then I should see "Print Service Manager" screen
        Then I tap to enable "<plugin_name>" if installed
        Then I should see "Enable Your Plugin" pop up
        
        Examples:
        
        |plugin_name|
        |HP Print Service Plugin    | 
        |Mopria Print Service       |
        |Brother Print Service Plugin|
        |Canon Print Service|
        |Epson Print Enabler|
        |Other PrintServicePlugin|
        
        