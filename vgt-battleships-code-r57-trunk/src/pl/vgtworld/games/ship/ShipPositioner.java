package pl.vgtworld.games.ship;

import java.util.ArrayList;
import java.util.Random;
import pl.vgtworld.exceptions.ParameterException;
import pl.vgtworld.exceptions.DeveloperException;
import pl.vgtworld.tools.Position;

/**
 * A aula trata da colocação aleatória de naves no tabuleiro. <br />
 *
 * <p>
 * atualizações: <br />
 * 1,1 <br />
 * - adicionado o parâmetro bStraightLines ao método {@link #shipSpaces (Ship Iterator, boolean)}. <br />
 * </p>
 * 
 * @author VGT
 * @version 1.1
 */
public class ShipPositioner
	{
	/**
	 * A container for which ships must be stowed on board.
	 */
	private ShipIterator oShips;
	/**
	* Plate belonging to a particular vessel vessel.
	*/
	private Board oBoard;
	/**
	 * Random number generator.
	 */
	private Random oRand;
	/**
	* Default constructor.
	*/
	public ShipPositioner()
		{
		oShips = null;
		oBoard = null;
		oRand = new Random();
		}
	/**
	 * @deprecated
	 */
	public boolean shipSpaces(ShipIterator oShips) throws ParameterException
		{
		return shipSpaces(oShips, false);
		}
	/**
	 * Main method for initiating the procedure for placing ships on board. <br />
	 *
	 * Successful ship placement is not guaranteed. The less space on board and/or the more ships, the better the chances
	 * an error when preparing ships. If staging is not feasible, the method returns FALSE
	 * and all partial ship positions are reset. <br />
	 *
	 * For standard game (board 10x10 and 10 ships with sizes 1 to 4)
	 * Current ship placement efficiency is at 99.93% level. <br />
	 *
	 *updates: <br />
	 *
	 * 1.1 - adding a second parameter.
	 *
	 * @param oShips Container with ships configured to be stowed on board.
	 * @param bStraightLines Defines whether ships should be vertical/horizontal lines only.
	 * @return Returns TRUE if the ships were correctly spooled, or FALSE if there is an error.
	 * @throws ParameterException It throws an exception if the container contains no ships.
	 */
	public boolean shipSpaces(ShipIterator oShips, boolean bStraightLines) throws ParameterException
		{
		if (oShips.getNumberOfShips() == 0)
			throw new ParameterException("oShips.iNumberOfShips = 0");
		try
			{
			importShips(oShips);
			this.oShips.resetFields();
			// determine the Size of the largest ship
			int iMaxSize = 0;
			for (int i = 1; i <= this.oShips.getNumberOfShips(); ++i)
				if (oShips.getShip(i).getSize() > iMaxSize)
					iMaxSize = this.oShips.getShip(i).getSize();
			// ship locations, starting with the largest
			while (iMaxSize > 0)
				{
				for (int i = 1; i <= this.oShips.getNumberOfShips(); ++i)
					if (oShips.getShip(i).getSize() == iMaxSize)
						{
						int iApproach = 1; // which attempt to put the ship on board
						boolean bPlaced = false;
						while (bPlaced == false)
							{
							try
								{
								placeShipsOnTheBoard(oShips.getShip(i), bStraightLines);
								bPlaced = true;
								}
							catch (ShipPositionerException e)
								{
								if (iApproach >= 3)
									throw new ShipPositionerException();
								++iApproach;
								oShips.getShip(i).resetFields();
								}
							}
						}
				--iMaxSize;
				}
			}
		catch (ShipPositionerException e)
			{
			oShips.resetFields();
			return false;
			}
		return true;
		}
	/**
	 * Loading of ship and board facilities to the local properties of the facility.
	 * 
	 * @param oShips Ship container requiring ships to be placed on board.
	 */
	private void importShips(ShipIterator oShips)
		{
		this.oShips = oShips;
		this.oBoard = oShips.getBoard();
		}
	/**
	 * Randomly puts the position of the ship object passed on to the board.
	 * 
	 * @param oShip Ship, whose position should be placed on the board.
	@throws ShipPositionerException Throws an exception if the ship was not successfully placed on board.	 */
	private void placeShipsOnTheBoard(Ship oShip, boolean bStraightLines) throws ShipPositionerException
		{
		try
			{
			for (int i = 1; i <= oShip.getSize(); ++i)
				{
				if (i == 1)
					{
					// the first field
					Position oEmptyField = drawEmptyField();
					oShip.setField(i, oEmptyField.getX(), oEmptyField.getY());
					}
				else
					{
					// next position
					// create a candidate list for the next position
					int iCandidatesQuantity = 0;
					ArrayList<Position> oCandidates = new ArrayList<Position>();
					for (int j = 1; j < i; ++j)
						{
						Position oField = oShip.getField(j);
						for (int k = -1; k <= 1; ++k)
							for (int l = -1; l <= 1; ++l)
								if (k + l == -1 || k + l == 1)
									if (oField.getX() + k >= 0 && oField.getX() + k < oBoard.getWidth()
										&& oField.getY() + l >= 0 && oField.getY() + l < oBoard.getHeight()
										&& oBoard.getField(oField.getX() + k, oField.getY() + l) == FieldTypeBoard.BOARD_FIELD_EMPTY
										)
										{
										Position oCandidate = new Position(2);
										oCandidate.setX(oField.getX() + k);
										oCandidate.setY(oField.getY() + l);
										if (verifyCandidate(oCandidate, oShip))
											{
											++iCandidatesQuantity;
											oCandidates.add(oCandidate);
											}
										}
										
						}
					// if ships are to be lines, discard mismatched fields
					if (bStraightLines == true && i > 2)
						{
						// check if the ship is vertical or horizontal and discard mismatched fields
						if (oShip.getField(1).getX() == oShip.getField(2).getX())
							{
							// co-ordinates X must be the same
							for (int j = oCandidates.size() - 1; j >= 0; --j)
								if (oShip.getField(1).getX() != oCandidates.get(j).getX())
									{
									--iCandidatesQuantity;
									oCandidates.remove(j);
									}
							}
						else if (oShip.getField(1).getY() == oShip.getField(2).getY())
							{
							// co-ordinates Y must be the same
							for (int j = oCandidates.size() - 1; j >= 0; --j)
								if (oShip.getField(1).getY() != oCandidates.get(j).getY())
									{
									--iCandidatesQuantity;
									oCandidates.remove(j);
									}
							}
						else
							throw new DeveloperException("The direction of the ship could not be determined");
						}
					if (iCandidatesQuantity == 0)
						throw new ShipPositionerException();
					// draw a candidate
					int iLos = oRand.nextInt(iCandidatesQuantity);
					// set the ship's position on the drawn candidate
					oShip.setField(i, oCandidates.get(iLos).getX(), oCandidates.get(iLos).getY());
					}
				}
			}
		catch (ParameterException e)
			{
			throw new DeveloperException(e);
			}
		}
	/**
	* The method returns the co-ordinates of a randomly selected empty position on the board,
	* which is suitable for starting a new ship (has no neighbors).
	*
	* @return Returns a random blank field from board.
	*/
	private Position drawEmptyField() throws ShipPositionerException
		{
		try
			{
			Position oCoordinates = new Position(2);
			// count empty fields on the board
			int iEmptyQuantity = 0;
			for (int i = 0; i < oBoard.getWidth(); ++i)
				for (int j = 0; j < oBoard.getHeight(); ++j)
					if (oBoard.getField(i, j) == FieldTypeBoard.BOARD_FIELD_EMPTY)
						{
						Position oCandidate = new Position(2);
						oCandidate.setX(i);
						oCandidate.setY(j);
						if (verifyCandidate(oCandidate, null))
							++iEmptyQuantity;
						}
			// error if there are no empty fields
			if (iEmptyQuantity == 0)
				throw new ShipPositionerException();
			int iRandomlyDrawnField = oRand.nextInt(iEmptyQuantity) + 1;
			// go through the board again and return an empty position with the drawn number
			for (int i = 0; i < oBoard.getWidth(); ++i)
				for (int j = 0; j < oBoard.getHeight(); ++j)
					if (oBoard.getField(i, j) == FieldTypeBoard.BOARD_FIELD_EMPTY)
						{
						Position oCandidate = new Position();
						oCandidate.setX(i);
						oCandidate.setY(j);
						if (verifyCandidate(oCandidate, null))
							--iRandomlyDrawnField;
						if (iRandomlyDrawnField == 0)
							{
							oCoordinates.setX(i);
							oCoordinates.setY(j);
							return oCoordinates;
							}
						}
			throw new DeveloperException("koniec board");
			}
		catch (ParameterException e)
			{
			throw new DeveloperException(e);
			}
		}
	/**
	* The method checks if the field with the given coordinates has no ships in the vicinity.
	*
	* @param oField co-ordinates position to check.
	* @param oShip If there is a donated ship facility, this is information
	* that there may be a position of this ship and the candidate is still valid.
	* @return Returns TRUE if the field has no unwanted neighbors, or false otherwise.
	*/
	private boolean verifyCandidate(Position oField, Ship oShip)
		{
		try
			{
			for (int i = -1; i <= 1; ++i)
				for (int j = -1; j <= 1; ++j)
					if (oField.getX() + i >= 0 && oField.getX() + i < oBoard.getWidth()
						&& oField.getY() + j >= 0 && oField.getY() + j < oBoard.getHeight()
						&& oBoard.getField(oField.getX() + i, oField.getY() + j) == FieldTypeBoard.SHIP_BOARD
						)
						{
						if (oShip == null)
							return false;
						else if(oShip.getNumberPosition(oField.getX() + i, oField.getY() + j) == 0)
							return false;
						}
			return true;
			}
		catch (ParameterException e)
			{
			throw new DeveloperException(e);
			}
		}
	}

/**
 * An exception is thrown when an error occurs while placing the ship on the board. <br />
 *
 * Its occurrence informs the main positioning method that the deployment of ships is over
 * an error that cannot be resolved.
 *
 * @author VGT
 * @version 1.0
 */
class ShipPositionerException extends Exception {}
