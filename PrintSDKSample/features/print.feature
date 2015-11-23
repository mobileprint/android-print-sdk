Feature: Print feature

  As an user
	I want to take and verify print for different options
    for Print SDK Sample app 
    

@TA12260
@lollipop
@kitkat
Scenario Outline: Print a card in Lollipop device and verify xml counters for 'PDF' option
    Given I am on Home screen
    Then I tap on "PDF" option
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
        | Paper Size| Quality   | Paper Type   |Layout Type|
        | 4x6 in    | Draft     | Plain        |Center    |
        | 4x6 in    | Normal    | Plain        |Center    |
        | 4x6 in    | Best      | Plain        |Center    |
        | 5x7 in    | Draft     | Plain        |Center    |
        | 5x7 in    | Normal    | Plain        |Center    |
        | 5x7 in    | Best      | Plain        |Center    |
        | Letter    | Draft     | Plain        |Center    |
        | Letter    | Normal    | Plain        |Center    |
        | Letter    | Best      | Plain        |Center    |
        
 	
@TA12260
@lollipop
@kitkat
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
        
   