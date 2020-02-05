require Logger

defmodule GomeServer do

  def connect(port) do
    {:ok, socket} = :gen_tcp.listen(port, [:binary, packet: :line, active: false, reuseaddr: true])

    Logger.info "Starting gome on 0.0.0.0:#{port}"

    await_next_connection(socket)
  end

  defp await_next_connection(socket) do
    {:ok, client} = :gen_tcp.accept(socket)
    {:ok, pid} = Task.Supervisor.start_child(GomeServer.TaskSupervisor, fn -> client_connected(client) end)

    :ok = :gen_tcp.controlling_process(client, pid)

    await_next_connection(socket)
  end

  defp client_connected(socket) do
    _ = socket
    |> read()
    |> also_log_client_response()

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

    {command, _} = decode_compat_command(data)

    case command do
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
  end

  defp write(data, socket) do
    Logger.info "Sending: #{data}"

    :gen_tcp.send(socket, "#{data}\r\n")
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

    {String.slice(data, 0..index), String.slice(data, (index + 1)..-1)}
  end
end