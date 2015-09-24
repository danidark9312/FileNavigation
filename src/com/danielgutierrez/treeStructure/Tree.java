package com.danielgutierrez.treeStructure;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
	Node root;
	
	public Tree(T t){
		root = new Node(t,null);
	}
	
	public Node getRoot(){
		return root;
	}
	
	class Node{
		private T t;
		private List<Node> children;
		private Node parent;
		
		public void setDate(T t){
			this.t = t;
		}
		
		public Node(T t,Node parent){
			this.t=t;
			this.parent = parent;
				
		}

		private void setParent(Node node){
			this.parent = node;
		}
		public void addChild(Node node){
			if(children == null)
				children = new ArrayList();
			children.add(node);
			node.setParent(this);
		}
	}

}
