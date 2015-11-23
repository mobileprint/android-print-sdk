require "net/http"
require "uri"
require "json"
require 'active_support/all'
require 'rubygems'
require 'cobravsmongoose'

def page_by_name page_name
	page_class_name = "#{page_name.gsub(' ', '')}Screen"
	page_constant = Object.const_get(page_class_name)
	page(page_constant)
end

def scroll_view_position
	imageview_attributes = query("imageView").first["description"]   
	imageview_attributes = imageview_attributes.scan(/\d+(?=,\d+-\d+,\d+}$)/)
	imageview_attributes[0].to_i 
end

def go_to page_class
    requested_page = page(page_class).navigate
	requested_page
end

def scroll_to_right
    perform_action('drag', 90, 0, 50, 50, 50)
end

  def scroll_to_left
    perform_action('drag', 0, 90, 50, 50, 50)
  end

 def getJsonData jsonKey
  workingDir = Dir.pwd
  Dir.chdir __dir__
  json = File.read('config.json')
  $jsonData = JSON.parse(json)
  value =  $jsonData[jsonKey]
  Dir.chdir workingDir
  return value
end
	
def readXMLValues # To read counter values from xml

  ipAddress = getJsonData "PrinterIP" #read printer ip from config.json
  counterFromXML = {} #json object to store counters

  hash = `curl -s -L "http://#{ipAddress}/DevMgmt/ProductUsageDyn.xml"`


  sleep(MAX_TIMEOUT)
  result = CobraVsMongoose.xml_to_hash(hash).to_json

  parsed_json = ActiveSupport::JSON.decode(result)

  array = parsed_json["pudyn:ProductUsageDyn"]["pudyn:PonyExpressSubunit"]["pudyn:PECounter"]
  array.each do |i|
    counterFromXML[i["@PEID"]] = i["$"]
  end

  for i in 0..1
    normalImpressionsPEID = parsed_json["pudyn:ProductUsageDyn"]["pudyn:PrinterSubunit"]["pudyn:UsageByMediaType"][i]["dd:UsageByQuality"]["dd:NormalImpressions"]["@PEID"]

    normalImpressionsCount = parsed_json["pudyn:ProductUsageDyn"]["pudyn:PrinterSubunit"]["pudyn:UsageByMediaType"][i]["dd:UsageByQuality"]["dd:NormalImpressions"]["$"]
    counterFromXML[normalImpressionsPEID] =  normalImpressionsCount
  end
  for i in 0..1
    betterImpressionsPEID = parsed_json["pudyn:ProductUsageDyn"]["pudyn:PrinterSubunit"]["pudyn:UsageByMediaType"][i]["dd:UsageByQuality"]["dd:BetterImpressions"]["@PEID"]
    betterImpressionsCount = parsed_json["pudyn:ProductUsageDyn"]["pudyn:PrinterSubunit"]["pudyn:UsageByMediaType"][i]["dd:UsageByQuality"]["dd:BetterImpressions"]["$"]
    counterFromXML[betterImpressionsPEID] =  betterImpressionsCount
  end
  draftImpressionPEID = parsed_json["pudyn:ProductUsageDyn"]["pudyn:PrinterSubunit"]["pudyn:UsageByMediaType"][0]["dd:UsageByQuality"]["dd:DraftImpressions"]["@PEID"]

  draftImpressionCount = parsed_json["pudyn:ProductUsageDyn"]["pudyn:PrinterSubunit"]["pudyn:UsageByMediaType"][0]["dd:UsageByQuality"]["dd:DraftImpressions"]["$"]
  counterFromXML[draftImpressionPEID] =  draftImpressionCount

  return counterFromXML

end

def verifyxmlCounters # To verify counters before and after print

  if $os_version < '5.0.0' # To check os version
    $counter =  getJsonData "counter2" # for kitkat

  else 
    $counter =  getJsonData "counter2" # for lollipop
  end

  $xmlCounterAfterPrint = readXMLValues #read counter values after print
  $paperType_qualityCounter = getJsonData $PaperType+"/"+$Quality
  #paperType_qualityCountBeforePrint = $xmlCounter[paperType_qualityCounter]
  #paperType_qualityCountAfterPrint = $xmlCounterAfterPrint[paperType_qualityCounter]
  if $xmlCounterAfterPrint[$counter[$paper_size]].to_i == $xmlCounter[$counter[$paper_size]].to_i + 1 and $xmlCounterAfterPrint[$paperType_qualityCounter].to_i == $xmlCounter[$paperType_qualityCounter].to_i + 1
    return true
  else 
    return false
  end

end
	
def check_page_loaded social_media
	 if element_exists("* id:'progress'")#check if spinner exists
        sleep(WAIT_SCREENLOAD)
		wait_for_elements_exist(social_media,:timeout=>WAIT_TIMEOUT,:retry_frequency => 10.0,:timeout_message=>"Page not Loaded!")#Wait for 30 seconds and check for the page in every 10 seconds
	else if element_exists(social_media)
		#break
        else
		raise "spinner not loaded!"
	end
	end
end

def startlogging(apkfile)
  $tstart = Thread.start do
  Dir.chdir ENV['HOME']
  if File.exist?("DroidPhoto.log")
      File.delete("DroidPhoto.log")
  end
      %x[adb -d logcat #{apkfile}  >> DroidPhoto.log]
  end
end

def checktemplate(category)
  latest = latestevent
  val = "false"
  logpath =ENV['HOME']+ "/DroidPhoto.log"
  log =%x[grep -r "I/Tracking Event" "#{logpath}"]
  log.each_line{|line|
      if (line.match("Label:\"#{category}\"")!= nil && line.match(latest.to_s)) 
        val = "true"
        break
      end
  }
  val
end

def latestevent
  event = 0
  logpath =ENV['HOME']+ "/DroidPhoto.log"
  log =%x[grep -r "I/Tracking Event" "#{logpath}"]
    log.each_line{|line|
        line = line.split(")")[0]
        line = line.split("(")[-1]
        line = line.lstrip
        if(event < line.to_i)
          event = line.to_i
        end
        }
    event
end

def checkprint
  latest = latestevent
  val = "false"
  logpath =ENV['HOME']+ "/DroidPhoto.log"
  log =%x[grep -r "I/Tracking Event" "#{logpath}"]
  log.each_line{|line|
      if (line.match("Action:\"Plugin Status\", Label:\"HP Plugin Installed")!= nil)
    val = "true"
    break
  end
  }
  val
end

def checkpapersize(paper_size)
  if paper_size=='8.5 x 11'
      paper_size = paper_size.gsub("11","11.0")
  else
      paper_size = paper_size.gsub(/[4,5,6,7]/){|m|m.to_f}
  end
    case paper_size
        when "4.0x6.0 in"
        paper_size = "4.0 x 6.0"
        when "5.0x7.0 in"
        paper_size = "5.0 x 7.0"
        when "Legal"
        paper_size = "8.5 x 14.0"
        when "Letter"
        paper_size = "8.5 x 11.0"
    end
  latest = latestevent
  val = "false"
  logpath =ENV['HOME']+ "/DroidPhoto.log"
  log =%x[grep -r "I/Tracking Event" "#{logpath}"]
  log.each_line{|line|
      if (line.match("Action:\"Print\", Label:\"#{paper_size} Photo com.hp.android.printservice")!= nil && line.match(latest.to_s))
    val = "true"
    break
  end
  }
  val
end

def cancelprint
  latest = latestevent
  val = "false"
  logpath =ENV['HOME']+ "/DroidPhoto.log"
  log =%x[grep -r "I/Tracking Event" "#{logpath}"]
  log.each_line{|line|
  if line.match("Action:\"Print\", Label:\"Cancel\"")
    val = "true"
    break
  end
  }
  val
end    
def print_service_helper
    sleep(WAIT_SCREENLOAD)  
    if selenium.find_elements(:name,"I have one").size > 0
            selenium.find_element(:name,"I have one").click
        end
    sleep(WAIT_SCREENLOAD)
    end