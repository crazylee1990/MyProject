package edu.cmu.cs.cs214.hw4.core;

import java.util.ArrayList;

import edu.cmu.cs.cs214.hw4.core.SpecialTiles.SpecialTile;

/**
 * This method is the content of the board
 * every content contains three kind of stuff: 
 * 1.Array list of special tiles
 * 2.Tile on the slot;
 * 3.the feature for this space such as the double word score;
 * @author Chao
 *
 */
public class BoardContent {
	private Tile tile;
	private ArrayList<SpecialTile> specialTile;
	
	public BoardContent() {
		specialTile = new ArrayList<SpecialTile>();
	}
	
	public void setTile(Tile tile) {
		this.tile = tile;
	}
	
	public void setSpecialTile(SpecialTile special) {
		specialTile.add(special);
	}
	
	public BoardFeature getBoardFeature(Location loc) {
		LocEnum locEnum = LocEnum.A1;
		String locString = loc.toString();
		locEnum = LocEnum.valueOf(locString);;
		
		switch (locEnum) {
		case H8:
			return BoardFeature.STAR; //start location
		case   A1 : case   H1 :  case  O1 :
		case   A8 : case   O8 :
		case  A15 : case  H15 : case  O15 :
			return BoardFeature.TWS; //triple word square
		case   B2 : case   C3 : case   D4 : case   E5 :
		case   K5 : case   L4 : case   M3 : case   N2 :
		case  B14 : case  C13 : case  D12 : case  E11 :
		case  K11 : case  L12 : case  M13 : case  N14 :
			return BoardFeature.DWS; //double word square
		case   F2 : case   J2 : 
		case   B5 : case   F6 : case   J6 : case   N6 :
		case  B10 : case  F10 : case  J10 : case  N10 :
		case  F14 : case  J14 : 
			return BoardFeature.TLS; //triple letter square
		case   D1 : case   L1 : 
		case   G3 : case   I3 :
		case   A4 : case   H4 : case   O4 : 
		case   C7 : case   G7 : case   I7 : case  M7 :
		case   D8 : case   L8 :
		case   C9 : case   G9 : case   I9 : case  M9 :
		case  A12 : case  H12 : case  O12 :
		case  G13 : case  I13 :
		case  D15 : case  L15 :
			return BoardFeature.DLS; //double letter square
		default:
			// if not other type, then return empty
			return BoardFeature.Normal; //normal square
		}
	}
	
	public Tile getTile() {
		if(tile == null){
			return null;
		}else {
			return tile;
		}
		
	}
	
	public ArrayList<SpecialTile> getSpecialTile() {
		if(specialTile.size() == 0){
			return null;
		}else {
			return specialTile;
		}
	}

}
