class CreatePins < ActiveRecord::Migration
  def change
    create_table :pins do |t|
      t.string :title
      t.string :description
      t.string :address
      t.float :lat
      t.float :long
      t.integer :map_id

      t.timestamps null: false
    end
  end
end
