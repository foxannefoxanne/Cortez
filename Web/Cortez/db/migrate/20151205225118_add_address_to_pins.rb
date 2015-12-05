class AddAddressToPins < ActiveRecord::Migration
  def change
    add_column :pins, :address, :string
  end
end
