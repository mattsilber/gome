require Logger

defmodule GomeServer do

  def connect(port) do
    Logger.info "Starting gome on 0.0.0.0:#{port}"

    local_network_ips()
    |> Enum.map(fn ip -> "Local network IP address: " <> ip end)
    |> Enum.join("\n")
    |> Logger.info

    {:ok, socket} = :gen_tcp.listen(port, [:binary, packet: :line, active: false, reuseaddr: true])

    await_next_connection(socket)

    :gen_tcp.close(socket)
  end

  defp await_next_connection(socket) do
    {:ok, client} = :gen_tcp.accept(socket)
    {:ok, pid} = Task.Supervisor.start_child(GomeServer.TaskSupervisor, fn -> client_connected(client) end)

    :ok = :gen_tcp.controlling_process(client, pid)

    await_next_connection(socket)
  end

  defp client_connected(socket) do
    client_identity = socket
    |> read()
    |> also_log_client_response()
    |> JSON.decode!

    Logger.info "Client [#{client_identity["name"]}] connected"

    respond_with_server_identity(socket)
    await_next_command(socket)
  end

  defp respond_with_server_identity(socket) do
    write("gome-server", socket)
  end

  defp await_next_command(socket) do
    socket
    |> read()
    |> handle_client_command(socket)
  end

  defp handle_client_command(data, socket) do
    Logger.info "Received command: #{data}"

    {command_key, command_data} = decode_compat_command(data)

    case command_key do
      "mouse" ->
        Logger.info "Mouse Command"
      _ ->
        Logger.info "Unknown command name"
    end

    await_next_command(socket)
  end

  defp read(socket) do
    {:ok, data} = :gen_tcp.recv(socket, 0)

    data
    |> String.trim("\r\n")
  end

  defp write(data, socket) do
    Logger.info "Sending: #{data}"

    :ok = :gen_tcp.send(socket, "#{data}\r\n")

    {:ok}
  end

  defp also_log_client_response(data) do
    Logger.info "Received from client: #{data}"

    data
  end

  defp decode_compat_command(data) do
    Regex.run(~r/:/, data, return: :index)
    |> split_compat_command_from_request(data)
  end

  defp split_compat_command_from_request(expression_result, data) do
    {index, _} = hd(expression_result)
    {key, value} = {String.slice(data, 0..(index - 1)), String.slice(data, (index + 1)..-1)}

    {key, JSON.decode!(value)}
  end

  defp local_network_ips() do
    :inet.getifaddrs()
    |> elem(1)
    |> Enum.flat_map(fn {_, value} ->
      value
      |> Keyword.get_values(:addr)
      |> Keyword.to_list()
      |> Enum.map(&Tuple.to_list/1)
      |> Enum.filter(fn address_components ->
        4 == length(address_components)
      end)
      |> Enum.map(fn address_components ->
        Enum.join(address_components, ".")
      end)
      |> Enum.filter(fn ip ->
        "127.0.0.1" != ip
      end)
    end)
  end
end