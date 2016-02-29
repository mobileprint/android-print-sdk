Then(/^I should see the skip option$/) do 
    skip_button =selenium.find_elements(:id,"com.hp.mss.printsdksample:id/print_btn")
     #selenium.find_element(:id,"com.hp.mss.printsdksample:id/print_btn").click
    raise "skip button not found!" unless skip_button.size > 0
        
end
Then(/^I should see "(.*?)" screen$/) do |screen_name|
    if screen_name == "Print Service Manager"
    element_xpath="//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.widget.TextView[1]"
	wait.until { selenium.find_element(:xpath,element_xpath) }
    home_title = selenium.find_element(:xpath,element_xpath).text
        raise "Error Screen" unless home_title == "Print Service Manager"    
    end
end

Then(/^I tap to enable "(.*?)" if installed$/) do |plugin_name|
    if plugin_name == "HP Print Service Plugin"
        index =1
    else if plugin_name == "Mopria Print Service"
        index =2
    else if plugin_name == "Brother Print Service Plugin"
        index =3
    else if plugin_name == "Canon Print Service"
        index =4
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
element_id ="//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.widget.FrameLayout[2]/android.widget.RelativeLayout[1]/android.widget.ListView[1]/android.widget.RelativeLayout[#{index.to_i}]/android.widget.FrameLayout[1]/android.widget.TextView[1]"
        
    if selenium.find_element(:xpath,element_id).text == "Disabled"
        selenium.find_element(:xpath,element_id).click
    else
        raise "#{plugin_name} not installed!"
end

end

Then(/^I should see "(.*?)" pop up$/) do |dialog_title|
  element_id="com.hp.mss.printsdksample:id/dialog_title"
	wait.until { selenium.find_element(:id,element_id) }
    home_title = selenium.find_element(:id,element_id).text
    raise "Error Screen" unless home_title == "Enable your Plugin"  
end


