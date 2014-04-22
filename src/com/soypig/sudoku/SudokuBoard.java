package com.soypig.sudoku;
/*
 * Frank Lin
 * 
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class SudokuBoard{

	public final static int DEFAULT_SIZE_FACTOR=3;

	public final static int SOLVED=1;
	public final static int OK=0;
	public final static int BAD=-1;

	private int sizeFactor;
	private int size;

	private int[][] board;

	private int[] squareHead;
	private int[] squareTail;
	private int maxDigits;

	public SudokuBoard(int sizeFactor,int[][] board){
		this.sizeFactor=sizeFactor;
		this.board=board;
		size=sizeFactor*sizeFactor;
		squareHead=new int[size];
		squareTail=new int[size];
		for(int i=0;i<size;i++){
			squareHead[i]=i/sizeFactor*sizeFactor;
			squareTail[i]=i/sizeFactor*sizeFactor+sizeFactor;
		}
		maxDigits=String.valueOf(size).length();
	}

	public SudokuBoard(int sizeFactor){
		this(sizeFactor,new int[sizeFactor*sizeFactor][sizeFactor*sizeFactor]);
	}

	public SudokuBoard(){
		this(DEFAULT_SIZE_FACTOR);
	}

	public void set(int row,int column,int number){
		board[row][column]=number;
	}

	public int get(int row,int column){
		return board[row][column];
	}

	public int getSizeFactor(){
		return sizeFactor;
	}

	public int getSize(){
		return size;
	}

	public int getSquareHead(int x){
		return squareHead[x];
	}

	public int getSquareTail(int x){
		return squareTail[x];
	}

	public int getStatus(){
		boolean hasBlank=false;
		boolean[] checker=new boolean[size+1];
		//check rows
		for(int i=0;i<size;i++){
			Arrays.fill(checker,false);
			for(int j=0;j<size;j++){
				if(board[i][j]==0){
					hasBlank=true;
				}
				else{
					if(checker[board[i][j]]){
						return BAD;
					}
				}
				checker[board[i][j]]=true;
			}
		}
		//check columns
		for(int i=0;i<size;i++){
			Arrays.fill(checker,false);
			for(int j=0;j<size;j++){
				if(board[j][i]!=0&&checker[board[j][i]]){
					return BAD;
				}
				checker[board[j][i]]=true;
			}
		}
		//check squares
		for(int i=0;i<size;i+=sizeFactor){
			for(int j=0;j<size;j+=sizeFactor){
				Arrays.fill(checker,false);
				for(int k=0;k<sizeFactor;k++){
					for(int l=0;l<sizeFactor;l++){
						if(board[i+k][j+l]!=0&&checker[board[i+k][j+l]]){
							return BAD;
						}
						checker[board[i+k][j+l]]=true;
					}
				}
			}
		}
		if(hasBlank){
			return OK;
		}
		else{
			return SOLVED;
		}
	}

	public SudokuBoard makeCopy(){
		SudokuBoard copy=new SudokuBoard(sizeFactor);
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				copy.board[i][j]=board[i][j];
			}
		}
		return copy;
	}

	public static SudokuBoard loadFile(String fileName){
		try{
			BufferedReader reader=new BufferedReader(new FileReader(fileName));
			int size=0;
			int[][] board=null;
			int lineNum=0;
			for(String nextLine;(nextLine=reader.readLine())!=null;lineNum++){
				String[] numbers=nextLine.split("\\s+");
				if(size==0){
					size=numbers.length;
					board=new int[size][size];
				}
				for(int i=0;i<size;i++){
					if(numbers[i].equals("_")){
						numbers[i]="0";
					}
					board[lineNum][i]=Integer.parseInt(numbers[i]);
				}
			}
			reader.close();
			return new SudokuBoard((int)Math.sqrt(size),board);
		}
		catch(Exception e){
			System.err.println("Error reading file: "+fileName);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString(){
		String s="";
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				int digits=String.valueOf(board[i][j]).length();
				s+=" ";
				for(int k=0;k<maxDigits-digits;k++){
					s+=" ";
				}
				if(board[i][j]!=0){
					s+=board[i][j];
				}
				else{
					s+=" ";
				}
			}
			s+="\n";
		}
		return s;
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof SudokuBoard){
			SudokuBoard other=(SudokuBoard)o;
			for(int i=0;i<size;i++){
				for(int j=0;j<size;j++){
					if(other.board[i][j]!=board[i][j]){
						return false;
					}
				}
			}
			return true;
		}
		else{
			return false;
		}
	}

}