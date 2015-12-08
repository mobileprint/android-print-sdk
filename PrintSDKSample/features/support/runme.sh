#!/bin/bash

if ps aux | grep "[a]ppium" > /dev/null
then
  echo 'Appium is already running'
else
osascript << END
tell application "Terminal"
do script "cd ${APPIUM_PATH} && appium --no-reset"
end tell
END
fi





  
     
    
    
