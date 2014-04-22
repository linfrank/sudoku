package com.soypig.sudoku;
import java.util.Arrays;

/*
 * Frank Lin
 * 
 */

public class SudokuCandidateBoard{

	private int sizeFactor;
	private int size;
	private int[][][] candidates;

	private int numMarkers;
	private int[][][] markers;

	public SudokuCandidateBoard(int sizeFactor,int numMarkers){
		//fill candidate table
		this.sizeFactor=sizeFactor;
		size=sizeFactor*sizeFactor;
		candidates=new int[size][size][size+1];
		for(int i=0;i<candidates.length;i++){
			for(int j=0;j<candidates[i].length;j++){
				Arrays.fill(candidates[i][j],1);
				candidates[i][j][0]=sizeFactor;
			}
		}
		//fill marker table
		this.numMarkers=numMarkers;
		markers=new int[size][size][numMarkers];
		fill(markers,0);
	}

	public SudokuCandidateBoard(SudokuBoard board,int numMarkers){
		this(board.getSizeFactor(),numMarkers);
		for(int i=0;i<candidates.length;i++){
			for(int j=0;j<candidates[i].length;j++){
				if(board.get(i,j)>0){
					removeAllExcept(i,j,board.get(i,j));
				}
			}
		}
	}

	public int getSizeFactor(){
		return sizeFactor;
	}

	public int getSize(){
		return size;
	}

	public int getNumMarkers(){
		return numMarkers;
	}

	public boolean removeCandidate(int x,int y,int number){
		if(candidates[x][y][number]==1){
			candidates[x][y][number]=0;
			candidates[x][y][0]--;
			return true;
		}
		else{
			return false;
		}
	}

	public boolean removeCandidates(int x,int y,int[] list){
		boolean removed=false;
		for(int i=0;i<list.length;i++){
			if(removeCandidate(x,y,list[i])){
				removed=true;
			}
		}
		return removed;
	}

	public boolean removeAllExcept(int x,int y,int number){
		if(candidates[x][y][0]>1){
			Arrays.fill(candidates[x][y],0);
			candidates[x][y][number]=1;
			candidates[x][y][0]=1;
			return true;
		}
		else{
			return false;
		}
	}

	public int numCandidates(int x,int y){
		return candidates[x][y][0];
	}

	public boolean isACandidate(int x,int y,int number){
		return candidates[x][y][0]==1;
	}

	public int[] getCandidateList(int x,int y){
		int[] list=new int[candidates[x][y][0]];
		int index=0;
		for(int i=1;i<candidates[x][y].length;i++){
			if(candidates[x][y][i]==1){
				list[index]=i;
				index++;
			}
		}
		return list;
	}

	public int getFirstCandidate(int x,int y){
		for(int i=1;i<candidates[x][y].length;i++){
			if(candidates[x][y][i]==1){
				return i;
			}
		}
		return 0;
	}

	public boolean isUniqueCandidateInRow(int number,int row){
		int count=0;
		for(int i=0;i<size;i++){
			if(candidates[row][i][number]==1){
				count++;
				if(count>1){
					return false;
				}
			}
		}
		return true;
	}

	public boolean isUniqueCandidateInColumn(int number,int column){
		int count=0;
		for(int i=0;i<size;i++){
			if(candidates[i][column][number]==1){
				count++;
				if(count>1){
					return false;
				}
			}
		}
		return true;
	}

	public boolean isUniqueCandidateInSquare(int number,int startRow,int startColumn){
		int count=0;
		for(int i=startRow;i<startRow+sizeFactor;i++){
			for(int j=startColumn;j<startColumn+sizeFactor;j++){
				if(candidates[i][j][number]==1){
					count++;
					if(count>1){
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean isSolved(){
		for(int i=0;i<candidates.length;i++){
			for(int j=0;j<candidates[i].length;j++){
				if(candidates[i][j][0]>1){
					return false;
				}
			}
		}
		return true;
	}

	public void setMark(int x,int y,int markID,int mark){
		markers[x][y][markID]=mark;
	}

	public int getMark(int x,int y,int markID){
		return markers[x][y][markID];
	}

	public SudokuCandidateBoard makeCopy(){
		SudokuCandidateBoard copy=new SudokuCandidateBoard(sizeFactor,numMarkers);
		copy(copy.candidates,candidates);
		copy(copy.markers,markers);
		return copy;
	}

	private static void fill(int[][][] array,int value){
		for(int i=0;i<array.length;i++){
			for(int j=0;j<array[i].length;j++){
				for(int k=0;k<array[i][j].length;k++){
					array[i][j][k]=value;
				}
			}
		}
	}

	private static void copy(int[][][] dest,int[][][] source){
		for(int i=0;i<dest.length;i++){
			for(int j=0;j<dest[i].length;j++){
				for(int k=0;k<dest[i][j].length;k++){
					dest[i][j][k]=source[i][j][k];
				}
			}
		}
	}

}
