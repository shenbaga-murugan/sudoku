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
 * <b>This class to represent a Sudoku Cell.</b><br/>
 * This contains the value of the cell(zero when not known),
 * and a list of possible numbers for that cell.
 */
public class SudokuCell {
	/**
	 * The value of the cell. This may be
	 * guess or finalized value.
	 */
	private int value = 0;
	/**
	 * List of possible values for that cell.
	 * If the list has only one element, that should
	 * be the value of the cell. If the list is empty,
	 * there is something wrong with the guess or the grid
	 * itself!
	 */
	private ArrayList<Integer> possibles = null;
	
	/**
	 * @deprecated
	 * Default constructor
	 */
	public SudokuCell() {
		//default constructor
	}
	
	/**
	 * Constructs a cell with possible numbers list
	 * @param order - number of cells in a block
	 * width * height
	 */
	public SudokuCell(int order) {
		this.possibles = new ArrayList<Integer>();
		for(int i = 1; i <= order; i++) {
			this.possibles.add(i);
		}
	}
	
	/**
	 * Constructs a cell with possible numbers 
	 * list and value
	 * @param value - value of the cell
	 * @param order -  - number of cells in a block
	 * width * height
	 */
	public SudokuCell(int value, int order) {
		this(order);
		this.value = value;
	}
	
	/**
	 * This method is used to clone the cell object
	 * @param cell - cell to be cloned
	 */
	public SudokuCell(SudokuCell cell) {
		ArrayList<Integer> poss = new ArrayList<Integer>();
		ArrayList<Integer> cellPoss = cell.getPossibles();
		for(int i : cellPoss) {
			poss.add(new Integer(i));
		}
		this.possibles = poss;
		this.value = cell.getValue();
	}
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	/**
	 * @return the possibles
	 */
	public ArrayList<Integer> getPossibles() {
		return possibles;
	}
	/**
	 * @param possibles the possibles to set
	 */
	public void setPossibles(ArrayList<Integer> possibles) {
		this.possibles = possibles;
	}
	
	/**
	 * This method removes the number from the possible
	 * numbers list
	 * @param num - number to remove
	 */
	public void removePossible(int num) {
		this.possibles.remove(new Integer(num));
	}
}
