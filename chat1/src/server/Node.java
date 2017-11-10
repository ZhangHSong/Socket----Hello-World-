package server;

import java.awt.Image;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 * 节点类
 * 
 * @author HaiSong.Zhang
 *
 */
public class Node implements Runnable {

	String username = null; // 用户名
	Socket socket = null;
	private boolean bConnected = false;

	DataOutputStream output = null;
	DataInputStream input = null;
	FileOutputStream fOutput = null;
	FileInputStream fInput = null;

	Node next = null;
	private ClientLinkList cll = null;

	private Calendar c = null;
	private SimpleDateFormat f = null;
	private JTextArea showChatMSG = null;
	private JLabel showImage;// 显示图片
	private boolean linked = false;
	private DefaultTableModel tableModel;

	//

	public Node() {
	}

	/*
	 * 构造方法
	 */
	public Node(Socket s, ClientLinkList cll, JTextArea showChatMSG, boolean linked, JLabel showImage,
			DefaultTableModel tableModel) {

		this.socket = s;
		this.cll = cll;
		this.showChatMSG = showChatMSG;
		this.linked = linked;
		this.showImage = showImage;
		this.tableModel = tableModel;
		try {
			output = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());
			bConnected = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setLinked(boolean linked) {
		linked = true;
	}

	public void sendString(String startName, String endName, String str) {
		try {
			output.writeUTF("聊天信息");
			output.flush();
			output.writeUTF(startName);
			output.flush();
			output.writeUTF(endName);
			output.flush();
			output.writeUTF(str);
			output.flush();
		} catch (IOException e) {
			System.out.println("对方退出了");
		}
	}

	public void sendFile(String startName, String endName, File file, String str, String objLastName) {
		try {
			if (str.equals("file")) {
				output.writeUTF("文件");
				output.flush();
			} else if (str.equals("image")) {
				output.writeUTF("图片");
				output.flush();
			}
			output.writeUTF(objLastName);
			output.flush();
			output.writeUTF(startName);
			output.flush();
			fInput = new FileInputStream(file);
			byte[] sendByte = new byte[1024];
			int length;
			while ((length = fInput.read(sendByte, 0, sendByte.length)) != -1) {
				output.write(sendByte, 0, length);
				output.flush();
				if (length < sendByte.length) {
					// socket.shutdownOutput();
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("对方退出了");
		}
	}

	// 发送列表信息
	public void sendList() {
		String userlist = "";
		for (int i = 0; i < cll.getClientCount(); i++) {
			Node node = cll.findNodeByIndex(i);
			if (node != null) {
				userlist += node.username;
				userlist += '\n';
			}
		}
		for (int i = 0; i < cll.getClientCount(); i++) {
			Node node = cll.findNodeByIndex(i);
			if (node != null) {
				try {
					node.output.writeUTF("用户列表");
					node.output.flush();
					node.output.writeUTF(userlist);
					node.output.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		String[] userlists = userlist.split("\n");
		int m = tableModel.getRowCount();
		for (int i = m - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
		String[] str1 = { "所有人" };
		tableModel.addRow(str1);
		for (int i = 0; i < userlists.length; i++) {
			String[] str3 = { userlists[i] };
			tableModel.addRow(str3);
		}
		tableModel.fireTableDataChanged();
	}

	public void run() {
		c = Calendar.getInstance();
		f = new SimpleDateFormat("HH:mm:ss");
		while (bConnected) {
			try {
				if (linked == true) {
					sendList();
					linked = false;
				}
				if (bConnected) {
					String infoType = input.readUTF().toString();// 信息类型
					String obj = input.readUTF().toString();

					if (infoType.equals("聊天信息")) {
						String str = input.readUTF().toString();
						showChatMSG
								.append(f.format(c.getTime()) + "—" + username + "对" + obj + " 说:" + '\n' + str + '\n');
						if (obj.equalsIgnoreCase("所有人")) {
							for (int i = 0; i < cll.getClientCount(); i++) {
								if (!cll.findNodeByIndex(i).username.equals(username))
									cll.findNodeByIndex(i).sendString(username, obj, str);
							}
						} else if (obj.equalsIgnoreCase("服务器")) {

						} else {
							cll.findNodeByName(obj).sendString(username, obj, str);
						}
					} else if (infoType.equals("文件")) {
						String objLastName = input.readUTF().toString();// 后缀名
						File objFile = new File("F:\\test." + objLastName);
						if (!objFile.exists()) {
							objFile.createNewFile();
						}
						fOutput = new FileOutputStream(objFile);
						byte[] buf = new byte[1024];
						int len;
						while ((len = input.read(buf, 0, buf.length)) != -1) {
							fOutput.write(buf, 0, len);
							fOutput.flush();
							if (len < buf.length) {
								// socket.shutdownInput();
								break;
							}
						}
						showChatMSG.append(f.format(c.getTime()) + "—" + username + "对" + obj + "发送了文件" + '\n');
						if (obj.equalsIgnoreCase("所有人")) {
							for (int i = 0; i < cll.getClientCount(); i++) {
								if (!cll.findNodeByIndex(i).username.equals(username))
									cll.findNodeByIndex(i).sendFile(username, obj, objFile, "file", objLastName);
							}
						} else if (obj.equalsIgnoreCase("服务器")) {

						} else {
							cll.findNodeByName(obj).sendFile(username, obj, objFile, "file", objLastName);
						}
					} else if (infoType.equals("图片")) {
						String objLastName = input.readUTF().toString();// 后缀名
						File objImage = new File("F:\\test." + objLastName);
						if (!objImage.exists()) {
							objImage.createNewFile();
						}
						fOutput = new FileOutputStream(objImage);
						byte[] buf = new byte[1024];
						int len;
						while ((len = input.read(buf, 0, buf.length)) != -1) {
							fOutput.write(buf, 0, len);
							fOutput.flush();
							if (len < buf.length) {
								// socket.shutdownInput();
								break;
							}
						}
						showChatMSG.append(f.format(c.getTime()) + "—" + username + "对" + obj + "发送了图片" + '\n');
						if (obj.equalsIgnoreCase("所有人")) {
							for (int i = 0; i < cll.getClientCount(); i++) {
								if (!cll.findNodeByIndex(i).username.equals(username))
									cll.findNodeByIndex(i).sendFile(username, obj, objImage, "image", objLastName);
							}
						} else if (obj.equalsIgnoreCase("服务器")) {

						} else {
							cll.findNodeByName(obj).sendFile(username, obj, objImage, "image", objLastName);
						}
						ImageIcon image = new ImageIcon("F:\\test." + objLastName);
						image.setImage(image.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
						showImage.setIcon(image);
					} else if (infoType.equals("关闭")) {
						showChatMSG.append(f.format(c.getTime()) + "1客户端" + username + "下线了" + '\n');
						cll.delNode(username);
						tableModel.fireTableDataChanged();
						sendList();
						bConnected = false;
					}
				}
			} catch (EOFException e) {
				bConnected = false;
				showChatMSG.append(f.format(c.getTime()) + "2客户端" + username + "下线了" + '\n');
				cll.delNode(username);
				tableModel.fireTableDataChanged();
				sendList();
				// e.printStackTrace();
				// System.exit(0);
			} catch (Exception e) {
				bConnected = false;
				showChatMSG.append(f.format(c.getTime()) + "3客户端" + username + "下线了" + '\n');
				cll.delNode(username);
				tableModel.fireTableDataChanged();
				sendList();
				// e.printStackTrace();
			}
		}
	}

	public String getUsername() {
		return username;
	}

	public void close() {
		try {
			socket.shutdownInput();
			socket.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}