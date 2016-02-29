Given /^I am on "(.*?)" screen$/ do |screen_name|
	@current_page = page_by_name(screen_name)
	@current_page.navigate
	sleep(STEP_PAUSE)
end

Then /^I should see the "(.*?)" screen$/ do |screen_name|
    required_page = page_by_name(screen_name)
	wait_for { required_page.current_page? }
	@current_page = required_page
    sleep(STEP_PAUSE)
end

Then(/^I should see the following$/) do |table|
    check_value_exists table.raw
end


