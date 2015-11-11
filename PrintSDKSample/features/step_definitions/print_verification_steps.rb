Then(/^I fetch xml counters$/) do
    $xmlCounter = readXMLValues 

end

Then(/^I verify counters$/) do
    result = verifyxmlCounters  
    if result != true
      raise "\n Paper size counter before print: " + $xmlCounter[$counter[$paper_size]] + "\n Paper size counter after print : " + $xmlCounterAfterPrint[$counter[$paper_size]] + "\n Paper type/quality counter before print: " + $xmlCounter[$paperType_qualityCounter] + "\n Paper type/quality counter after print: " + $xmlCounterAfterPrint[$paperType_qualityCounter] + "\n Counter verification failed!"  
    end
end

After do
    sleep(5)
    selenium.driver_quit
	ENV['http_proxy']=$proxy_http
    ENV['https_proxy']=$proxy_https
end

Then(/^I should see the "(.*?)" button$/) do |menu|
	if menu=="Print"
		print_button_value=query("* id:'mobile_print'")
		raise "Print button not found!" unless print_button_value.length > 0
	else if menu=="Back"
		raise "Back menu not found!" unless navigate_button.length > 0
	else if menu=="Hamburger"
		hamburger_button_value=query("android.widget.ImageButton")
		raise "Hamburger menu not found!" unless hamburger_button_value.length > 0
	else 
		raise "Invalid menu!" unless menu=="Share"
		share_button_value=query("* id:'mobile_print'")
		raise "Share button not found!" unless share_button_value.length > 0
	end
	end
	end
end
