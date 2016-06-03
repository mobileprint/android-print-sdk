Then(/^I select "(.*?)" (?:option|button)$/) do |option|
    sleep(3.0)
    if option =="Image"
        touch query("* text:'#{option}'")
    else if option =="Preview"
        touch query("* text:'#{option}'")
    else if option =="I have one"
       if element_exists("* text:'#{option}'")
            touch query("* text:'#{option}'")
        end
    end
    end
    end
end

Given(/^I am on Home screen$/) do
    $proxy_http=ENV['http_proxy']
    $proxy_https=ENV['https_proxy']
    ENV['http_proxy']=nil
    ENV['https_proxy']=nil
    path =File.expand_path("../../support/runme.sh", __FILE__)
    system(path)
    sleep(WAIT_TIMEOUT)
    selenium.start_driver
    element_id="com.hp.mss.printsdksample:id/sample_toolbar"
	wait.until { selenium.find_element(:id,element_id) }
    home_title = selenium.find_element(:name,"Print SDK").text
    raise "Error Screen" unless home_title == "Print SDK"
end

Then(/^I tap on "(.*?)" option$/) do |option|
    if option == "Image"
        $content_option = "Image"
    else if option == "Not Encrypted" || option == "Unique Per App" || option == "Unique Per Vendor"
        unique_id_loc = selenium.find_element(:id,"com.hp.mss.printsdksample:id/deviceIdText").location
        data_loc = (unique_id_loc.to_s).split(" ")
        data_x = data_loc[2].split("=")
        data_y = data_loc[3].split("=")
        x1_value = data_x[1].to_i
        y1_value = (data_y[1].delete! '>').to_i
        x1 = x1_value + 100
        x2 =  x1
        y1 = y1_value + 50
        y2 = y1 - 200
        %x(adb shell input swipe #{x1.to_i} #{y1.to_i} #{x2.to_i} #{y2.to_i} 100)
        $unique_device_id = option
    end
    end
    sleep(WAIT_SCREENLOAD)  
    if selenium.find_elements(:name,option).size > 0
            selenium.find_element(:name,option).click
        end
    sleep(WAIT_SCREENLOAD)
end
Then(/^I select preview button$/) do
    $os_version = getOSversion
    sleep(WAIT_SCREENLOAD)
    element_id="com.hp.mss.printsdksample:id/printBtn"
    selenium.find_element(:id,element_id).click
    print_service_helper
end
Then(/^I select layout as "(.*?)"$/) do |layout_option|
    if layout_option == "Center"
        element_id="com.hp.mss.printsdksample:id/layoutCenter"
        selenium.find_element(:id,element_id).click
    end
end
Then(/^I navigate to home screen$/) do
    if $os_version < '5.0'
        macro %Q|I navigate to back|
    end
    macro %Q|I tap on "OK" option|
end

Given(/^I tap on print plugin manager$/) do
  element_id="com.hp.mss.printsdksample:id/print_plugin_manager"
    selenium.find_element(:id,element_id).click
end

Then /^verify that "(.*?)" value is present$/ do |value|
    sleep(STEP_PAUSE)
    wait_for_element_exists("radiobutton marked:'#{value}'", timeout=> APPIUM_TIMEOUT)
end

Then /^verify that "(.*?)" button is present$/ do |button|
    sleep(STEP_PAUSE)
    wait_for_element_exists("button marked:'#{button}'", timeout=> APPIUM_TIMEOUT)
end

Given(/^"(.*?)" value should be selected$/) do |value|
    if value.to_s == "Not Encrypted"
        %x(adb shell input swipe 400 400 400 200 100)
    end
    raise "#{value} is not the default selection" if query("AppCompatRadioButton marked:'#{value}'",:checked)[0] != true
end

When /^I tap on "(.*?)" value, it should be selected$/ do |value|
    touch "AppCompatRadioButton marked:'#{value}'"
    raise "#{value} is not selection" if query("AppCompatRadioButton marked:'#{value}'",:checked)[0] != true
end
Then(/^I click "(.*?)" button$/) do |buton_text|
  selenium.find_element(:xpath,"//android.widget.Button[@text='#{buton_text}']").click
end
Then(/^I select the "(.*?)"$/) do |content_type|
    sleep(APPIUM_TIMEOUT)
    if content_type == "pdf"
        if selenium.find_elements(:xpath,"//android.widget.TextView[@text='Recent']").size > 0
            selenium.find_element(:xpath,"//android.widget.TextView[@text='Downloads']").click
        end
        sleep(APPIUM_TIMEOUT)
        if selenium.find_elements(:xpath,"//android.widget.TextView[contains(@text, '.pdf')]").size > 0
            selenium.find_element(:xpath,"//android.widget.TextView[contains(@text, '.pdf')]").click
        else
            raise "#{content_type} file not found!"
        end
    else
        $os_version = getOSversion
        if $os_version >= '5.0'
            element_id = "//android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.support.v4.widget.DrawerLayout[1]/android.widget.LinearLayout[1]/android.view.View[1]/android.widget.ImageButton[1]"
        else
            element_id = "//android.view.View[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.ImageView[1]"
        end
        if selenium.find_elements(:xpath,"//android.widget.TextView[@text='Downloads']").size > 0
            selenium.find_element(:xpath,"#{element_id}").click
            sleep(APPIUM_TIMEOUT)
            selenium.find_element(:xpath,"//android.widget.TextView[@text='Images']").click
        end
        sleep(APPIUM_TIMEOUT)
        %x(adb shell input tap 300 300)
        sleep(APPIUM_TIMEOUT)
        %x(adb shell input tap 300 300)
        sleep(APPIUM_TIMEOUT)
    end    
end
