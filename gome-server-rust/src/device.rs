extern crate pnet;
use pnet::datalink;
use std::iter;

pub fn with_local_ip_addresses(each: fn(String)) {
    datalink::interfaces()
        .iter()
        .flat_map(|it| it.ips.iter())
        .filter(|it| it.is_ipv4())
        .map(|it| it.ip().to_string())
        .filter(|it| !it.eq_ignore_ascii_case("127.0.0.1"))
        .for_each(each);
}

pub fn print_local_ip_addresses() {
    return with_local_ip_addresses(|it|
        println!("Local IP: {:?}", it)
    )
}