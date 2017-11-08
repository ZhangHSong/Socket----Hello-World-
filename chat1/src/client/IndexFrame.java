package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * 注册登录界面
 * 
 * @author HaiSong.Zhang
 *
 */
public class IndexFrame extends JFrame {

	private Font font1 = new Font("微软雅黑", Font.LAYOUT_LEFT_TO_RIGHT, 15);
	private Font font2 = new Font("微软雅黑", Font.BOLD, 12);

	private JLabel lb1 = new JLabel("用户：");
	private JTextField tf = new JTextField();
	private JLabel lb2 = new JLabel("密码：");
	private JPasswordField pf = new JPasswordField();

	private JButton btn1 = new JButton("登录");
	private JButton btn2 = new JButton("注册");

	Database db = new Database();

	/**
	 * 界面初始化方法
	 */
	public void IFrame() {

		setTitle("登录界面");
		setBounds(450, 250, 400, 250);
		setBackground(Color.blue);

		setLayout(null);
		add(lb1);
		add(lb2);
		add(tf);
		add(pf);
		add(btn1);
		add(btn2);

		lb1.setBounds(50, 50, 40, 30);
		lb2.setBounds(50, 90, 40, 30);
		lb1.setFont(font2);
		lb2.setFont(font2);

		tf.setBounds(100, 50, 200, 30);
		tf.setFont(font1);

		pf.setBounds(100, 90, 200, 30);

		btn1.setBounds(110, 140, 70, 30);
		btn1.setFont(font2);
		btn1.setFocusPainted(false); // 去掉按钮文字周围的焦点框
		btn1.addActionListener(new Btn1Listener());

		btn2.setBounds(210, 140, 70, 30);
		btn2.setFont(font2);
		btn2.setFocusPainted(false);
		btn2.addActionListener(new Btn2Listener());

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {

				System.exit(0);
			}

		});
		setResizable(false);
		setVisible(true);

	}

	/**
	 * 正则表达式验证输入内容是否符合规定
	 * 
	 * @return
	 */
	public boolean match() {

		// 验证用户输入框
		String str1 = tf.getText();
		Pattern p = Pattern.compile("\\w{6,20}");
		Matcher m1 = p.matcher(str1);

		// 验证密码框
		char[] c = pf.getPassword();
		String str2 = String.valueOf(c);
		Matcher m2 = p.matcher(str2);

		return m1.matches() && m2.matches();
	}

	/**
	 * 登录按钮响应
	 * 
	 * @author HaiSong.Zhang
	 *
	 */
	private class Btn1Listener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if (match()) { // 判断输入结果是否正确
				db.selectDatabase(tf.getText());
				if (db.result) { // 如果数据库里面有这个用户
					// 登入界面
					setVisible(false);
					dispose(); // 本窗口销毁s
					new ClientFrame(tf.getText());
				} else
					JOptionPane.showMessageDialog(null, "用户未注册", "操作有误", JOptionPane.ERROR_MESSAGE);
			} else
				JOptionPane.showMessageDialog(null, "用户和密码应为6~12位的字母数字、下划线组合", "格式有误", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * 注册按钮响应
	 * 
	 * @author HaiSong.Zhang
	 *
	 */
	private class Btn2Listener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if (match()) { // 判断输入结果是否正确
				db.selectDatabase(tf.getText());
				if (db.result)
					JOptionPane.showMessageDialog(null, "用户已注册", "操作有误", JOptionPane.ERROR_MESSAGE);
				else {
					db.insertDatabase(tf.getText(), String.valueOf(pf.getPassword()));
					tf.setText("");
					pf.setText("");
					JOptionPane.showMessageDialog(null, "恭喜！注册成功！");
				}
			} else
				JOptionPane.showMessageDialog(null, "格式有误", "用户和密码应为6~12位的字母数字、下划线组合", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * 单元测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new IndexFrame().IFrame();
	}
}
