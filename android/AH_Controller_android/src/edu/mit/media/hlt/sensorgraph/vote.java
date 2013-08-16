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
		
		while(resultTank.size() > 6) {
			resultTank.removeFirst();
		}
		
		if(resultTank.size() == 5) result = judge();
		else result = 5;
		return result;
	}
	
	private static int judge() {
		int max = 0;
		for(int i : resultTank) {
			if(i < 5)	voteBoard[i - 1] += 2;
			else voteBoard[i - 1]++;
		for(int i : voteBoard) if(i > max) max = i;
		return max;
	}
}
