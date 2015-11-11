WAIT_TIMEOUT 	 = (ENV['WAIT_TIMEOUT'] || 30).to_f
STEP_PAUSE		 = (ENV['STEP_PAUSE'] || 0.5).to_f
APPIUM_TIMEOUT = (ENV['APPIUM_TIMEOUT'] || 5).to_f
WAIT_SCREENLOAD = (ENV['WAIT_SCREENLOAD'] || 3).to_f
MAX_TIMEOUT 	 = (ENV['MAX_TIMEOUT'] || 20).to_f
take_screenshots = ENV['TAKE_SCREENSHOTS']

require 'calabash-android/cucumber'
