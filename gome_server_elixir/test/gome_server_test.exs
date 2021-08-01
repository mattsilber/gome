defmodule GomeServerTest do
  use ExUnit.Case
  doctest GomeServer

  test "the client and server can identify themselves" do
    {_, identity} = connect_and_identify()

    assert "gome-server" == identity
  end

  defp connect_and_identify() do
    {:ok, socket} = :gen_tcp.connect('localhost', 13337, [:binary, packet: :line, active: false])
    {:ok, identity} = JSON.encode([name: "Call-Me-Stacy"])

    :ok = write(socket, identity)

    {socket, read_line(socket)}
  end

  defp write(socket, json) do
    :gen_tcp.send(socket, "#{json}\r\n")
  end

  defp read_line(socket) do
    {:ok, data} = :gen_tcp.recv(socket, 0)

    data |> String.trim("\r\n")
  end
end
