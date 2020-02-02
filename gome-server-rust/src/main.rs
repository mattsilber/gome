mod client;
mod device;
mod server;
mod test_service;

use server::{Server, Connectible};

use std::thread;
use std::thread::sleep;
use std::time::Duration;

extern crate serde_json;

fn main() {
    let server_thread = thread::spawn(spawn_server_instance);
    let ui_thread = thread::spawn(spawn_interface_instance);

    device::print_local_ip_addresses();
//    test_service::run_test_connection();

    server_thread
        .join()
        .unwrap();
}

fn spawn_server_instance() {
    let server_instance = Server();
    let result = server_instance.connect("0.0.0.0", "13337");

    match result {
        Ok(_success) => {
            println!("Finished gracefully.");
        }
        Err(error) => {
            println!("I clearly fucked up somewhere: {}.", error);
        }
    }
}

fn spawn_interface_instance() {
    println!("Pretending that I'm actually starting a Thread for later...");
}