use std::{
    cell::RefCell,
    path::PathBuf,
    process::{Command, Output, Stdio},
};

// This is a global variable that stores the path.
// static mut M_PATH: OnceCell<String> = OnceCell::new();
static mut M_PATH: RefCell<String> = RefCell::new(String::new());

pub struct ShellUtils;

impl ShellUtils {
    /// Sets the path of an program.
    pub fn set_path(path: String) -> String {
        // unsafe { M_PATH.get_or_init(|| path).clone() }
        unsafe {
            let mut m_path = M_PATH.borrow_mut();
            *m_path = path;
            m_path.clone()
        }
    }

    /// Returns the full path of an ADB program.
    fn program_path(program: &str) -> PathBuf {
        // let path = unsafe { M_PATH.get_or_init(|| String::new()) };
        let path = unsafe { M_PATH.borrow().clone() };
        let mut program_path = PathBuf::from(path);
        program_path.push(program);
        program_path
    }

    /// Executes a shell command and returns the output as a `Result<Output>`.
    ///
    /// # Arguments
    ///
    /// * `program` - The shell command to execute.
    /// * `args` - The arguments to pass to the shell command.
    ///
    /// # Example
    ///
    /// ```
    /// use adb_core::utils::ShellUtils;
    ///
    /// let output = ShellUtils::shell("ls", vec!["-l"]);
    /// match output {
    ///     Ok(result) => {
    ///         println!("Command executed successfully");
    ///         println!("stdout: {}", String::from_utf8_lossy(&result.stdout));
    ///         println!("stderr: {}", String::from_utf8_lossy(&result.stderr));
    ///     }
    ///     Err(error) => {
    ///         println!("Command execution failed: {}", error);
    ///     }
    /// }
    /// ```
    pub fn shell<T: AsRef<str>>(program: T, args: Vec<T>) -> std::io::Result<Output> {
        let program = program.as_ref();
        let args = args.iter().map(|x| x.as_ref()).collect::<Vec<&str>>();

        if cfg!(debug_assertions) {
            println!("shell: {} {}", &program, &args.join(" ")); // Log command
        }

        let mut command = Command::new(Self::program_path(program));

        #[cfg(target_os = "windows")]
        {
            use std::os::windows::process::CommandExt;
            command.creation_flags(0x08000000); // CREATE_NO_WINDOW
        }

        command
            .args(args.clone())
            .stdout(Stdio::piped()) // Redirect output
            .stderr(Stdio::piped())
            .output()
    }

    /// Executes a shell command and returns the output as a `String`.
    ///
    /// # Arguments
    ///
    /// * `program` - The shell command to execute.
    /// * `args` - The arguments to pass to the shell command.
    ///
    /// # Example
    ///
    /// ```
    /// use adb_core::utils::ShellUtils;
    ///
    /// let output = ShellUtils::shell_to_string("ls", vec!["-l"]);
    /// println!("Command output: {}", output);
    /// ```
    pub fn shell_to_string<T: AsRef<str>>(program: T, args: Vec<T>) -> String {
        match Self::shell(program, args) {
            Ok(out) => {
                let stdout = String::from_utf8_lossy(&out.stdout).to_string();
                let stderr = String::from_utf8_lossy(&out.stderr).to_string();
                stderr + &stdout // Combine stdout and stderr
            }
            Err(err) => err.to_string(),
        }
    }

    /// Spawns a shell command and returns the child process.
    ///
    /// # Arguments
    ///
    /// * `program` - The shell command to execute.
    /// * `args` - The arguments to pass to the shell command.
    ///
    /// # Example
    ///
    /// ```
    /// use adb_core::utils::ShellUtils;
    ///
    /// let child = ShellUtils::shell_spawn("ls", vec!["-l"]);
    /// // Additional operations on the child process...
    /// ```
    pub fn shell_spawn<T: AsRef<str>>(program: T, args: Vec<T>) -> std::process::Child {
        let program = program.as_ref();
        let args = args.iter().map(|x| x.as_ref()).collect::<Vec<&str>>();

        if cfg!(debug_assertions) {
            println!("shell_spawn: {} {}", &program, &args.join(" ")); // Log command
        }

        let mut command = Command::new(Self::program_path(program));

        #[cfg(target_os = "windows")]
        {
            use std::os::windows::process::CommandExt;
            command.creation_flags(0x08000000); // CREATE_NO_WINDOW
        }

        command
            .args(args)
            .stdout(Stdio::piped()) // Redirect output
            .stderr(Stdio::piped())
            .spawn()
            .expect("Failed to execute command")
    }
}
