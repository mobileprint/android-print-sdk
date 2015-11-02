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

 