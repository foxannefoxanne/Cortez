class AddMapIdToPins < ActiveRecord::Migration
  def change
    add_column :pins, :map_id, :int
  end
end
