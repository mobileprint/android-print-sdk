Then (/^Fetch metrics details$/) do
  sleep(APPIUM_TIMEOUT)
    device_type=$device_type.strip
     hash = `curl -x "http://proxy.atlanta.hp.com:8080" -L "http://hpmobileprint:print1t@print-metrics-test.twosmiles.com/api/v1/mobile_app_metrics?device_type=#{device_type}&product_name=PrintSDKSample"`
   #hash = `curl -L "http://hpmobileprint:print1t@print-metrics-test.twosmiles.com/api/v1/mobile_app_metrics?device_type=#{device_type}&product_name=PrintSDKSample"`
    hash = JSON.parse(hash)
    $mertics_array = hash["metrics"]
    $mertics_details = hash["metrics"][($mertics_array.length)-1]
end


And (/^I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand$/) do
    sleep(APPIUM_TIMEOUT)
    $deviceid = %x(adb shell getprop net.hostname)
    $device_id = $deviceid.split("android-").last
    $os_version = %x(adb shell getprop ro.build.version.release)
    $os_type = %x(adb shell getprop net.bt.name)
    $device_type = %x(adb shell getprop ro.product.model)
    $manufacturer = %x(adb shell getprop ro.product.manufacturer)
    $device_brand = %x(adb shell getprop ro.product.brand)
    $wifiSSID = %x(adb shell dumpsys netstats | grep 'iface' | grep -o 'networkId.*')
    $wifissid = $wifiSSID.split('"')
    $wifi_ssid= $wifissid[1]
end

Then (/^I get black and white filter value and number of copies$/) do
    $copies = selenium.find_element(:id,"com.android.printspooler:id/copies_edittext").text
    if $os_version < '5.0.0'
        $black_and_white_filter =  selenium.find_element(:xpath,"//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.widget.ScrollView[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.Spinner[1]/android.widget.LinearLayout[1]").text
    else
        $black_and_white_filter =  selenium.find_element(:xpath," //android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.view.View[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.view.View[1]/android.widget.LinearLayout[3]/android.widget.Spinner[1]/android.widget.CheckedTextView[1]").text
    end
    if $black_and_white_filter == "black_and_white"
        $black_and_white_filter = "1"
    else
        $black_and_white_filter = "0"
    end
end

And (/^I check the manufacturer name$/) do
  compare  = ($mertics_details['manufacturer'] == $manufacturer.split(" ").last) ?  true : false
  raise "manufacturer verification failed" unless compare==true
end

And (/^I check the os_type$/) do
  compare  = ($mertics_details['os_type'] == $os_type.split(" ").last) ?  true : false
  raise "os_type verification failed" unless compare==true
end

And (/^I check the version$/) do
  compare  = ($mertics_details['version'] == $version) ?  true : false
  raise "version verification failed" unless compare==true
end


And (/^I check the wifi_ssid$/) do
  compare  = ($mertics_details['wifi_ssid'] == $wifi_ssid) ?  true : false
  raise "wifi_ssid verification failed" unless compare==true
end

And (/^I check the product name is "([^\"]*)"$/) do |product_name|
  compare  = ($mertics_details['product_name'] == product_name) ?  true : false
  raise "product name verification failed" unless compare==true
end

And (/^I check the device brand$/) do
  compare  = ($mertics_details['device_brand'] == $device_brand.split(" ").last) ?  true : false
  raise "device brand verification failed" unless compare==true
end

And (/^I check the off ramp is "([^\"]*)"$/) do |off_ramp|
  compare  = ($mertics_details['off_ramp'] == off_ramp) ?  true : false
  raise "off ramp verification failed" unless compare==true
end

And (/^I check the device type$/) do
    device_type=$device_type.strip
    compare  = ($mertics_details['device_type'] == device_type) ?  true : false
  fail "device type verification failed" unless compare==true
end
    
And (/^I check the os version$/) do
  compare  = ($mertics_details['os_version'] == $os_version.split(" ").last) ?  true : false
  raise "os version verification failed" unless compare==true
end

And (/^I check the paper type is "([^\"]*)"$/) do |paper_type|
      compare  = ($mertics_details['paper_type'] == paper_type) ?  true : false
    raise "paper_type verification failed" unless compare==true
end

And (/^I check the print_plugin_tech is "([^\"]*)"$/) do |print_plugin_tech|
   
    compare  = ($mertics_details['print_plugin_tech'] == print_plugin_tech) ?  true : false
    raise "print_plugin_tech verification failed" unless compare==true
end

And (/^I check the print_result is "([^\"]*)"$/) do |print_result|
    compare  = ($mertics_details['print_result'] == print_result) ?  true : false
    raise "print_result verification failed" unless compare==true
end

And (/^I check the device id$/) do
      compare  = ($mertics_details['device_id'] == $device_id.split(" ").last) ?  true : false
    raise "device_id verification failed" unless compare==true
end

And (/^I check the wifi ssid$/) do
    compare  = ($mertics_details['wifi_ssid'] == $wifi_ssid.split(" ").last) ?  true : false
    raise "wifi_ssid verification failed" unless compare==true
end

And (/^I check the black and white filter$/) do
    compare  = ($mertics_details['black_and_white_filter'] == $black_and_white_filter) ?  true : false
    raise "black_and_white_filter verification failed" unless compare==true
end

And (/^I check the number of copies$/) do
    compare  = ($mertics_details['copies'] == $copies) ?  true : false
    raise "copies verification failed" unless compare==true
end

And (/^I check the paper size$/) do
   $os_version = getOSversion
        if $paper_size == "4x6 in"
           $paper_size = "4.0 x 6.0"
        else if $paper_size == "5x7 in"
            $paper_size = "5.0 x 7.0"
        else if $paper_size == "Legal"
            $paper_size = "8.5 x 14.0"
        else 
            $paper_size = "8.5 x 11.0"
        end
        end
        end
   
    compare  = ($mertics_details['paper_size'] == $paper_size) ?  true : false
    raise "paper_size verification failed" unless compare==true
    
end

Then(/^I cancel the print$/) do
  macro %Q|I navigate to back|
end

Then(/^I check print result is "(.*?)"$/) do |print_result|
  compare  = ($mertics_details['print_result'] == print_result.strip) ?  true : false
  fail "Print result verification failed" unless compare==true
end

Then(/^I verify metrics not generated for current print/) do
    previous_metrics_length=$mertics_array.length
    macro %Q|Fetch metrics details|
    if previous_metrics_length < $mertics_array.length
        raise "Print metrics generated for without metrics option"
    end
end







