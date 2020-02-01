use std::net::TcpStream;
use std::io::{Result, BufReader, BufWriter, Write, BufRead};

pub fn run_test_connection() {
    let stream = TcpStream::connect("127.0.0.1:13337")
        .expect("Could not connect to host");

    write_test_data(&stream, r#"test-client"#.to_string());

    let identify_response = read_test_response(&stream);

    assert!(identify_response == "gome-server");

    write_test_data(&stream, r#"bad_command:{"bad_key":"bad_value"}"#.to_string());
}

fn write_test_data(stream: &TcpStream, data: String) {
    let line_broken_content = format!("{}\n", data);

    BufWriter::new(stream)
        .write_all(line_broken_content.as_bytes())
        .expect("Could not write response to client");
}

fn read_test_response(stream: &TcpStream) -> String {
    let mut request = String::new();

    BufReader::new(stream)
        .read_line(&mut request)
        .expect("Could not read request from client");

    println!("Read from server: {}", request.trim());

    return request.trim().to_string() //serde_json::from_str(request.as_str()).unwrap()
}