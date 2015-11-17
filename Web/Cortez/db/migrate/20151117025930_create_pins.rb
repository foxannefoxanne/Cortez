class CreatePins < ActiveRecord::Migration
  def change
    create_table :pins do |t|
      t.float :latitude
      t.float :longitude
      t.string :name
      t.string :description

      t.timestamps null: false
    end
  end
end
