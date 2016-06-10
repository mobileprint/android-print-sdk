require 'digest/md5'
require 'uri'

Then (/^I Fetch event metrics details$/) do
    sleep(APPIUM_TIMEOUT)
    encrypted_device_id = get_device_id    
    hash = `curl -x "http://proxy.atlanta.hp.com:8080" -L "http://hpmobileprint:print1t@print-metrics-test.twosmiles.com/api/v2/events?device_id=#{encrypted_device_id}&product_id=com.hp.mss.printsdksample"`
    #hash = `curl -L "http://hpmobileprint:print1t@print-metrics-test.twosmiles.com/api/v2/events?device_id=#{encrypted_device_id}&product_id=com.hp.mss.printsdksample"`
    $hash = JSON.parse(hash)
    $mertics_array = $hash
   end
Then(/^I get the event metrics for "(.*?)"$/) do |event_name|
  if event_name == "entered_print_sdk"
      $mertics_details = $hash[($mertics_array.length)-2]
  else
      $mertics_details = $hash[($mertics_array.length)-1]
  end
end

Then(/^I check the print session id is "(.*?)"$/) do |print_session_id|
  compare = ($mertics_details['print_session_id'] == print_session_id) ?  true : false
  raise "Print_session_id verification failed!" unless compare==true
end

Then(/^I check the event count is "(.*?)"$/) do |event_count|
compare = ($mertics_details['event_count'] == event_count) ?  true : false
  raise "Event_count verification failed!" unless compare==true
end

Then(/^I check the event type id is "(.*?)"$/) do |event_type|
 compare = ($mertics_details['event_type_id'] == event_type) ?  true : false
  raise "Event_type verification failed!" unless compare==true
end