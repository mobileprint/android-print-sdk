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
    element_id="android:id/action_bar_title"
	wait.until { selenium.find_element(:id,element_id) }
    home_title = selenium.find_element(:id,element_id).text
    raise "Error Screen" unless home_title == "PrintSDKSample"        
end

Then(/^I tap on "(.*?)" option$/) do |option|
    if option == "Image"
        $content_option = "Image"
    else if option == "True" || option == "False"
        $unique_id_per_app = option
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
Given(/^I tap on plugin helper button$/) do
  element_id="com.hp.mss.printsdksample:id/newPluginStatus"
    selenium.find_element(:id,element_id).click
end

