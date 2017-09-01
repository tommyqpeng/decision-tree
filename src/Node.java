import java.util.ArrayList;
import java.util.Objects;

//A simple version of a node that only takes into account the decision and not the array of Instances
public class Node {

	//index of the attribute that splits at this node
	private int att_index;

	//value of the attribute
	private String left_att_value, right_att_value;

	//entropy at this node
	private double entropy;
	
	//threshold of entropy
	private double entropy_thresh;

	//the left daughter node
	protected Node left_node;

	//the right daughter node
	protected Node right_node;

	//the outcomes of all the instances that arrive at this node
	private ArrayList<String> array_outcomes;

	//the array of instances at this node
	private ArrayList<Instance> array_instances;

	//whether or not this node is a leaf
	private boolean isLeaf;

	//arraylist of possible indices to choose from
	private ArrayList<Double> possible_index;

	public Node(ArrayList<Instance> array_in, double threshold_in) {
		this.array_instances = array_in;
		this.entropy_thresh = threshold_in;
		this.att_index = -1; //so that it is not 0, which is also a valid index
		if (array_instances.size() == 0) {
			System.out.println("array_instances cannot be 0 sized");
		}
		this.array_outcomes = new ArrayList<String>();
		for (int i = 0; i < this.array_instances.size(); i++) {
			array_outcomes.add(array_instances.get(i).get_outcome());
		}
		this.entropy = calcEntropy();
		this.isLeaf = isNodeLeaf();
	}

	//calculates the entropy at the node
	public double calcEntropy(){
		double ans = 0;
		int count = 0;
		if (this.array_outcomes.size() == 0 || this.array_outcomes.isEmpty()) {
			return 0;
		}
		for (int i = 0; i < this.array_outcomes.size(); i++) {
			if (Objects.equals(this.array_outcomes.get(i), "won")) {
				count++;
			}
		}
		double p1 = ((double)count/array_outcomes.size());
		double p2 = 1-p1;
		//		System.out.println("p1: " + p1 + ",count: " + count + ", arraysize: " + array_outcomes.size());
		if (p1 == 0 || p1 == 1) {
			return 0;
		} else {
			ans = -p1*(Math.log(p1)/Math.log(2)) - p2*(Math.log(p2)/Math.log(2));
			return ans;
		}
	}

	//returns true if entropy of node is below threshold
	public boolean isNodeLeaf(){
		if (this.entropy < this.entropy_thresh) {
			return true;
		} else {
			return false;
		}
	}

	//returns the outcome of the node based on probabilistic model
	public String getOutcome() {
		int count = 0;
		if (this.array_outcomes.size() == 0 || this.array_outcomes.isEmpty()) {
			return "outcome_array size = 0";
		}
		for (int i = 0; i < this.array_outcomes.size(); i++) {
			if (Objects.equals(this.array_outcomes.get(i), "won")) {
				count++;
			}
		}
		double p1 = ((double)count/array_outcomes.size());
		double compare = Math.random();
		if (compare <= p1) {
			return "won";
		} else {
			return "nowin";
		}
	}

	//sets the left and right nodes based on index
	public void splitNode(int index){
		//if this node is not a leaf
		this.att_index = index;
		if (!this.isNodeLeaf()) {
			String compare = this.array_instances.get(0).get_list().get(index);
			ArrayList<Instance> l = new ArrayList<Instance>();
			ArrayList<Instance> r = new ArrayList<Instance>();
			for (int i = 0; i < this.array_instances.size(); i++) {
				if (Objects.equals(compare, this.array_instances.get(i).get_list().get(index))) {
					l.add(this.array_instances.get(i));
				} else {
					r.add(this.array_instances.get(i));
				}
			}
			if (l.size() > 0) {
				this.left_node = new Node(l, this.entropy_thresh);
				this.left_att_value = compare;
			}
			if (r.size() > 0) {
				this.right_node = new Node(r, this.entropy_thresh);
				this.right_att_value = r.get(0).get_list().get(index);
			}
		}
	}

	//returns the index of the attribute that was split in this node's daughters
	public int getSplitIndex() {
		return this.att_index;
	}

	//returns the instance list
	public ArrayList<Instance> getInstances(){
		return this.array_instances;
	}

	//returns the left node attribute value
	public String getLeftAttValue(){
		return this.left_att_value;
	}

	//returns the right node attribute value
	public String getRightAttValue(){
		return this.right_att_value;
	}

	//sets the index of the node
	public void setIndex(int index){
		this.att_index = index;
	}

	//generates an index to go to next using information gain
	public int genIndex() {
		int ans = -1;
		double left_weighted = 0;
		double right_weighted = 0;
		double maxInfo = 0;
		for (int i = 0; i < this.array_instances.get(0).getNumbAtt(); i++) {
			Node tempNode = new Node(this.array_instances, this.entropy_thresh);
			tempNode.splitNode(i);
			int total = tempNode.array_instances.size();
			if (tempNode.left_node != null) {
				left_weighted = tempNode.left_node.calcEntropy()*( (double) tempNode.left_node.array_instances.size()/total);
			} else {
				left_weighted = 0;
			}
			if (tempNode.right_node != null) {
				right_weighted = tempNode.right_node.calcEntropy()*( (double) tempNode.right_node.array_instances.size()/total);
			} else {
				right_weighted = 0;
			}
			double daughter_weighted_entropy = left_weighted + right_weighted;
			double infoGain = tempNode.calcEntropy() - daughter_weighted_entropy;
//			System.out.println("i: " + i + "info gain: " + infoGain);
			if (infoGain > maxInfo) {
				ans = i;
				maxInfo = infoGain;
			}
		}
		return ans;
	}

	//builds the tree
	public void buildTree(){
		this.att_index = this.genIndex();
		this.splitNode(this.att_index);
		if ((this.left_node != null) && !this.left_node.isNodeLeaf()) {
			this.left_node.buildTree();
		}
		if ((this.right_node != null) && !this.right_node.isNodeLeaf()) {
			this.right_node.buildTree();
		}
	}

	//gets the correct node at this split
	public Node getCorrectNode (Instance input) {
		if (Objects.equals(input.get_list().get(this.att_index), this.left_att_value)) {
			return this.left_node;
		} else {
			return this.right_node;
		}
	}

	//gets the correct leaf
	public Node getCorrectLeaf (Instance input) {
		if (this.getCorrectNode(input) != null) {
			if (!this.getCorrectNode(input).isNodeLeaf()) {
				return this.getCorrectNode(input).getCorrectLeaf(input);
			}
			return getCorrectNode(input);
		} else {
			return this;
		}
	}
}
