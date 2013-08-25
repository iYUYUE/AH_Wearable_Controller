package edu.mit.media.hlt.sensorgraph;

import java.util.ArrayDeque;
import java.util.Deque;

public class vote {
	static Deque<Integer> resultTank;
	static int result;
	static int voteBoard[];

	public vote() {
		resultTank = new ArrayDeque<Integer>();
		result = 5;
		voteBoard = new int[5];
	}
	
	
	public static int sendToJudge(int resultOnce) {
		resultTank.addLast(resultOnce);
		
		while(resultTank.size() > 4) {
			resultTank.removeFirst();
		}
		
//		String output = "";
//		for(int i : resultTank) output = output + i + ", ";
//		System.out.println(output);
		
		if(resultTank.size() == 4) result = judge();
		else result = 5;
		return result;
	}
	
	private static int judge() {
		int max = 0;
		int maxIndex = 0;
		for(int i : resultTank) {
			if(i < 5)	voteBoard[i - 1] += 3;
			else voteBoard[i - 1]++;
		}
		for(int i = 0;i < 5;i++) {
			if(voteBoard[i] > max) {
				max = voteBoard[i];
				maxIndex = i;
			}
		}
		
		String output = "";
		for(int i : voteBoard) output = output + i + ", ";
		System.out.println(output);
		
		voteBoard = new int[5];
		return maxIndex + 1;
	}
}
