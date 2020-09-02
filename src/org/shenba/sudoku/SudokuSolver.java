/* This file is part of "Psuedoku".
 * "Psuedoku" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Psuedoku" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. <http://www.gnu.org/licenses/>.
 */
package org.shenba.sudoku;

import java.util.ArrayList;

/**
 * @author Shenbaga Murugan
 * <br/>
 * <b>This class acts as the main thread for Sudoku Solver</b><br>
 * This class can be used to solve any size of Sudoku grid.
 * This won't work with wrong inputs.
 * If you want to make it perfect, you are welcome!!!
 */
public class SudokuSolver {
	/**
	 * To save the grids in previous state while guessing
	 */
	private static ArrayList<ArrayList<SudokuCell>> savedGrids = new ArrayList<ArrayList<SudokuCell>>();
	/**
	 * To save the index of the last modified cell
	 */
	private static ArrayList<Integer> savedIndices = new ArrayList<Integer>();

	/* These variables are here because currently
	 * this is the way to give inputs. This can be
	 * safely replaced by any means of better input!
	 */
	/**
	 * width of the block
	 */
	private static int width = 3;
	/**
	 * height of the block
	 */
	private static int height = 3;
	/**
	 * input gird - This is a space separated numbers as string.
	 * Make unknown cells as zero. Be extra careful during input!
	 */
	private static String input = "8 0 0 0 0 0 0 0 0 0 0 3 6 0 0 0 0 0 0 7 0 0 9 0 2 0 0 0 5 0 0 0 7 0 0 0 0 0 0 0 4 5 7 0 0 0 0 0 1 0 0 0 3 0 0 0 1 0 0 0 0 6 8 0 0 8 5 0 0 0 1 0 0 9 0 0 0 0 4 0 0";
	/**
	 * order is the number of cells in a block
	 */
	private static int order = width * height;
	/**
	 * total cells in the grid
	 */
	private static int cellCount = order * order;

	/**
	 * Main method to invoke to solve your grid
	 * When untouched, you need to feed the
	 * "input" variable along with order, width, height variables
	 * @param args - nothing at this moment
	 */
	public static void main(String[] args) {
		long startTime = 0;
		long endTime = 0;
		ArrayList<SudokuCell> grid = new ArrayList<SudokuCell>();
		ArrayList<SudokuCell> prevGrid = new ArrayList<SudokuCell>();
		int value = 0;
		String[] arr = input.split(" ");
		
		for(int i = 0; i < cellCount; i++) {
			try {
				value = Integer.parseInt(arr[i]);
			} catch (Exception e) {
				value = 0;
			}
			grid.add(new SudokuCell(value, order));
		}
		
		startTime = System.currentTimeMillis();
		while(!solved(grid)) {
			prevGrid = cloneGrid(grid);
			for(int i = 0; i < cellCount; i++) {
				checkRow(i, grid);
				checkCol(i, grid);
				checkBlock(i, grid);
				checkCell(i, grid);
			}
			if(incorrect(grid)) {
				grid = rollback();
			}
			if(compareGrids(prevGrid, grid)) {
				guess(grid);
			}
		}
		endTime = System.currentTimeMillis();
		
		//to show the output in console -bad right? Change it!!
		System.out.println("Solved in " + (endTime - startTime) + " milliseconds.");
		System.out.println("Final result");
		printGrid(grid);
	}

	/**
	 * Compares two grids<br/>
	 * This method is used to check if any value in the gird is changed
	 * after an iteration. If no value is changed, it means, there is
	 * no more number to find without guessing.
	 * @param prevGrid - Grid that was before the checking
	 * @param grid - Grids after checking
	 * @return - True if both grids have same numbers, else False
	 */
	public static boolean compareGrids(ArrayList<SudokuCell> prevGrid,
		ArrayList<SudokuCell> grid) {
		for(int i = 0; i < cellCount; i++) {
			if(prevGrid.get(i).getValue() != grid.get(i).getValue()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks the row of a cell<br/>
	 * This method checks the numbers in the cell's row and removes
	 * all the numbers already in the row from the possible numbers list.
	 * @param index - index of the cell in the grid (array list)
	 * @param sudokuGrid - ArrayList of SudokuCells
	 */
	public static void checkRow(int index, ArrayList<SudokuCell> sudokuGrid) {
		SudokuCell currentCell = sudokuGrid.get(index);
		int beginInd = (index / order) * order;
		int endInd = beginInd + order;
		for(int i = beginInd; i < endInd; i++) {
			if(i != index) {
				currentCell.removePossible(sudokuGrid.get(i).getValue());
			}
		}
	}

	/**
	 * Checks the column of a cell<br/>
	 * This method checks the numbers in the cell's column and removes
	 * all the numbers already in the row from the possible numbers list.
	 * @param index - index of the cell in the grid (array list)
	 * @param sudokuGrid - ArrayList of SudokuCells
	 */
	public static void checkCol(int index, ArrayList<SudokuCell> sudokuGrid) {
		SudokuCell currentCell = sudokuGrid.get(index);
		int beginInd = index % order;
		int endInd = beginInd + (order * order);
		for(int i = beginInd; i < endInd; i += order) {
			if(i != index) {
				currentCell.removePossible(sudokuGrid.get(i).getValue());
			}
		}
	}

	/**
	 * Checks the block of a cell<br/>
	 * This method checks the numbers in the cell's block and removes
	 * all the numbers already in the row from the possible numbers list.
	 * @param index - index of the cell in the grid (array list)
	 * @param sudokuGrid - ArrayList of SudokuCells
	 */
	public static void checkBlock(int index, ArrayList<SudokuCell> sudokuGrid) {
		SudokuCell currentCell = sudokuGrid.get(index);

		/* I don't know if this is needed
		 * As I kept the Grid as list, came up with
		 * this complex logic. Can be simplified. May not be!
		 */
		int rowBegin =  (index / order) * order;
		int colBegin = index % order;
		int blockNo = (colBegin / width) + (((rowBegin / order) / height) * height);
		int corner = ((blockNo % order) * width) + ((blockNo / height) * (order * (height - 1)));

		for(int i = 0; i < (height * order); i += order){
			for(int j = (corner + i); j < (corner + i + width); j++) {
				if(j != index) {
					currentCell.removePossible(sudokuGrid.get(j).getValue());
				}
			}
		}
	}

	/**
	 * Checks current cell for finalized value.
	 * If only one possible number is left after checking all cells in the
	 * row, the number is finalized as the value of the cell.
	 * @param index - index of the cell
	 * @param grid - ArrayList of SudokuCells
	 */
	public static void checkCell(int index, ArrayList<SudokuCell> grid) {
		SudokuCell currentCell = grid.get(index);
		if(currentCell.getPossibles().size() == 1) {
			currentCell.setValue(currentCell.getPossibles().get(0));
		}
	}

	/**
	 * To check if the grid is solved
	 * @param sudokuGrid - ArrayList of the SudokuCells
	 * @return True when all the cells are having value other than zero
	 * else false
	 */
	public static boolean solved(ArrayList<SudokuCell> sudokuGrid) {
		for(SudokuCell cell : sudokuGrid) {
			if(cell.getValue() == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * To check if the grid is valid
	 * @param grid - ArrayList of SudokuCells
	 * @return - true if any of the cell has no possible value
	 * else false
	 */
	public static boolean incorrect(ArrayList<SudokuCell> grid) {
		for(SudokuCell cell : grid) {
			if(cell.getPossibles().size() < 1) {
				return true;
			}
		}
		return false;
	}

		/**
	 * To guess the possible value for a cell<br/>
	 * This method sets the value of the cell to the first
	 * possible number in the possible numbers list.
	 * After that, it saves the grid to savedGrid variable for
	 * rollback if needed.
	 * @param - grid
	 */
	public static void guess(ArrayList<SudokuCell> grid) {
		int guessIndex = bestGuess(grid);
		SudokuCell cell = grid.get(guessIndex);
		savedIndices.add(guessIndex);
		cell.setValue(cell.getPossibles().get(0));
		savedGrids.add(cloneGrid(grid));
	}

	/**
	 * To find the cell with vacant least possible numbers.<br/>
	 * Instead of guessing the first vacant cell, this method
	 * gives the best cell to guess
	 * @param grid - ArrayList of SudokuCells
	 * @return - index of the vacant cell with least
	 * possible numbers
	 */
	public static int bestGuess(ArrayList<SudokuCell> grid) {
		int i = 0;
		int index = 0;
		int possibles = order;
		for (SudokuCell cell: grid) {
			if(cell.getValue() == 0 && possibles > cell.getPossibles().size()) {
				possibles = cell.getPossibles().size();
				index = i;
				if(possibles == 2) {
					break;
				}
			}
			i++;
		}
		return index;
	}

	/**
	 * To rollback a wrong guess<br/>
	 * When a guess lead to incorrect grid, this method
	 * retrieves the previous state and removes that wrong
	 * guess from that cell. If this method is called without any
	 * saved grid to rollback, that means the grid input is wrong
	 * and the system will exit after printing "No Solution".
	 * @return - Previous state of the grid with the wrong
	 * guess removed.
	 */
	public static ArrayList<SudokuCell> rollback() {
		if(savedGrids.isEmpty()) {
			System.out.println("No Solution");
			System.exit(0);
		}
		int lastInd = savedGrids.size() - 1;
		ArrayList<SudokuCell> grid = savedGrids.get(lastInd);
		int savedIndex = savedIndices.get(lastInd);

		SudokuCell cell = grid.get(savedIndex);
		cell.removePossible(cell.getValue());
		cell.setValue(0);

		savedGrids.remove(lastInd);
		savedIndices.remove(new Integer(savedIndex));

		return grid;
	}

	/**
	 * To deep clone the grid (ArrayList of SudokuCells)
	 * @param grid - ArrayList of SudokuCells
	 * @return - a deep clone of the grid
	 */
	public static ArrayList<SudokuCell> cloneGrid(ArrayList<SudokuCell> grid) {
		ArrayList<SudokuCell> clone = new ArrayList<SudokuCell>();
		for(SudokuCell cell : grid) {
			clone.add(new SudokuCell(cell));
		}
		return clone;
	}

	/**
	 * To print the grid in console<br/>
	 * This method prints the grid in console.
	 * Every block is separated by two line breaks at bottom
	 * and two white spaces at sides
	 * @param grid - ArrayList of SudokuCells
	 */
	public static void printGrid(ArrayList<SudokuCell> grid) {
		int i = 0;
		String formatter = "%0" + String.valueOf(order).length() + "d";
		for(SudokuCell cell : grid) {
			System.out.print(String.format(formatter, cell.getValue()));
			System.out.print(" ");
			i++;
			if(i % width == 0) {
				System.out.print(" ");
			}
			if(i % order == 0) {
				System.out.println();
			}
			if(i % (height * order) == 0) {
				System.out.println();
			}
		}
	}
}

