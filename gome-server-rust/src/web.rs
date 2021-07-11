use std::net::{TcpListener, TcpStream};
use std::io::{Result, BufReader, BufWriter, Write, BufRead, Read};

use std::thread::sleep;
use std::time::Duration;

#[path = "./server.rs"] mod server;
#[path = "./thread_pool.rs"] mod thread_pool;
use thread_pool::ThreadPool;

pub trait WebConnectible {
    fn connect(&self, ip: &str, port: &str) -> Result<()>;
    fn disconnect(&self);
}

pub struct WebService();

impl WebConnectible for WebService {

    fn connect(&self, ip: &str, port: &str) -> Result<()> {
        let ip_port = format!("{}:{}", ip, port);
        let listener = TcpListener::bind(&ip_port)?;
        let pool = ThreadPool::new(5);

        println!("Bound web server to {}", ip_port);

        for stream in listener.incoming() {
            match stream {
                Ok(ok_streak) => {
                    pool.execute(|| web_client_connected(ok_streak))
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

fn web_client_connected(mut stream: TcpStream) {
    let request = readRequest(&stream);

    let content = r#"<!DOCTYPE HTML>
    <html>
        <head></head>
        <body>
            <h1>gome</h1>
            <p>Much to do...</p>
        </body>
    </html>"#;

    let formatted = format!("HTTP/1.1 200 OK\r\n\r\n{}", content);

    write(&stream, formatted.to_string());
}

fn readRequest(stream: &TcpStream) -> String {
    let mut request = String::new();

    BufReader::new(stream)
        .read_line(&mut request)
        .expect("Could not read request from client");

    println!("Web client requested: {}", request.trim());

    request.to_string()
}

fn write(stream: &TcpStream, data: String) {
    println!("Sending web client: {}", data);

    let line_broken_content = format!("{}\n", data);

    BufWriter::new(stream)
        .write_all(line_broken_content.as_bytes())
        .expect("Could not write response to web client");
}