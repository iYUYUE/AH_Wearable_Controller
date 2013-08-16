package edu.mit.media.hlt.sensorgraph;

import java.util.ArrayDeque;
import java.util.Deque;

public class vote {
	Deque<Integer> resultTank;
	int result;
	int voteBoard[];

	public vote() {
		resultTank = new ArrayDeque<Integer>();
		result = 5;
		voteBoard = new int[5];
	}
	
	
	public int sendToJudge(int resultOnce) {
		resultTank.addLast(resultOnce);
		
		while(resultTank.size() > 6) {
			resultTank.removeFirst();
		}
		
		if(resultTank.size() == 5) result = judge();
		else result = 5;
		return result;
	}
	
	private int judge() {
		int max = 0;
		for(int i : resultTank) voteBoard[i]++;
		for(int i : voteBoard) if(i > max) max = i;
		return max;
	}
}
