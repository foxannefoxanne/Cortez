class Pin < ActiveRecord::Base
	belongs_to :map

	geocoded_by :address
	after_validation :geocode

end
