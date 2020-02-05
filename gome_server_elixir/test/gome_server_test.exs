defmodule GomeServerTest do
  use ExUnit.Case
  doctest GomeServer

  test "greets the world" do
    assert GomeServer.hello() == :world
  end
end
