package edu.cmu.cs.cs214.hw4.core;

/**
 * The BoardFeature indicates the board features such as double word score, double letter score
 * @author Chao
 */
public enum BoardFeature {
	STAR,// the starting square
	DLS,//double letter score
	TLS,//triple letter score
	DWS,//double word score
	TWS,//triple word score
	Normal;//no special feature here
}
