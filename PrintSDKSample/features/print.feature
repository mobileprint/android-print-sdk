Feature: Print feature

  As an user
	I want to take and verify print for different options
    for Print SDK Sample app


  @TA12260
Scenario Outline: Print a card and verify xml counters for 'PDF' option
    Given I am on Home screen
    Then I tap on "<Content Type>" option
    And I select layout as "<Layout Type>"
    And I select preview button 
    And I tap on Print in Print Preview screen
    Then I select the printer "_QA Photosmart 6510 series [FD90EC]" if available
    Then I select paper size as "<Paper Size>"
    Then I tap on Printer settings
    Then I select Quality as "<Quality>"
    Then I select Paper Type as "<Paper Type>"
    Then I navigate to back
    Then I fetch xml counters
    Then I tap on Print
    Then I verify counters
    
     #PDF print for 4x6 and 5x7 print has crash issues(defect DE3532)
    
     Examples:
      |Content Type  | Paper Size| Quality   | Paper Type   |Layout Type|
      |Image         | 4x6 in    | Best      | Plain        |Center     |
      |PDF           | Letter    | Normal    | Plain        |Center Top |
      |Image         | 5x7 in    | Best      | Plain        |Fit        |
      |PDF           | 5x7 in    | Draft     | Plain        |Fill       |

        
 	
@TA12260
Scenario Outline: Print a card in Lollipop device and verify xml counters for 'Image' option
    Given I am on Home screen
    Then I tap on "Image" option
    And I select layout as "<Layout Type>"
    And I select preview button 
    And I tap on Print in Print Preview screen
    Then I select the printer "_QA Photosmart 6510 series [FD90EC]" if available
    Then I select paper size as "<Paper Size>"
    Then I tap on Printer settings
    Then I select Quality as "<Quality>"
    Then I select Paper Type as "<Paper Type>"
    Then I navigate to back
    Then I fetch xml counters
    Then I tap on Print
    Then I verify counters
    
     Examples:
        | Paper Size | Quality   | Paper Type   |Layout Type|
        | 4x6 in     | Draft     | Plain        |Center    |
        | 5x7 in     | Normal    | Plain        |Center    |
        | Letter     | Best      | Plain        |Center    |
      
        | 4x6 in     | Draft     | Plain        |Crop      |
        | 5x7 in     | Normal    | Plain        |Crop      |
        | Letter     | Best      | Plain        |Crop      |
        
        | 4x6 in     | Draft     | Plain        |Fit       |
        | 5x7 in     | Normal    | Plain        |Fit       |
        | Letter     | Best      | Plain        |Fit       |
        
        | 4x6 in     | Draft     | Plain        |Top Left  |
        | 5x7 in     | Normal    | Plain        |Top Left  |
        | Letter     | Best      | Plain        |Top Left  |

  @TA13625
  Scenario: Select a pdf and verify print
    Given I am on Home screen
    Then I tap on "PDF" option
    Then I click "SELECT A FILE" button
    Then I select the "pdf"
    And I select layout as "Center"
    Then I tap on "Not Encrypted" option
    And I tap on "PRINT SETTINGS" option
    And I select preview button
    And I check the pop up - Print plugin is present or not
    And I tap on Print in Print Preview screen
    Then I should see "pdf" screen

  @TA13625
  Scenario: Select a image and verify print
    Given I am on Home screen
    Then I tap on "Image" option
    Then I click "SELECT A FILE" button
    Then I select the "photo"
    And I select layout as "Center"
    Then I tap on "Not Encrypted" option
    And I tap on "PRINT SETTINGS" option
    And I select preview button
    And I check the pop up - Print plugin is present or not
    Then I should see "preview" screen
    
    