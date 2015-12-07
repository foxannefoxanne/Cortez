Rails.application.routes.draw do
  resources :pins
  resources :maps
  
  #root 'maps#index'
  root 'maps#home'
  get 'new_maps' => 'maps#new'

end
