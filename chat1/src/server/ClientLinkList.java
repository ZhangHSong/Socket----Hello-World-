package server;

/**
 * 链表
 * 
 * @author HaiSong.Zhang
 *
 */
public class ClientLinkList {

	private Node root;
	private Node pointer;
	/** 链表长度 */
	private int clientCount = 0;

	/** 构造方法 */
	public ClientLinkList() {
		root = new Node();
		root.next = null;
		pointer = null;
		clientCount = 0;
	}

	/**
	 * 尾插法
	 * 
	 * @param newNode
	 *            需要添加的节点
	 */
	public void addNodeToTail(Node newNode) {

		pointer = root;
		while (pointer.next != null) {
			pointer = pointer.next;
		}
		pointer.next = newNode;
		newNode.next = null;
		clientCount++;
	}

	/**
	 * 根据用户名查找节点
	 * 
	 * @param name
	 *            用户名
	 * @return 查找到的节点
	 */
	public Node findNodeByName(String name) {
		if (clientCount == 0)
			return null;
		pointer = root;
		while (pointer.next != null) {
			pointer = pointer.next;
			if (pointer.getUsername().equalsIgnoreCase(name)) {
				return pointer;
			}
		}
		return null;
	}

	/**
	 * 根据索引查询节点
	 * 
	 * @param index
	 * @return
	 */
	public Node findNodeByIndex(int index) {
		if (index < 0 || clientCount == 0) {
			return null;
		}
		pointer = root;
		int k = 0;
		while (k <= index) {
			if (pointer.next == null) {
				return null;
			} else {
				pointer = pointer.next;
			}
			k++;
		}
		return pointer;
	}

	/**
	 * 删除节点
	 * 
	 * @param name
	 */
	public void delNode(String name) {

		Node node = findNodeByName(name);
		pointer = root;
		while (pointer.next != null) {
			if (pointer.next == node) {
				pointer.next = node.next;
				clientCount--;
				break;
			}
			pointer = pointer.next;
		}
	}

	// 返回在线客户端数量
	public int getClientCount() {
		return clientCount;

	}

	public String[] toArray(int i) {
		String[] str = { findNodeByIndex(i).username };
		return str;
	}

}
