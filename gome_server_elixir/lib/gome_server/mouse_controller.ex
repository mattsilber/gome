require Logger

defmodule MouseController do
  @moduledoc

  @nir_cmd "nircmd.exe"

  def handle(command_data) do
    command_type = command_data["type"]

    case command_type do
      "move" ->
        move(command_data["mouse_x"], command_data["mouse_y"])
      "left_single_click" ->
        left_click()
      "left_double_click" ->
        left_double_click()
      "right_single_click" ->
        right_click()
      "wheel_click" ->
        wheel_click()
      "scroll" ->
        scroll(command_data["mouse_y"])
      _ ->
        Logger.info "Unhandled mouse action type: #{command_type}"
    end
  end

  def move(dx, dy) do
    #    {family, name} = :os.type()
    command = "#{@nir_cmd} movecursor #{dx} #{dy}"

    GomeServer.run_carelessly(command)
  end

  def left_click() do
    command = "#{@nir_cmd} sendmouse left click"

    GomeServer.run_carelessly(command)
  end

  def left_double_click() do
    command = "#{@nir_cmd} sendmouse left dblclick"

    GomeServer.run_carelessly(command)
  end

  def right_click() do
    command = "#{@nir_cmd} sendmouse right click"

    GomeServer.run_carelessly(command)
  end

  def wheel_click() do
    command = "#{@nir_cmd} sendmouse middle click"

    GomeServer.run_carelessly(command)
  end

  def scroll(value) do
    command = "#{@nir_cmd} sendmouse wheel #{value}"

    GomeServer.run_carelessly(command)
  end

end
