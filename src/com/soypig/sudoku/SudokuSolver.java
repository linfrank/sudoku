package com.soypig.sudoku;
/*
 * Frank Lin
 * 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SudokuSolver{

	private int nodes;
	private boolean logging;

	public SudokuSolver(boolean logging){
		nodes=0;
		this.logging=logging;
	}

	public int getNodes(){
		return nodes;
	}

	public SudokuBoard solve(SudokuBoard board){
		boolean[][][] table=makeCandidateTable(board);
		nodes++;
		return easyRecurse(board,table,0);
	}

	private SudokuBoard easyRecurse(SudokuBoard board,boolean[][][] table,int level){
		log("== Processing ==");
		log("Nodes: "+nodes);
		log("Search Level: "+level);
		log("Board:");
		log(board);
		boolean updated;
		do{
			updated=false;
			boolean directUpdated;
			if(directUpdated=directDeduction(board,table)){
				updateBoard(board,table);
			}
			boolean indirectUpdated;
			if(indirectUpdated=indirectDeduction(board,table)){
				updateBoard(board,table);
			}
			updated=directUpdated||indirectUpdated;
		}while(updated);
		log("Deductive Fill Result:");
		log(board);
		int status=board.getStatus();
		if(status==SudokuBoard.SOLVED){
			return board;
		}
		if(status==SudokuBoard.BAD){
			return null;
		}
		List<SudokuFork> queue=new ArrayList<SudokuFork>();
		for(int i=0;i<table.length;i++){
			for(int j=0;j<table[i].length;j++){
				int[] list=getCandidateList(table[i][j]);
				if(list.length>1){
					queue.add(new SudokuFork(i,j,list));
				}
			}
		}
		Collections.sort(queue);
		while(!queue.isEmpty()){
			SudokuFork fork=(SudokuFork)queue.remove(0);
			log("Picking "+fork+" leaving "+queue.size()+" forks");
			int[] list=fork.getCandidateList();
			for(int i=0;i<list.length;i++){
				SudokuBoard boardCopy=board.makeCopy();
				boardCopy.set(fork.getRow(),fork.getColumn(),list[i]);
				boolean[][][] tableCopy=copyTable(table);
				Arrays.fill(tableCopy[fork.getRow()][fork.getColumn()],false);
				tableCopy[fork.getRow()][fork.getColumn()][list[i]]=true;
				if(boardCopy.getStatus()!=SudokuBoard.BAD){
					log("Going down Branch: R"+fork.getRow()+"C"+fork.getColumn()+"="+list[i]+"\n");
					nodes++;
					SudokuBoard solution=easyRecurse(boardCopy,tableCopy,level+1);
					if(solution!=null){
						return solution;
					}
					else{
						log("Bad Branch: R"+fork.getRow()+"C"+fork.getColumn()+"="+list[i]+"\n");
					}
				}
				else{
					log("Early Branch Pruning: R"+fork.getRow()+"C"+fork.getColumn()+"="+list[i]+"\n");
				}
			}
			return null;
		}
		return null;
	}

	private boolean[][][] makeCandidateTable(SudokuBoard board){
		boolean[][][] table=new boolean[board.getSize()][board.getSize()][board.getSize()+1];
		for(int i=0;i<board.getSize();i++){
			for(int j=0;j<board.getSize();j++){
				if(board.get(i,j)==0){
					Arrays.fill(table[i][j],true);
					table[i][j][0]=false;
				}
				else{
					table[i][j][board.get(i,j)]=true;
				}
			}
		}
		return table;
	}

	private boolean[][][] copyTable(boolean[][][] table){
		boolean[][][] copy=new boolean[table.length][][];
		for(int i=0;i<table.length;i++){
			copy[i]=new boolean[table[i].length][];
			for(int j=0;j<table[i].length;j++){
				copy[i][j]=new boolean[table[i][j].length];
				for(int k=0;k<table[i][j].length;k++){
					copy[i][j][k]=table[i][j][k];
				}
			}
		}
		return copy;
	}

	private void updateBoard(SudokuBoard board,boolean[][][] table){
		for(int i=0;i<table.length;i++){
			for(int j=0;j<table[i].length;j++){
				if(board.get(i,j)==0){
					int[] list=getCandidateList(table[i][j]);
					if(list.length==1){
						board.set(i,j,list[0]);
					}
				}
			}
		}
	}

	private boolean indirectDeduction(SudokuBoard board,boolean[][][] table){
		boolean updated=false;
		for(int i=0;i<table.length;i++){
			for(int j=0;j<table[i].length;j++){
				if(board.get(i,j)==0){
					for(int k=1;k<table[i][j].length;k++){
						if(table[i][j][k]){
							if(
									isUniqueNumberInSquare(k,board.getSquareHead(i),board.getSquareHead(j),table)||
									isUniqueNumberInRow(k,i,table)||
									isUniqueNumberInColumn(k,j,table)){
								Arrays.fill(table[i][j],false);
								table[i][j][k]=true;
								updated=true;
							}
						}
					}
				}
			}
		}
		return updated;
	}

	private boolean isUniqueNumberInRow(int number,int row,boolean[][][] table){
		int count=0;
		for(int i=0;i<table.length;i++){
			if(table[row][i][number]){
				count++;
				if(count>1){
					return false;
				}
			}
		}
		return true;
	}

	private boolean isUniqueNumberInColumn(int number,int column,boolean[][][] table){
		int count=0;
		for(int i=0;i<table.length;i++){
			if(table[i][column][number]){
				count++;
				if(count>1){
					return false;
				}
			}
		}
		return true;
	}

	private boolean isUniqueNumberInSquare(int number,int startRow,int startColumn,boolean[][][] table){
		int count=0;
		int squareSize=(int)Math.sqrt(table.length);
		for(int i=startRow;i<startRow+squareSize;i++){
			for(int j=startColumn;j<startColumn+squareSize;j++){
				if(table[i][j][number]){
					count++;
					if(count>1){
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean directDeduction(SudokuBoard board,boolean[][][] table){
		boolean updated=false;
		for(int i=0;i<table.length;i++){
			for(int j=0;j<table[i].length;j++){
				if(board.get(i,j)==0){
					//check row
					for(int k=0;k<board.getSize();k++){
						if(table[i][j][board.get(i,k)]){
							table[i][j][board.get(i,k)]=false;
							updated=true;
						}
					}
					//check column
					for(int k=0;k<board.getSize();k++){
						if(table[i][j][board.get(k,j)]){
							table[i][j][board.get(k,j)]=false;
							updated=true;
						}
					}
					//check square
					for(int k=board.getSquareHead(i);k<board.getSquareTail(i);k++){
						for(int l=board.getSquareHead(j);l<board.getSquareTail(j);l++){
							if(table[i][j][board.get(k,l)]){
								table[i][j][board.get(k,l)]=false;
								updated=true;
							}
						}
					}
				}
			}
		}
		return updated;
	}

	private int[] getCandidateList(boolean[] candidates){
		int count=0;
		for(int i=0;i<candidates.length;i++){
			if(candidates[i]){
				count++;
			}
		}
		int[] list=new int[count];
		int listIndex=0;
		for(int i=0;i<candidates.length;i++){
			if(candidates[i]){
				list[listIndex]=i;
				listIndex++;
			}
		}
		return list;
	}

	private void log(Object o){
		if(logging){
			System.out.println(o);
		}
	}

	public static void main(String[] args){
		
		if(args.length<1){
			System.err.println("Usage: <SDK file>");
			return;
		}
		
		SudokuBoard problem=SudokuBoard.loadFile(args[0]);

		System.out.println("Problem:");
		System.out.println(problem);

		SudokuSolver s=new SudokuSolver(false);

		long start=System.currentTimeMillis();

		SudokuBoard solution=s.solve(problem);

		long end=System.currentTimeMillis();

		System.out.println("Solution:");
		System.out.println(solution);
		System.out.println("Search Nodes: "+s.getNodes());
		System.out.println("Run Time: "+(end-start)+" ms");

	}

}
