package is.entity;

import java.util.ArrayList;

public class Utility {

	public static <T> void printList(ArrayList<T> list){
		for(T e: list) {
			System.out.println(e);
		}
		System.out.println("\n");
	}
}
