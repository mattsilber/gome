extern crate pnet;
use pnet::datalink;
use std::iter;

pub fn collect_local_ip_addresses() -> Vec<String> {
    return datalink::interfaces()
        .iter()
        .flat_map(|it| it.ips.iter())
        .filter(|it| it.is_ipv4())
        .map(|it| it.ip().to_string())
        .filter(|it| !it.eq_ignore_ascii_case("127.0.0.1"))
        .collect();
}

pub fn print_local_ip_addresses() {
    collect_local_ip_addresses()
        .iter()
        .for_each(|it| println!("Local IP: {:?}", it))
}