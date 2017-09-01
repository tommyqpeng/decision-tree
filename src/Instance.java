import java.util.ArrayList;
import java.util.Objects;

//Creates an case that has been inputted by the data

public class Instance {
	
	//an arraylist of all attributes - as strings - in an instance
	private ArrayList<String> att_list;
	
	private String outcome;

	//Constructor
	public Instance(ArrayList<String> list, String out){
		this.att_list = list;
		this.outcome = out;
	}
	
	//adds an attribute to the instance
	public void add_att(String new_att) {
		att_list.add(new_att);
	}
	
	public void add_outcome(String out) {
		this.outcome = out;
	}
	
	public ArrayList<String> get_list(){
		return att_list;
	}
	
	public String get_outcome() {
		return this.outcome;
	}
	
	//sets the list of attributes
	public void set_list(ArrayList<String> new_list) {
		this.att_list = new_list;
	}
	
	public int getNumbAtt (){
		return this.att_list.size();
	}
	
}
