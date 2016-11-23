
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

Then(/^I should see "([^\"]*) screen$/) do |screen_name|
  $os_version = getOSversion

  if $os_version >= '5.0'
    screen_displayed = selenium.find_element(:id, "com.android.printspooler:id/embedded_content_scrim").displayed?
  else
    if screen_name == "pdf"
      screen_displayed = selenium.find_element(:id,"com.android.printspooler:id/print_button").displayed?
    else
      screen_displayed = selenium.find_element(:id, "com.hp.mss.printsdksample:id/preview_image_view").displayed?
    end
  end
  raise "Error screen not loaded" unless screen_displayed == true

end

