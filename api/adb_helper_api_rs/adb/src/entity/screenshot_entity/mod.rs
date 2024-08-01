use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct Screenshot {
    pub mimetype: String,
    pub width: i64,
    pub height: i64,
    pub data: Vec<u8>,
}
