require Logger

defmodule KeyboardController do
  @moduledoc

  @nir_cmd "nircmd.exe"

  def handle(command_data) do
    command_type = command_data["type"]
    wrapped = wrapped_for_nir(command_data["wrapped"])

    case command_type do
      "string" ->
        send_keys(command_data["value"], wrapped)
      "key_code_action" ->
        send_key_action(command_data["value"], wrapped)
      _ ->
        Logger.info "Unhandled keyboard action type: #{command_type}"
    end
  end

  def send_keys(value, wrapped) do
    nir_key_press_cmds(value, wrapped)
    |> Enum.each(&(GomeServer.run_carelessly/1))
  end

  def send_key_action(value_code, wrapped) do
    key_for_nir = nir_key_from_action_code(value_code)

    nir_key_press_cmds(key_for_nir, wrapped)
    |> Enum.each(&(GomeServer.run_carelessly/1))
  end

  defp nir_key_press_cmds(value, wrapped) do
    String.graphemes(value)
    |> Enum.map(fn character -> nir_keycode(character) end)
    |> Enum.map(fn group -> Enum.join(wrapped ++ [group], "+") end)
    |> Enum.map(fn command_set -> "#{@nir_cmd} sendkeypress #{command_set}" end)
  end

  defp nir_keycode(value) do
    cond do
      value == " " -> "spc"
      String.match?(value, ~r/^\p{Lu}$/u) -> "shift+#{value}"
      true -> value
    end
  end

  defp wrapped_for_nir(data) do
    data
    |> Enum.map(fn entity ->
      case entity do
        "ALT" -> "alt"
        "CTRL" -> "ctrl"
        "SHIFT" -> "shift"
        _ -> :error
      end
    end)
  end

  defp nir_key_from_action_code(value) do
    case value do
      27 -> "esc"
      35 -> "end"
      36 -> "home"
      155 -> "insert"
      _ -> :error
    end
  end

end
