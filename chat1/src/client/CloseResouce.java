package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CloseResouce {
	
	public static void closeInput(DataInputStream input) {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			input = null;
		}
	}

	public static void closeOutput(DataOutputStream output) {
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			output = null;
		}
	}

	public static void closeFileInput(FileInputStream input) {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			input = null;
		}
	}

	public static void closeFileOutput(FileOutputStream output) {
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			output = null;
		}
	}
	
	public static void closeSocket(Socket s) {
		if (s != null) {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			s = null;
		}
	}

	public static void closeServerSocket(ServerSocket ss) {
		if (ss != null) {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ss = null;
		}
	}

}
