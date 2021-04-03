package com.lupcode.Utilities.maps;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Tree structure that can be used to represent a file system.
 * Tree is therefore not balanced. Allows concurrent accesses
 * @author LupCode.com (Luca Vogels)
 * @since 2021-02-12
 */
public class StructuredTree<E> {

	
	public abstract class Component {
		protected Node parent;
		protected String name;
		
		/**
		 * Returns if this object is a node
		 * @return True if node false otherwise
		 */
		public boolean isNode() {
			return this instanceof StructuredTree.Node;
		}
		
		/**
		 * Returns if this object is a leaf
		 * @return True if leaf false otherwise
		 */
		public boolean isLeaf() {
			return this instanceof StructuredTree.Leaf;
		}
		
		/**
		 * Returns the parent node this component is inside in
		 * @return Parent node or null if this component is root
		 */
		public Node getParent() {
			return parent;
		}
		
		/**
		 * Returns the name of the component
		 * @return Name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Returns the full name of the component including parents name
		 * @return Full name
		 */
		public String getFullName() {
			return (parent!=null ? parent.getFullName() : "/")+name+(isNode() ? "/" : "");
		}
	}
	
	public class Node extends Component {
		protected HashMap<String, Node> children = new HashMap<>();
		
		// TODO
	}
	
	public class Leaf extends Component {
		protected E e;
		
		// TODO
	}
	
	
	protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	protected char separator;
	
	public StructuredTree() {
		this('/');
	}
	public StructuredTree(char separator) {
		this.separator = separator;
	}
	
	
	
	public void toStream(OutputStream output) {
		// TODO
	}
	
	public void fromStream(InputStream input) {
		// TODO
	}
}
