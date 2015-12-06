class AddLatitudeToPins < ActiveRecord::Migration
  def change
    add_column :pins, :latitude, :float
  end
end
