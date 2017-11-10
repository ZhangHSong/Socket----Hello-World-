package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * �ͻ����������
 * 
 * @author HaiSong.Zhang
 *
 */
public class ClientFrame extends JFrame {

	private JTextArea ta = new JTextArea();
	private JTextField tf = new JTextField();
	private JLabel label = new JLabel();
	private JButton btn = new JButton("����");
	private JButton btn1 = new JButton("+");
	private JPanel panel = new JPanel();

	private boolean started = true;
	private String username = null;
	public static final int serverPort = 8888;// ����˶˿ں�

	// ������
	private JScrollPane jScrollPane1 = null;// ���������
	private JTable heroTable = null;// ������
	private ListSelectionModel listSelectionModel = null;
	// ���ͷ��
	private String[] tableColName = { "�ҵĺ���" };
	private String[][] tableColValue = null;// ����ֵ
	private GridBagLayout gridBag = new GridBagLayout();// ���ֹ�����
	private GridBagConstraints gridBagCon = null;
	private Socket s = null;
	private DataInputStream input = null;
	private DataOutputStream output = null;
	private FileOutputStream fOutput = null;
	private FileInputStream fInput = null;
	private DefaultTableModel tableModel = null;
	private String obj = null;
	private Calendar c = null;
	private SimpleDateFormat f = null;
	private Font font2 = new Font("΢���ź�", 0, 15);
	private JScrollPane MSGScrollPane;// ��Ϣ��ʾ���ڵĹ�����
	private JFileChooser chooser = null;
	private JLabel showImage;// ��ʾͼƬ

	/**
	 * ���췽��
	 */
	public ClientFrame(String username) {

		this.username = username;
		LanchFrame();
	}

	/**
	 * �����ʼ��
	 */
	public void LanchFrame() {

		// �����������
		this.setTitle("Hello World");
		this.setBounds(450, 200, 470, 450);

		// ���Զ�λ
		setLayout(null);

		panel.setLayout(gridBag);
		tableColValue = new String[2][1];
		tableColValue[0][0] = "������";
		tableColValue[1][0] = "������";
		tableModel = new DefaultTableModel(tableColValue, tableColName);
		// ��ʼ������е���Ϣ
		heroTable = new JTable(tableModel) {
			public boolean isCellEditable(int row, int column) {
				return false; // ���ɱ༭
			}
		};
		heroTable.setFont(new Font("΢���ź�", 0, 15));
		heroTable.setRowHeight(30);
		heroTable.setPreferredScrollableViewportSize(new Dimension(120, 320));
		listSelectionModel = heroTable.getSelectionModel();
		// ���ñ��ѡ��ʽ:ѡ��һ��
		listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSelectionModel.addListSelectionListener(new SelectListener());
		jScrollPane1 = new JScrollPane(heroTable);
		jScrollPane1.setPreferredSize(new Dimension(120, 320));
		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 0;
		gridBagCon.gridy = 1;
		gridBagCon.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(jScrollPane1, gridBagCon);
		panel.add(jScrollPane1);
		panel.setBounds(0, 0, 120, 320);

		// ��ǰ���������
		label.setBounds(120, 0, 350, 30);

		// ���ı��������
		ta.setEditable(false);
		ta.setFont(font2);
		ta.setLineWrap(true);
		// Ϊ������Ϣ����ӹ�����
		MSGScrollPane = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		MSGScrollPane.setPreferredSize(new Dimension(400, 200));
		MSGScrollPane.setBounds(120, 30, 350, 360);
		this.add(MSGScrollPane);
		// ����������ĳ�������е�����Ĵ�С�Ժ���Ҫ���ð�����������������revalidate����������
		// Revalidate�������������¼������������е��齨�Ĵ�С�����Ҷ����ǽ������²���
		MSGScrollPane.revalidate();

		showImage = new JLabel();
		showImage.setBounds(0, 310, 120, 120);
		this.add(showImage);

		// ���ı������
		tf.setBounds(120, 390, 240, 31);
		tf.addActionListener(new TfListener());

		// ���ļ���ͼƬ���в���
		btn1.setBounds(355, 390, 45, 30);
		btn1.setBackground(new Color(250, 250, 250));
		btn1.setFocusPainted(false); // ȥ����ť������Χ�Ľ����
		btn1.addActionListener(new Btn1Listener());

		// �԰�ť����
		btn.setBounds(400, 390, 70, 30);
		btn.setBackground(new Color(250, 250, 250));
		btn.setFocusPainted(false); // ȥ����ť������Χ�Ľ����
		btn.addActionListener(new BtnListener());

		add(label);
		add(panel);
		add(tf);
		add(btn1);
		add(btn);

		setResizable(false);
		setBackground(Color.white);
		setVisible(true);
		setTitle("Hello World ���� " + username);

		connect();

		// �رճ���ʱ����
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int flag = JOptionPane.showConfirmDialog(null, "ȷ��Ҫ�˳��ͻ�����?", "�˳�", JOptionPane.YES_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (flag == JOptionPane.YES_OPTION) {
					try {
						output.writeUTF("�ر�");
						output.flush();
						s.shutdownOutput();
						s.shutdownInput();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					disconnect();
					System.exit(0);
				}
			}
		});
	}

	public void connect() {

		try {
			s = new Socket("127.0.0.1", serverPort);
			output = new DataOutputStream(s.getOutputStream());
			input = new DataInputStream(s.getInputStream());
			output.writeUTF(username);
			output.flush();
			new Thread(new ClientReceive()).start();
		} catch (ConnectException e) {
			JOptionPane.showMessageDialog(null, "����", "�޷���¼,������δ����!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {

		if (started == true)
			started = false;
		CloseResouce.closeSocket(s);
		CloseResouce.closeInput(input);
		CloseResouce.closeOutput(output);
		CloseResouce.closeFileInput(fInput);
		CloseResouce.closeFileOutput(fOutput);
	}

	private class SelectListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			// ȡ��ѡ�е��к�
			int index = heroTable.getSelectedRow();

			// ����ѡ�û������浽str��
			obj = tableModel.getValueAt(index, 0).toString();
			label.setText(obj);
		}

	}

	public void action() {
		c = Calendar.getInstance();
		f = new SimpleDateFormat("HH:mm:ss");
		String str = tf.getText().trim();// ȥ���ı�ǰ��Ŀո�
		if (str.equals(""))
			JOptionPane.showMessageDialog(null, "����", "�������ݲ���Ϊ�գ�", JOptionPane.ERROR_MESSAGE);
		else {
			tf.setText("");
			if (obj == null) {
				JOptionPane.showMessageDialog(null, "��ѡ����ܶ���");
				return;
			}
			try {
				output.writeUTF("������Ϣ");
				output.flush();
				output.writeUTF(obj);
				output.flush();
				output.writeUTF(str);
				output.flush();
				if (obj.equals("������")) {
					ta.append(f.format(c.getTime()) + "��" + "��������˵:" + '\n' + str + '\n');
				} else if (obj.equals("������")) {
					ta.append(f.format(c.getTime()) + "��" + "�Է�����˵:" + '\n' + str + '\n');
				} else {
					ta.append(f.format(c.getTime()) + "(" + username + ")" + '\n' + str + '\n');
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * �ı������
	 * 
	 * @author HaiSong.Zhang
	 *
	 */
	private class TfListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			action();
		}

	}

	/**
	 * ���ӹ��ܰ�ť����
	 * 
	 * @author HaiSong.Zhang
	 *
	 */
	private class Btn1Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(true);
			FileNameExtensionFilter filter1 = new FileNameExtensionFilter("PNG & GIF & JPG Images", "jpg", "gif",
					"png");
			FileNameExtensionFilter filter2 = new FileNameExtensionFilter("TXT & DOC & DOCX File", "txt", "doc",
					"docx");
			FileNameExtensionFilter filter3 = new FileNameExtensionFilter("MP3 & MP4 File", "mp3", "mp4");
			chooser.addChoosableFileFilter(filter1);
			chooser.addChoosableFileFilter(filter2);
			chooser.addChoosableFileFilter(filter3);
			int returnVal = chooser.showOpenDialog(panel);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if (obj == null) {
					JOptionPane.showMessageDialog(null, "��ѡ����ܶ���");
					return;
				}
				try {
					String objname = chooser.getSelectedFile().getName();
					String objLastName = objname.substring(objname.lastIndexOf(".") + 1);
					if (objLastName.equals("png") || objLastName.equals("jpg") || objLastName.equals("gif")) {
						output.writeUTF("ͼƬ");
						output.flush();
						output.writeUTF(obj);
						output.flush();
						output.writeUTF(objLastName);
						output.flush();
						fInput = new FileInputStream(chooser.getSelectedFile());
						byte[] sendByte = new byte[1024];
						int length;
						while ((length = fInput.read(sendByte, 0, sendByte.length)) != -1) {
							output.write(sendByte, 0, length);
							output.flush();
							if (length < sendByte.length) {
								// s.shutdownOutput();
								break;
							}
						}
						if (obj.equals("������")) {
							ta.append(f.format(c.getTime()) + "�� �������˶�������ͼƬ" + '\n');
						} else if (obj.equals("������")) {
							ta.append(f.format(c.getTime()) + "�� �������������ͼƬ" + '\n');
						} else {
							ta.append(f.format(c.getTime()) + "�� ��" + obj + "������ͼƬ" + '\n');
						}
						ImageIcon image = new ImageIcon(chooser.getSelectedFile().getPath());
						image.setImage(image.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
						showImage.setIcon(image);
					} else if (objLastName.equals("txt") || objLastName.equals("doc") || objLastName.equals("docx")
							|| objLastName.equals("mp3") || objLastName.equals("mp4")) {
						output.writeUTF("�ļ�");
						output.flush();
						output.writeUTF(obj);
						output.flush();
						output.writeUTF(objLastName);
						output.flush();
						fInput = new FileInputStream(chooser.getSelectedFile());
						byte[] sendByte = new byte[1024];
						int length;
						while ((length = fInput.read(sendByte, 0, sendByte.length)) != -1) {
							output.write(sendByte, 0, length);
							output.flush();
							if (length < sendByte.length) {
								// s.shutdownOutput();
								break;
							}
						}
						if (obj.equals("������")) {
							ta.append(f.format(c.getTime()) + "�� �������˶��������ļ�" + '\n');
						} else if (obj.equals("������")) {
							ta.append(f.format(c.getTime()) + "�� ��������������ļ�" + '\n');
						} else {
							ta.append(f.format(c.getTime()) + "�� ��" + obj + "�������ļ�" + '\n');
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	/**
	 * ���Ͱ�ť����
	 * 
	 * @author HaiSong.Zhang
	 *
	 */
	private class BtnListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			action();
		}

	}

	private class ClientReceive implements Runnable {

		@Override
		public void run() {
			c = Calendar.getInstance();
			f = new SimpleDateFormat("HH:mm:ss");
			String objLastName = null;
			String startName = null;
			while (started) {
				try {
					String infoType = input.readUTF().toString();// ��Ϣ����
					if (infoType.equals("������Ϣ")) {
						startName = input.readUTF().toString();
						String endName = input.readUTF().toString();
						String str = (String) input.readUTF();
						if (endName.equals("������")) {
							ta.append(f.format(c.getTime()) + "��" + startName + "��" + endName + " ˵:" + '\n' + str
									+ '\n');
						} else {
							ta.append(f.format(c.getTime()) + "(" + startName + ")" + '\n' + str + '\n');
						}
					} else if (infoType.equals("�û��б�")) {
						String userlist = input.readUTF().toString();
						String[] userlists = userlist.split("\n");
						int m = tableModel.getRowCount();
						for (int i = m - 1; i >= 0; i--) {
							tableModel.removeRow(i);
						}
						String[] str1 = { "������" };
						tableModel.addRow(str1);
						String[] str2 = { "������" };
						tableModel.addRow(str2);
						for (int i = 0; i < userlists.length; i++) {
							if (!username.equals(userlists[i])) {
								String[] str3 = { userlists[i] };
								tableModel.addRow(str3);
							}
						}
						tableModel.fireTableDataChanged();
					} else if (infoType.equals("ϵͳ��Ϣ")) {
						String str = input.readUTF().toString();
						ta.append(f.format(c.getTime()) + "�� ϵͳ��Ϣ:    " + str + '\n');

					} else if (infoType.equals("ͼƬ")) {
						objLastName = input.readUTF().toString();
						startName = input.readUTF().toString();
						int flag = JOptionPane.showConfirmDialog(null, startName + "�������㷢��ͼƬ,�Ƿ����?", "����",
								JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (flag == JOptionPane.YES_OPTION) {
							File objImage = new File("F:\\obj." + objLastName);
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
									// s.shutdownInput();
									break;
								}
							}
							ImageIcon image = new ImageIcon("F:\\obj." + objLastName);
							image.setImage(image.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
							showImage.setIcon(image);
							ta.append(f.format(c.getTime()) + "��" + "�Ѿ�������" + startName + "���㷢�͵�ͼƬ,�洢��"
									+ objImage.getPath() + "��" + '\n');
						}

					} else if (infoType.equals("�ļ�")) {
						objLastName = input.readUTF().toString();
						startName = input.readUTF().toString();
						int flag = JOptionPane.showConfirmDialog(null, startName + "�������㷢���ļ�,�Ƿ����?", "����",
								JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (flag == JOptionPane.YES_OPTION) {
							File objFile = new File("F:\\obj." + objLastName);
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
									// s.shutdownInput();
									break;
								}
							}
							ta.append(f.format(c.getTime()) + "��" + "���ѽ���" + startName + "���㷢�͵��ļ�,�洢��"
									+ objFile.getPath() + "��" + '\n');
						} else {
							return;
						}
					}
				} catch (EOFException | SocketException e) {
					//e.printStackTrace();
					disconnect();
					System.exit(0);
				} catch (Exception e) {
					disconnect();
					System.exit(0);
					//e.printStackTrace();
				}
			}
		}

	}

/*	
	 * ��Ԫ����
	 
	public static void main(String[] args) {
		new ClientFrame("test");
	}*/
}
