require 'rubygems'
#require 'selenium-cucumber'
require 'selenium-webdriver'


def todaysdate
    time = Time.new
    currentdate = time.strftime("%Y-%m-%d_%H:%M:%S")
    return currentdate
end

def clearfunction 
    comments =selenium.find_element(:xpath,"//android.view.View[1]/android.widget.FrameLayout[2]/android.widget.RelativeLayout[1]/android.widget.LinearLayout[1]/android.widget.RelativeLayout[1]/android.widget.RelativeLayout[1]/android.widget.EditText[1]").text
    
    #$driver.find_element(:xpath,"//android.view.View[1]/android.widget.FrameLayout[2]/android.widget.RelativeLayout[1]/android.widget.LinearLayout[1]/android.widget.RelativeLayout[1]/android.widget.RelativeLayout[1]/android.widget.EditText[1]").clear
    while comments.length > 0 do
        selenium.find_element(:id,"com.hp.mss.droidcardsapp:id/sentiment_edit_text").send_keys [:control,'a'], :delete
        comments =selenium.find_element(:xpath,"//android.view.View[1]/android.widget.FrameLayout[2]/android.widget.RelativeLayout[1]/android.widget.LinearLayout[1]/android.widget.RelativeLayout[1]/android.widget.RelativeLayout[1]/android.widget.EditText[1]").text
        
    end
end

def getOSversion
    #return %x(adb shell getprop ro.build.version.release)[0..2]
    return %x(adb shell getprop ro.build.version.release)
end

