Then(/^I should see the skip option$/) do 
    skip_button =selenium.find_elements(:id,"com.hp.mss.printsdksample:id/print_btn")
     #selenium.find_element(:id,"com.hp.mss.printsdksample:id/print_btn").click
    raise "skip button not found!" unless skip_button.size > 0
        
end
Then(/^I should see "(.*?)" screen$/) do |screen_name|
    $flag = ""
    if screen_name == "Print Service Manager"
    element_xpath="//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.widget.TextView[1]"
	wait.until { selenium.find_element(:xpath,element_xpath) }
    home_title = selenium.find_element(:xpath,element_xpath).text
        raise "Error Screen" unless home_title == "Print Service Manager"    
    end
end

Then(/^I tap to enable "(.*?)" if installed$/) do |plugin_name|
    $os_version = getOSversion
    if plugin_name == "HP Print Service Plugin"
        index =1
    else if plugin_name == "Samsung Print Service Plugin" || plugin_name == "Brother Print Service Plugin"
        index =3
    else if plugin_name == "Mopria Print Service"    
        index =4
    else if plugin_name == "Canon Print Service"
        index =2
    else if plugin_name == "Epson Print Enabler"
        index =5
    else if plugin_name == "Other PrintServicePlugin"
        index =6
    else
    end
    end
    end
    end
    end
    end
    if plugin_name == "Samsung Print Service Plugin" && $os_version >= '5.0'
        puts "skipping-check for Samsung plugin(Kitkat only)"
        $flag = "false"
    else if plugin_name == "Brother Print Service Plugin" && $os_version < '5.0'
        puts "skipping-check for Brother plugin(Lollipop only)"
        $flag = "false"
    else
        element_id ="//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.widget.FrameLayout[2]/android.widget.RelativeLayout[1]/android.widget.ListView[1]/android.widget.RelativeLayout[#{index.to_i}]/android.widget.FrameLayout[1]/android.widget.TextView[1]"
        if selenium.find_element(:xpath,element_id).text == "Disabled"
            selenium.find_element(:xpath,element_id).click
        else
            raise "#{plugin_name} not installed!"
        end

    end
    end
end

Then(/^I should see "(.*?)" pop up$/) do |dialog_title|
    if $flag != "false"
        element_id="com.hp.mss.printsdksample:id/dialog_title"
        wait.until { selenium.find_element(:id,element_id) }
        home_title = selenium.find_element(:id,element_id).text
        raise "Error Screen" unless home_title == "Enable your Plugin"  
    else
        puts "skipping-not applicable"
    end
end


Then(/^I get installed and enabled plugin count$/) do
	sleep(APPIUM_TIMEOUT)
    index=1
    $enabled_plugin_count = 0
    $installed_plugin_count = 0
    while index < 7
        element_id ="//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.widget.FrameLayout[2]/android.widget.RelativeLayout[1]/android.widget.ListView[1]/android.widget.RelativeLayout[#{index.to_i}]/android.widget.FrameLayout[1]/android.widget.TextView[1]"
        
        if ((selenium.find_element(:xpath,element_id).text == "Disabled") ||(selenium.find_element(:xpath,element_id).text == "Enabled"))
            $installed_plugin_count = $installed_plugin_count+1
             
			if selenium.find_element(:xpath,element_id).text == "Enabled"
				$enabled_plugin_count = $enabled_plugin_count+1
			end
        end
         index = index + 1
        end
    end
Then(/^I tap to "(.*?)" a plugin$/) do |action|
    index=1
 while index < 7
        element_id ="//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.widget.FrameLayout[2]/android.widget.RelativeLayout[1]/android.widget.ListView[1]/android.widget.RelativeLayout[#{index.to_i}]/android.widget.FrameLayout[1]/android.widget.TextView[1]"
        
     if action == "enable"
        if (selenium.find_element(:xpath,element_id).text == "Disabled") 
            selenium.find_element(:xpath,element_id).click
            break
        end
    else
         if (selenium.find_element(:xpath,element_id).text == "") 
            selenium.find_element(:xpath,element_id).click
            break
        end
    end
         index = index + 1
        end
end