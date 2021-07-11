require Logger

defmodule GomeServer.Application do
  @moduledoc false

  use Application

  def start(_type, _args) do
    Logger.info "Starting application..."

    children = [
      {Task.Supervisor, name: GomeServer.TaskSupervisor},
      Supervisor.child_spec(
        {Task, fn -> GomeServer.connect(13337) end},
        restart: :permanent)
    ]

    opts = [
      strategy: :one_for_one,
      name: GomeServer.Supervisor
    ]

    Supervisor.start_link(children, opts)
  end
end