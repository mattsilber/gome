use std::net::{TcpListener, TcpStream};
use std::io::{Result, BufReader, BufWriter, Write, BufRead};

#[path = "./client.rs"] mod client;
use client::{Client, ClientConnectible};
use thread_pool::ThreadPool;

use std::thread::sleep;
use std::time::Duration;

#[path = "./mouse_commands.rs"] mod mouse_commands;
#[path = "./keyboard_commands.rs"] mod keyboard_commands;
#[path = "./thread_pool.rs"] mod thread_pool;

extern crate serde_json;

pub trait Connectible {
    fn connect(&self, ip: &str, port: &str) -> Result<()>;
    fn disconnect(&self);
}

pub struct Server();

impl Connectible for Server {

    fn connect(&self, ip: &str, port: &str) -> Result<()> {
        let ip_port = format!("{}:{}", ip, port);
        let listener = TcpListener::bind(&ip_port)?;
        let pool = ThreadPool::new(3);

        println!("Bound gome server to {}", ip_port);

        for stream in listener.incoming() {
            match stream {
                Ok(ok_streak) => {
                    pool.execute(|| client_connected(ok_streak))
                }
                Err(error) => {
                    println!("Client connection error {}", error);
                }
            }
        }

        Ok(())
    }

    fn disconnect(&self) {

    }
}

fn client_connected(stream: TcpStream) {
    let client = Client {
        stream: stream
    };

    let request = client.read();

    println!("Client connected with name: {}", request.to_string());

    write_identity(&client);
    await_commands(&client);
}

fn write_identity(client: &Client) {
    let data = r#"gome-server"#;

    client.write(data.to_string())
}

fn await_commands(client: &Client) {
    let request = client.read();

    process_command(client, request);
    await_commands(client);
}

fn process_command(client: &Client, response: String) {
    let command_index = response.find(":");

    if command_index.is_none() {
        println!("Command empty...");

        sleep(Duration::from_secs(1));

        return
    }

    let command_group = response.split_at(command_index.unwrap());
    let command_json_string = command_group.1.trim_start_matches(":");
    let command_json = serde_json::from_str(command_json_string).unwrap();

    match command_group.0 {
        "mouse" => mouse_commands::handle_mouse_command(command_json),
        "key" => keyboard_commands::handle_keyboard_command(command_json),
        _ => println!("Unknown command: {}", command_group.0)
    }
}