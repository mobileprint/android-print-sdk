require 'digest/md5'
require 'uri'

Then (/^Fetch metrics details$/) do
  sleep(APPIUM_TIMEOUT)
    #device_type=$device_type.strip
    #device_type= URI.encode(device_type)
	encrypted_device_id = get_device_id
    hash = `curl -x "http://proxy.atlanta.hp.com:8080" -L "http://hpmobileprint:print1t@print-metrics-test.twosmiles.com/api/v1/mobile_app_metrics?device_id=#{encrypted_device_id}&product_id=com.hp.mss.printsdksample"`
    #hash = `curl -L "http://hpmobileprint:print1t@print-metrics-test.twosmiles.com/api/v1/mobile_app_metrics?device_id=#{encrypted_device_id}&product_id=com.hp.mss.printsdksample"`
    hash = JSON.parse(hash)
    $mertics_array = hash["metrics"]
    $mertics_details = hash["metrics"][($mertics_array.length)-1]
end


And (/^I get the wifi_ssid, device id, os version, os type, device type, manufacturer and device brand$/) do
    sleep(APPIUM_TIMEOUT)
    $deviceid = %x(adb shell getprop net.hostname)
    $device_id = $deviceid.split("android-").last
    $os_version_original = %x(adb shell getprop ro.build.version.release)
    $os_type = %x(adb shell getprop net.bt.name)
    $device_type = %x(adb shell getprop ro.product.model)
    $manufacturer = %x(adb shell getprop ro.product.manufacturer)
    $device_brand = %x(adb shell getprop ro.product.brand)
    $wifiSSID = %x(adb shell dumpsys netstats | grep 'iface' | grep -o 'networkId.*')
    $wifissid = $wifiSSID.split('"')
    $wifi_ssid= $wifissid[1]
end

Then (/^I get black and white filter value and number of copies$/) do
  sleep(APPIUM_TIMEOUT)
    $copies = selenium.find_element(:id,"com.android.printspooler:id/copies_edittext").text.split('')

    if getOSversion < '5.0'
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
  compare  = ($mertics_details['version'] == "1.0") ?  true : false
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
  compare  = ($mertics_details['os_version'] == $os_version_original.split(" ").last) ?  true : false
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
    #my_device = $device_id.split(" ").last
    #if $unique_device_id == "Unique Per Vendor"
    #    device_id_value = Digest::MD5.hexdigest("com.hp#{my_device.to_s}").upcase
    #else if $unique_device_id == "Unique Per App"
    #    device_id_value = Digest::MD5.hexdigest("com.hp.mss.printsdksample#{my_device.to_s}").upcase
    #else if $unique_device_id == "Not Encrypted"
    #    device_id_value = my_device
    #end
    #end
    #end
    compare = ($mertics_details['device_id'] == get_device_id.delete(' ')) ? true : false
    raise "device_id verification failed" unless compare==true
end

And (/^I check the wifi ssid$/) do
    if $target == "Emulator"
        $wifi_ssid = "NO-WIFI"
    end
  encrypt_wifi_ssid = Digest::MD5.hexdigest($wifi_ssid.split(" ").last).upcase
    compare  = ($mertics_details['wifi_ssid'] == encrypt_wifi_ssid) ?  true : false
    raise "wifi_ssid verification failed" unless compare==true
end

And (/^I check the black and white filter$/) do
    compare  = ($mertics_details['black_and_white_filter'] == $black_and_white_filter) ?  true : false
    raise "black_and_white_filter verification failed" unless compare==true
end

And (/^I check the number of copies$/) do
    compare  = ($mertics_details['copies'].strip == $copies[0]) ?  true : false
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
  compare  = ($mertics_details['print_result'].strip == print_result.strip) ?  true : false
  fail "Print result verification failed" unless compare==true
end

Then(/^I verify metrics not generated for current print/) do
    previous_metrics_length=$mertics_array.length
    macro %Q|Fetch metrics details|
    if previous_metrics_length < $mertics_array.length
        raise "Print metrics generated for without metrics option"
    end
end

Then(/^I check the print library version$/) do
    compare = ($mertics_details['print_library_version'] == "v2.02.448") ?  true : false
  raise "print_library_version verification failed!" unless compare==true
end

Then(/^I check the content type is "(.*?)"$/) do |content_type|
    compare = ($mertics_details['content_type'] == content_type.downcase) ?  true : false
  raise "content_type verification failed!" unless compare==true
end

Then(/^I check the app_type is "(.*?)"$/) do |app_type|
  compare = ($mertics_details['app_type'] == app_type) ?  true : false
  raise "app_type verification failed!" unless compare==true
end

Then(/^I check the number of installed plugins$/) do
    compare = ($mertics_details['num_of_plugins_installed'].to_i == $installed_plugin_count.to_i) ?  true : false
    raise "no of installed plugins verification failed!" unless compare==true
end

Then(/^I check the number of enabled plugins$/) do
    compare = ($mertics_details['num_of_plugins_enabled'].to_i == $enabled_plugin_count.to_i) ?  true : false
    raise "no of enabled plugins verification failed!" unless compare==true
end
Then(/^I check timestamp is not null$/) do
   compare = ($mertics_details['timestamp'] != "null") ?  true : false
  raise "Timestamp verification failed" unless compare==true
end

Then(/^I check the route taken is "(.*?)"$/) do |route_taken|
  compare = ($mertics_details['route_taken'] == route_taken) ?  true : false
  raise "Route_taken verification failed!" unless compare==true
end
Then(/^I check the printer id$/) do
    printer_id = "PDF printer"
    encrypt_printer_id = Digest::MD5.hexdigest(printer_id).upcase
    
  compare = ($mertics_details['printer_id'] == encrypt_printer_id) ?  true : false
  raise "Printer id verification failed" unless compare==true
end
Then(/^I check the country code$/) do
  compare = ($mertics_details['country_code'] == "USA") ?  true : false
  raise "Country code verification failed" unless compare==true
end

Then(/^I check the language code$/) do
  compare = ($mertics_details['language_code'] == "eng") ?  true : false
  raise "Language code verification failed" unless compare==true
end
Then(/^I check the print session id$/) do
  compare = ($mertics_details['print_session_id'] == "1") ?  true : false
  raise "Print Session id verification failed" unless compare==true
end

Then(/^I check the custom data$/) do
  compare = ($mertics_details['custom_data'] == "N/A") ?  true : false
  raise "Custom data verification failed" unless compare==true
end
Then(/^I check the product id is "(.*?)"$/) do |product_id| 
  compare = ($mertics_details['product_id'] == product_id) ?  true : false
  raise "product_id verification failed" unless compare==true
end
Then(/^I delete the generated pdf$/) do
  sleep(APPIUM_TIMEOUT)
    file_path = %x(adb shell ls /storage/sdcard/Download)
  
    if (file_path.to_s).include? "No such file or directory"
        %x(adb shell rm /storage/sdcard0/Download/print_card.pdf)
    else
        %x(adb shell rm /storage/sdcard/Download/print_card.pdf)
    end
end