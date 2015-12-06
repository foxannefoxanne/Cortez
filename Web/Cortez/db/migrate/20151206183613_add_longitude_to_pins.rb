class AddLongitudeToPins < ActiveRecord::Migration
  def change
    add_column :pins, :longitude, :float
  end
end
