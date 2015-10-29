# My appium utils function
require 'appium_lib'
require 'selenium-webdriver'


CAPS = {
    'platformName' => 'Android',
    'deviceName' => %x(adb shell getprop ro.serialno),
    'app'=> ENV['APP_PATH'],
    'noSign' => true
}

def server_url
  "http://127.0.0.1:4723/wd/hub"
end

def selenium
  @driver ||= Appium::Driver.new(caps: CAPS)
end

def wait
  wait = Selenium::WebDriver::Wait.new(:timeout => APPIUM_TIMEOUT)
end