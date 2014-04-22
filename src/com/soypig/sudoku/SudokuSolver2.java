package com.soypig.sudoku;
/*
 * Frank Lin
 * 
 * Experimental - TBD
 * 
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SudokuSolver2{

	private final static int NUM_MARKERS=1;
	private final static int DIRECT_DEDUCED_MARK=0;

	private int nodes;
	private boolean logging;

	public SudokuSolver2(boolean logging){
		nodes=0;
		this.logging=logging;
	}

	public int getNodes(){
		return nodes;
	}

	public SudokuBoard solve(SudokuBoard board){
		SudokuCandidateBoard candidates=new SudokuCandidateBoard(board,NUM_MARKERS);
		nodes++;
		return easyRecurse(board,candidates,0);
	}

	private SudokuBoard easyRecurse(SudokuBoard board,SudokuCandidateBoard candidates,int level){
		log("== Processing ==");
		log("Nodes: "+nodes);
		log("Search Level: "+level);
		log("Board:");
		log(board);
		boolean updated;
		do{
			updated=false;
			boolean directUpdated=directDeduction(board,candidates);
			if(directUpdated){
				updateBoard(board,candidates);
			}
			log(board);
			boolean indirectUpdated=indirectDeduction(board,candidates);
			if(indirectUpdated){
				updateBoard(board,candidates);
			}
			log(board);
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
		for(int i=0;i<candidates.getSize();i++){
			for(int j=0;j<candidates.getSize();j++){
				if(candidates.numCandidates(i,j)>1){
					queue.add(new SudokuFork(i,j,candidates.getCandidateList(i,j)));
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
				SudokuCandidateBoard candidatesCopy=candidates.makeCopy();
				candidatesCopy.removeAllExcept(fork.getRow(),fork.getColumn(),list[i]);
				if(boardCopy.getStatus()!=SudokuBoard.BAD){
					log("Going down Branch: R"+fork.getRow()+"C"+fork.getColumn()+"="+list[i]+"\n");
					nodes++;
					SudokuBoard solution=easyRecurse(boardCopy,candidatesCopy,level+1);
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

	private void updateBoard(SudokuBoard board,SudokuCandidateBoard candidates){
		for(int i=0;i<candidates.getSize();i++){
			for(int j=0;j<candidates.getSize();j++){
				if(board.get(i,j)==0&&candidates.numCandidates(i,j)==1){
					board.set(i,j,candidates.getFirstCandidate(i,j));
				}
			}
		}
	}

	private boolean directDeduction(SudokuBoard board,SudokuCandidateBoard candidates){
		boolean updated=false;	
		for(int i=0;i<candidates.getSize();i++){
			for(int j=0;j<candidates.getSize();j++){
				if(candidates.numCandidates(i,j)==1&&candidates.getMark(i,j,DIRECT_DEDUCED_MARK)!=1){
					//eliminate same number in row
					for(int k=0;k<candidates.getSize();k++){
						if(candidates.removeCandidate(i,k,board.get(i,j))){
							updated=true;
						}
					}
					//eliminate same number in column
					for(int k=0;k<candidates.getSize();k++){
						if(candidates.removeCandidate(k,j,board.get(i,j))){
							updated=true;
						}
					}
					//eliminate same number in square
					for(int k=board.getSquareHead(i);k<board.getSquareTail(i);k++){
						for(int l=board.getSquareHead(j);l<board.getSquareTail(j);l++){
							if(candidates.removeCandidate(k,l,board.get(i,j))){
								updated=true;
							}
						}
					}
					candidates.setMark(i,j,DIRECT_DEDUCED_MARK,1);
				}
			}
		}
		return updated;
	}

	private boolean indirectDeduction(SudokuBoard board,SudokuCandidateBoard candidates){
		boolean updated=false;
		for(int i=0;i<candidates.getSize();i++){
			for(int j=0;j<candidates.getSize();j++){
				if(candidates.numCandidates(i,j)>1){
					for(int k=1;k<candidates.getSizeFactor();k++){
						if(candidates.isACandidate(i,j,k)){
							if(
									candidates.isUniqueCandidateInSquare(k,board.getSquareHead(i),board.getSquareHead(j))||
									candidates.isUniqueCandidateInRow(k,i)||
									candidates.isUniqueCandidateInColumn(k,j)){
								candidates.removeAllExcept(i,j,k);
								updated=true;
							}
						}
					}
				}
			}
		}
		return updated;
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

		SudokuSolver2 s=new SudokuSolver2(true);

		long start=System.currentTimeMillis();

		SudokuBoard solution=s.solve(problem);

		long end=System.currentTimeMillis();

		System.out.println("Solution:");
		System.out.println(solution);
		System.out.println("Search Nodes: "+s.getNodes());
		System.out.println("Run Time: "+(end-start)+" ms");

	}

}
