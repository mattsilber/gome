extern crate serde_json;
extern crate enigo;

use enigo::*;
use std::iter::Map;

pub fn handle_keyboard_command(data: serde_json::Value) {

    match data["type"].as_str().unwrap() {
        "string" => type_key_value(data),
        "action" => type_key_action(data),
        _ => println!("Unknown mouse command type"),
    }
}

fn type_key_value(data: serde_json::Value) {
    let mut enigo = Enigo::new();

    let value = data["value"].as_str().unwrap();

    for key in wrapped_keys(data.clone()) {
        enigo.key_down(key)
    }

    enigo.key_sequence(value);

    for key in wrapped_keys(data.clone()) {
        enigo.key_up(key)
    }
}

fn type_key_action(data: serde_json::Value) {
    let mut enigo = Enigo::new();

    let value = data["value"].as_i64().unwrap();
    let key = key_for_int(value);

    for key in wrapped_keys(data.clone()) {
        enigo.key_down(key)
    }

    enigo.key_click(key);

    for key in wrapped_keys(data.clone()) {
        enigo.key_up(key)
    }
}

fn wrapped_keys(data: serde_json::Value) -> Vec<Key> {
    return data["wrapped"]
        .as_array()
        .unwrap()
        .iter()
        .map(|it| it.to_string())
        .map(|it| key_for_string(it))
        .collect();
}

fn key_for_string(value: String) -> Key {
    return match value.as_str() {
        "ALT" => Key::Alt,
        "CTRL" => Key::Control,
        "SHIFT" => Key::Shift,
        _ => Key::Escape
    }
}

fn key_for_int(value: i64) -> Key {
    return match value {
        27 => Key::Escape,
        35 => Key::End,
        36 => Key::Home,
        _ => Key::Escape
    }
}