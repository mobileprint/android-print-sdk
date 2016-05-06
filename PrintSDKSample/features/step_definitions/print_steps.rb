
Then (/^I navigate to back$/) do
    selenium.driver.press_keycode 4
    sleep(MAX_TIMEOUT)
end

Then (/^I tap on Print$/) do
    sleep(APPIUM_TIMEOUT)
    wait.until { selenium.find_element(:id,"com.android.printspooler:id/print_button") }
    selenium.find_element(:id,"com.android.printspooler:id/print_button").click
    sleep(MAX_TIMEOUT)
end
Then(/^I should see "(.*?)" screen for kitkat and "(.*?)" screen for Lollipop$/) do |screen_kitkat, screen_lollipop|
    $os_version = getOSversion
    if $os_version >= '5.0'
        screen_title = screen_lollipop
    else
         screen_title = screen_kitkat
    end
    element_xpath="//android.widget.TextView[1]"
	wait.until { selenium.find_element(:xpath,element_xpath) }
    home_title = selenium.find_element(:xpath,element_xpath).text
    raise "Error Screen" unless home_title == "#{screen_title}"  
    
end