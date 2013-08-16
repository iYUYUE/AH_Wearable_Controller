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
		for(int i : resultTank) voteBoard[i - 1]++;
		for(int i = 0;i < 4;i++) if(voteBoard[i] > max) max = i;
		return max;
	}
}
