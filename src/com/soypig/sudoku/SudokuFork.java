package com.soypig.sudoku;
/*
 * Frank Lin
 * 
 */

public class SudokuFork implements Comparable<SudokuFork>{
	
	private int row;
	private int column;
	private int[] list;
	
	public SudokuFork(int row,int column,int[] list){
		this.row=row;
		this.column=column;
		this.list=list;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getColumn(){
		return column;
	}
	
	public int[] getCandidateList(){
		return list;
	}
	
	@Override
	public String toString(){
		String s="Fork R"+row+"C"+column+":";
		for(int i=0;i<list.length;i++){
			s+=" "+list[i];
		}
		return s;
	}
	
	@Override
	public int compareTo(SudokuFork other){
		return this.list.length-other.list.length;
	}

}
