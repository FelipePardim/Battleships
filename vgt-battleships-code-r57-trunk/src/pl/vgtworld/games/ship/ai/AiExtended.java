package pl.vgtworld.games.ship.ai;

import pl.vgtworld.games.ship.ShipIterator;

/**
 * Prosta implementacja interface'u Ai.<br />
 * 
 * Strzela w losowo wybrane pole, dopoki nie trafi w statek.
 * Po trafieniu w statek, jesli ma on wiecej pol,
 * sa ostrzeliwane sasiednie position az do zatopienia statku.
 *  
 * @author VGT
 * @version 1.0
 */
public class AiExtended
	extends AiGeneric
	implements Ai
	{
	/**
	 * Konstruktor.
	 * 
	 * @param oShips Kontener statkow nalezacych do gracza sterowanego przez dany obiekt Ai.
	 */
	public AiExtended(ShipIterator oShips)
		{
		super(oShips);
		}
	/**
	 * Implementacja metody interface'u Ai.
	 */
	public boolean shot(ShipIterator oOpponentShip)
		{
		if (oUsefulHits.size() > 0)
			{
			return shotNeighbor(oOpponentShip);
			}
		else
			return shotRandom(oOpponentShip);
		}
	}
