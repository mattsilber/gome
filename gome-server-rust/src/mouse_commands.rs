extern crate serde_json;
extern crate enigo;

use enigo::*;
use std::thread::sleep;
use std::time::Duration;

pub fn handle_mouse_command(data: serde_json::Value) {
    match data["type"].as_str().unwrap() {
        "move" => move_mouse(data),
        "scroll" => scroll(data),
        "left_single_click" => click_button(MouseButton::Left),
        "left_double_click" => {
            click_button(MouseButton::Left);

            sleep(Duration::from_nanos(30));

            click_button(MouseButton::Left);
        },
        "wheel_click" => click_button(MouseButton::Middle),
        "right_single_click" => click_button(MouseButton::Right),
        _ => println!("Unknown mouse command type"),
    }
}

fn move_mouse(data: serde_json::Value) {
    let mut enigo = Enigo::new();

    let offset_x = data["mouse_x"].as_i64().unwrap();
    let offset_y = data["mouse_y"].as_i64().unwrap();

    enigo.mouse_move_relative(offset_x as i32, offset_y as i32);
}

fn scroll(data: serde_json::Value) {
    let mut enigo = Enigo::new();

    let offset_x = data["mouse_x"].as_i64().unwrap();
    let offset_y = data["mouse_y"].as_i64().unwrap();

    enigo.mouse_scroll_x(offset_x as i32);
    enigo.mouse_scroll_y(offset_y as i32);
}

fn click_button(button: MouseButton) {
    let mut enigo = Enigo::new();

    enigo.mouse_click(button);
}