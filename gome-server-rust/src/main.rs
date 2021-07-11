mod client;
mod device;
mod server;
mod test_service;
mod thread_pool;
mod web;

use server::{Server, Connectible};
use web::{WebService, WebConnectible};
use thread_pool::ThreadPool;

fn main() {
    let pool = ThreadPool::new(2);
    pool.execute(spawn_server_instance);
    pool.execute(spawn_interface_instance);

    device::print_local_ip_addresses();
//    test_service::run_test_connection();
}

fn spawn_server_instance() {
    let server_instance = server::Server();
    let result = server_instance.connect("0.0.0.0", "13337");

    match result {
        Ok(_success) => {
            println!("gome controller service finished gracefully.");
        }
        Err(error) => {
            println!("I clearly fucked up somewhere: {}.", error);
        }
    }
}

fn spawn_interface_instance() {
    let server_instance = WebService();
    let result = server_instance.connect("0.0.0.0", "13338");

    match result {
        Ok(_success) => {
            println!("Web service finished gracefully.");
        }
        Err(error) => {
            println!("I clearly fucked up somewhere: {}.", error);
        }
    }
}