And (/^I check the pop up - Print plugin is present or not$/) do
    $os_version = getOSversion
    if selenium.find_elements(:xpath,"//android.widget.Button[@text='SKIP']").size > 0
           puts "Plugin is not installed!, so I am skipping the pop up to load Print Preview screen"
        selenium.find_element(:xpath,"//android.widget.Button[@text='SKIP']").click
    end
end

Then (/^I select paper size as "([^"]*)"$/) do |paper_size|
    sleep(MAX_TIMEOUT)
    $paper_size = paper_size
    $printer_arr = $paper_arr["paper_arr_printer"]
    $pdf_arr = $paper_arr["paper_arr_pdf"]
    if $paper_size == "4 x 5"
        selenium.back
        wait.until { selenium.find_element(:id,"com.hp.mss.droidphoto:id/paper_size_spinner") }
        selenium.find_element(:id,"com.hp.mss.droidphoto:id/paper_size_spinner").click
        wait.until {selenium.find_elements(:xpath,"//android.widget.CheckedTextView[@text='#{paper_size}']")}
        if selenium.find_elements(:xpath,"//android.widget.CheckedTextView[@text='#{paper_size}']").size > 0
            selenium.find_element(:xpath,"//android.widget.CheckedTextView[@text='#{paper_size}']").click
        else
            raise "Failed to select Paper!"
        end
        macro %Q|I select the printer "#{$PrinterName}" if available|        
    else
        if $os_version >= '5.0'
            wait.until { selenium.find_element(:xpath,"//android.widget.TextView[@text='Paper size:']") }
            selenium.find_element(:xpath,"//android.widget.TextView[@text='Paper size:']").click
        end
        wait.until { selenium.find_element(:id,"com.android.printspooler:id/paper_size_spinner") }
        selenium.find_element(:id,"com.android.printspooler:id/paper_size_spinner").click
        sleep(APPIUM_TIMEOUT)
        if $os_version < '5.0'
           # wait.until {selenium.find_elements(:xpath,"//android.widget.TextView[@text='#{paper_size}']")}
            if selenium.find_elements(:xpath,"//android.widget.TextView[@text='#{paper_size}']").size > 0
                selenium.find_element(:xpath,"//android.widget.TextView[@text='#{paper_size}']").click
            else if selenium.find_elements(:xpath,"//android.widget.TextView[@text='#{$printer_arr["#{paper_size}"]}']").size > 0
                selenium.find_element(:xpath,"//android.widget.TextView[@text='#{$printer_arr["#{paper_size}"]}']").click
            else if selenium.find_elements(:xpath,"//android.widget.TextView[@text='#{$pdf_arr["#{paper_size}"]}']").size > 0
                    selenium.find_element(:xpath,"//android.widget.TextView[@text='#{$pdf_arr["#{paper_size}"]}']").click
            else
                    raise "Failed to select Paper!"
            end
            end
end
        else
            
             # wait.until {selenium.find_elements(:xpath,"//android.widget.CheckedTextView[@text='#{paper_size}']")}
            if selenium.find_elements(:xpath,"//android.widget.CheckedTextView[@text='#{paper_size}']").size > 0
                selenium.find_element(:xpath,"//android.widget.CheckedTextView[@text='#{paper_size}']").click
           else if selenium.find_elements(:xpath,"//android.widget.CheckedTextView[@text='#{$printer_arr["#{paper_size}"]}']").size > 0
                selenium.find_element(:xpath,"//android.widget.CheckedTextView[@text='#{$printer_arr["#{paper_size}"]}']").click
            else if selenium.find_elements(:xpath,"//android.widget.CheckedTextView[@text='#{$pdf_arr["#{paper_size}"]}']").size > 0
                    selenium.find_element(:xpath,"//android.widget.CheckedTextView[@text='#{$pdf_arr["#{paper_size}"]}']").click
           else
                    raise "Failed to select Paper!"
            end
            end
end

            end
    end
end


And (/^I tap on Print in Print Preview screen$/) do
    $os_version = getOSversion
    sleep(15)
    if $os_version < '5.0' && $content_option == 'Image'
        #wait.until { selenium.find_element(:id,"com.hp.mss.printsdksample:id/action_print") }
        wait.until { selenium.find_element(:id,"com.hp.mss.printsdksample:id/print_preview_print_btn")}
        selenium.find_element(:id,"com.hp.mss.printsdksample:id/print_preview_print_btn").click
    end
    #wait.until { selenium.find_element(:xpath,"//android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.support.v7.widget.LinearLayoutCompat[1]/android.widget.TextView[1]") }
    #selenium.find_element(:xpath,"//android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.support.v7.widget.LinearLayoutCompat[1]/android.widget.TextView[1]").click
    
    if $paper_size == "4 x 5" 
        selenium.find_element(:id,"android:id/button1").click  #Click  Continue button
    end
end

Then (/^I select the printer "([^"]*)" if available$/) do |printer_name|
    $os_version = getOSversion
    print_service_helper
    sleep(APPIUM_TIMEOUT)
    if $os_version < '5.0'
        wait.until { selenium.find_element(:id,"com.android.printspooler:id/destination_spinner") }
        selenium.find_element(:id,"com.android.printspooler:id/destination_spinner").click   
    else
        wait.until { selenium.find_element(:id,"com.android.printspooler:id/title") }
        selenium.find_element(:id,"com.android.printspooler:id/title").click
    end
    sleep(APPIUM_TIMEOUT)
    if selenium.find_elements(:name,"#{printer_name}").size > 0
        selenium.find_element(:name,"#{printer_name}").click
    else if selenium.find_elements(:xpath,"//android.widget.TextView[@text='All printers…']").size > 0
        selenium.find_element(:xpath,"//android.widget.TextView[@text='All printers…']").click
        if selenium.find_elements(:name,"#{printer_name}").size > 0
            selenium.find_element(:name,"#{printer_name}").click
        else
            begin
                selenium.scroll_to("#{printer_name}")
            rescue Selenium::WebDriver::Error::NoSuchElementError
                if selenium.find_elements(:name,"#{printer_name}").size > 0
                    selenium.find_element(:name,"#{printer_name}").click
                else
                    raise "Printer not found!"
                end
            end
        end
    else
        raise "Failed to select All Printers!"
    end
    end
end 

Then(/^I tap on Printer settings$/) do
    sleep(APPIUM_TIMEOUT)
    $os_version = getOSversion
    if $os_version > '5.0'
        wait.until { selenium.find_element(:id,"com.android.printspooler:id/more_options_button") }
        selenium.find_element(:id,"com.android.printspooler:id/more_options_button").click
    else
        wait.until { selenium.find_element(:id,"com.android.printspooler:id/advanced_settings_button") }
                selenium.find_element(:id,"com.android.printspooler:id/advanced_settings_button").click
end
end

Then (/^I select Quality as "([^"]*)"$/) do |quality|
    
    sleep(APPIUM_TIMEOUT)
    $Quality = quality
    #wait.until { selenium.find_element(:xpath,"//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.ScrollView[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.Spinner[1]/android.widget.TextView[1]") }
    #selenium.find_element(:xpath,"//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.ScrollView[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.Spinner[1]/android.widget.TextView[1]").click

    wait.until{selenium.find_element(:id,"com.hp.android.printservice:id/quality_spinner")}
    selenium.find_element(:id,"com.hp.android.printservice:id/quality_spinner").click
    wait.until {selenium.find_elements(:xpath,"//android.widget.ListView[1]/android.widget.CheckedTextView[@text='#{quality}']")}
       if selenium.find_elements(:xpath,"//android.widget.CheckedTextView[@text='#{quality}']").size > 0
        selenium.find_element(:xpath,"//android.widget.CheckedTextView[@text='#{quality}']").click
        else
            raise "Failed to select Quality!"
        end
    
    sleep(MAX_TIMEOUT)
end

Then (/^I select Paper Type as "([^"]*)"$/) do |paperType|
    sleep(APPIUM_TIMEOUT)
    $PaperType = paperType
    wait.until { selenium.find_element(:xpath,"//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.ScrollView[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.Spinner[1]/android.widget.TextView[1]") }
    selenium.find_element(:xpath,"//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.ScrollView[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.Spinner[1]/android.widget.TextView[1]").click
    
    wait.until {selenium.find_elements(:xpath,"//android.widget.ListView[1]/android.widget.CheckedTextView[@text='#{paperType}']")}
        if selenium.find_elements(:xpath,"//android.widget.CheckedTextView[@text='#{paperType}']").size > 0
        selenium.find_element(:xpath,"//android.widget.CheckedTextView[@text='#{paperType}']").click
        else
            raise "Failed to select Paper Type!"
        end
    
    sleep(MAX_TIMEOUT)
end
Then(/^I save the pdf$/) do
    sleep(5.0)
    $target = ""
    if $device_brand.include? "generic"
        $target = "Emulator"
    end
    if selenium.find_elements(:xpath,"//android.widget.TextView[@text='Recent']").size > 0
         selenium.find_element(:xpath,"//android.widget.TextView[@text='Downloads']").click
     end
    sleep(5.0)
    selenium.find_element(:xpath,"//android.widget.Button[@text='Save']").click
     
end

$paper_arr =

{
    "paper_arr_printer" => {
        "4x6 in" => "Photo-4x6 in",
        "5x7 in" => "Photo-5x7 in",
        "Legal" => "Main-Legal",
        "Letter" => "Main-Letter"
       },
    "paper_arr_pdf" => {
        "4x6 in" => "Index Card 4x6",
        "5x8 in" => "Index Card 5x8",
        "Legal" => "Main-Legal",
        "Letter" => "Main-Letter"
       }
 }

Then(/^I navigate back to PrintPod screen$/) do
    macro %Q|I navigate back|
    if $os_version.to_f < 5.0           
        macro %Q|I navigate back|
    end
end