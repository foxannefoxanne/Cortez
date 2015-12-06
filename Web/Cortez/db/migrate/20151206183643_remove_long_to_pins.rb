class RemoveLongToPins < ActiveRecord::Migration
  def change
    remove_column :pins, :long, :float
  end
end
