use std::{
    io::{BufRead, BufReader},
    net::TcpStream,
};

fn main() {
    let stream = TcpStream::connect("localhost:5656").expect("failed connect to server");
    loop {
        let reader = BufReader::new(&stream);
        for line in reader.lines() {
            println!("{}", line.unwrap());
        }
    }
}
