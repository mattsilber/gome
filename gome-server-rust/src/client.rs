use std::net::TcpStream;
use std::io::{BufReader, BufWriter, Write, BufRead};

extern crate serde_json;

pub trait ClientConnectible {
    fn read(&self) -> String;
    fn write(&self, data: String);
}

pub struct Client {
    pub stream: TcpStream
}

impl ClientConnectible for Client {

    fn read(&self) -> String {
        let mut request = String::new();

        BufReader::new(&self.stream)
            .read_line(&mut request)
            .expect("Could not read request from client");

        println!("Client requested: {}", request.trim());

        return request.trim().to_string() //serde_json::from_str(request.as_str()).unwrap()
    }

    fn write(&self, data: String) {
        println!("Sending client: {}", data);

        let line_broken_content = format!("{}\n", data);

        BufWriter::new(&self.stream)
            .write_all(line_broken_content.as_bytes())
            .expect("Could not write response to client");
    }
}