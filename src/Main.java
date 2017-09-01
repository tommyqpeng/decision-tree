import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import weka.core.converters.CSVLoader;

//Main class for decision tree
//Author: Tommy Peng


public class Main {

	public static void main(String args[])
	{
		ArrayList<Instance> instance_list = new ArrayList<Instance>();
		ArrayList<Instance> testing_list = new ArrayList<Instance>();

		//filereader code is from https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
		String filename_data = "kr-vs-kp.data";
		String testfilename = "testcase.txt";
		String line = "";

		try (BufferedReader br_d = new BufferedReader(new FileReader(filename_data))) {

			while ((line = br_d.readLine()) != null) {
				// use comma as separator
				String[] attributes = line.split(",");
				Instance tempInstance = new Instance(new ArrayList<String>(Arrays.asList(attributes).subList(0, attributes.length-2)), attributes[attributes.length-1]);
				instance_list.add(tempInstance);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try (BufferedReader br_d = new BufferedReader(new FileReader(testfilename))) {

			while ((line = br_d.readLine()) != null) {
				// use comma as separator
				String[] attributes = line.split(",");
				Instance tempInstance = new Instance(new ArrayList<String>(Arrays.asList(attributes).subList(0, attributes.length-1)), "test");
				testing_list.add(tempInstance);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}


		//seperates the list into training and testing
		int total_instances = instance_list.size();
		ArrayList<Instance> training = instance_list;
		ArrayList<Instance> testing = testing_list;
		
		Node root = new Node(training, 0.1);
		root.buildTree();
		
		int correct = 0;
		int incorrect = 0;
		
		System.out.println("normal tree:");
		//tests the tree
		for (int i = 0; i < testing.size(); i++) {
			if (root.getCorrectLeaf(testing.get(i)).right_node != null) {
				System.out.println(root.getCorrectLeaf(testing.get(i)).right_node.calcEntropy());
			}
//			if (Objects.equals(root.getCorrectLeaf(testing.get(i)).getOutcome(), testing.get(i).get_outcome())) {
//				correct++;
//			}
//			else {
//				incorrect++;
//			}
			System.out.println(root.getCorrectLeaf(testing.get(i)).getOutcome());
		}
		
//		System.out.println("Normal Tree");
//		System.out.println("Correct: " + correct);
//		System.out.println("Incorrect: " + incorrect);
//		
		//Building Random Forest
		ArrayList<Node> forest = new ArrayList<Node>();
		int treeNum = 9;
		for (int i = 0; i < treeNum; i++) {
			forest.add(new Node(training, 0.1));
		}
		for (int i = 0; i < forest.size(); i++) {
			forest.get(i).buildTree();
		}
		
		int forest_correct = 0;
		int forest_incorrect = 0;
		
		System.out.println("Random forest n =" + treeNum);
		//Using random forest - majority wins
		for (int i = 0; i < testing.size(); i++) {
			int no_win_count = 0;
			int won_count = 0;
			String outcome = "";
			for (int j = 0; j < forest.size(); j++) {
				if (Objects.equals(forest.get(j).getCorrectLeaf(testing.get(i)).getOutcome(), "won")) {
					won_count++;
				} else {
					no_win_count++;
				}
			}
			
			if (won_count > no_win_count) {
//				outcome = "won";
				System.out.println("won");
			} else {
//				outcome = "nowin";
				System.out.println("nowin");
			}
			
			if (Objects.equals(testing.get(i).get_outcome(), outcome)) {
				forest_correct++;
			} else {
				forest_incorrect++;
			}
		}
		
//		System.out.println("Forest Results");
//		System.out.println("Correct: " + forest_correct);
//		System.out.println("Incorrect: " + forest_incorrect);
	}



}


