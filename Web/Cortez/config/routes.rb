Rails.application.routes.draw do
  resources :pins
  resources :maps
  
  root 'maps#index'
  #root 'pins#index'

end
