require 'calabash-android/abase'

class HomeScreen < Calabash::ABase

  def trait
      screen_title
  end

  def screen_title
      "* text:'Print SDK'"
  end


  def navigate
    await
  end

end