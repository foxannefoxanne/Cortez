Rails.application.routes.draw do
  resources :maps
  resources :pins
  
  root 'maps#index'
  #root 'pins#index'

end
