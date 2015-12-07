class PinsController < ApplicationController
  before_action :set_pin, only: [:show, :edit, :update, :destroy]

  # GET /pins
  def index
    @pins = Pin.all
     @hash = Gmaps4rails.build_markers(@pins) do |pin, marker|
      marker.lat pin.latitude
      marker.lng pin.longitude
      marker.title pin.title
      info = "<p><b>" + pin.title + "</b><br>" + pin.description + "<br>" + pin.address + 
      "<br><a href='/pins/" + pin.id.to_s + "/edit'> Edit </a href> </p> " 
      marker.infowindow marker.infowindow info
    end
  end

  # GET /pins/1
  def show
  
  end

  # GET /pins/new
  def new
    @pin = Pin.new
  end

  # GET /pins/1/edit
  def edit
  end

  # POST /pins
  def create
    @pin = Pin.new(pin_params)

    if @pin.save
      redirect_to @pin, notice: 'Pin was successfully created.'
    else
      render :new
    end
  end

  # PATCH/PUT /pins/1
  def update
    if @pin.update(pin_params)
      redirect_to @pin, notice: 'Pin was successfully updated.'
    else
      render :edit
    end
  end

  # DELETE /pins/1
  def destroy
    @pin.destroy
    redirect_to pins_url, notice: 'Pin was successfully destroyed.'
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_pin
      @pin = Pin.find(params[:id])
    end

    # Only allow a trusted parameter "white list" through.
    def pin_params
      params.require(:pin).permit(:title, :description, :address, :latitude, :longitude)
    end
end
