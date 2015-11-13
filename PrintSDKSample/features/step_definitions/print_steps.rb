
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
