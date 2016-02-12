And (/^I check the pop up - Print plugin is present or not$/) do
    $os_version = getOSversion
    if $os_version < '5.0'
        if selenium.find_elements(:id, 'android:id/alertTitle').size() > 0
            selenium.find_element(:xpath,"//android.widget.Button[@text='CONTINUE']").click
        else
        end
    else
        begin
            if selenium.find_element(:xpath,"//android.widget.TextView[@text='HP Print Plugin']").displayed? == true
                selenium.find_element(:xpath,"//android.widget.Button[@text='CONTINUE']").click
            end
        rescue Selenium::WebDriver::Error::NoSuchElementError
        end
        selenium.find_element(:id,"android:id/button1").click
    end
end


Then (/^I select paper size as "([^"]*)"$/) do |paper_size|
    sleep(APPIUM_TIMEOUT)
    $os_version = getOSversion
    $paper_size = paper_size
    if $os_version < '5.0'
        wait.until { selenium.find_element(:id,"com.android.printspooler:id/paper_size_spinner") }
        selenium.find_element(:id,"com.android.printspooler:id/paper_size_spinner").click   
        wait.until {selenium.find_elements(:xpath,"//android.widget.TextView[@text='#{paper_size}']")}
        if selenium.find_elements(:xpath,"//android.widget.TextView[@text='#{paper_size}']").size > 0
        selenium.find_element(:xpath,"//android.widget.TextView[@text='#{paper_size}']").click
        else
            raise "Failed to select Paper!"
        end
    else
        wait.until { selenium.find_element(:xpath,"//android.widget.TextView[@text='Paper size:']") }
        selenium.find_element(:xpath,"//android.widget.TextView[@text='Paper size:']").click
        wait.until { selenium.find_element(:id,"com.android.printspooler:id/paper_size_spinner") }
        selenium.find_element(:id,"com.android.printspooler:id/paper_size_spinner").click       
        wait.until {selenium.find_elements(:xpath,"//android.widget.CheckedTextView[@text='#{paper_size}']")}
        if selenium.find_elements(:xpath,"//android.widget.CheckedTextView[@text='#{paper_size}']").size > 0
        selenium.find_element(:xpath,"//android.widget.CheckedTextView[@text='#{paper_size}']").click
        else
            raise "Failed to select Paper!"
        end
    end
end


And (/^I tap on Print in Print Preview screen$/) do
    $os_version = getOSversion
    sleep(15)
    if $os_version < '5.0'
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
    wait.until { selenium.find_element(:xpath,"//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.ScrollView[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.LinearLayout[1]/android.widget.Spinner[1]/android.widget.TextView[1]") }
    selenium.find_element(:xpath,"//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.ScrollView[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.LinearLayout[2]/android.widget.LinearLayout[1]/android.widget.Spinner[1]/android.widget.TextView[1]").click
    
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