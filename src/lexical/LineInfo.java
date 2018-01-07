package lexical;

import java.util.ArrayList;

public class LineInfo {
	ArrayList<TokenInfo> lineList ; // Store Token inside the Line
	
	public LineInfo() {
	  lineList = new ArrayList<TokenInfo>() ;
	} // Constructor 1
	
	public LineInfo( ArrayList<TokenInfo> oLinelist) {
	  lineList = oLinelist ;
	} // Constructor 2
	
	public void insertTokenInfo( TokenInfo oTokenInfo) {
		lineList.add(oTokenInfo) ;
	} // insertTokenInfo
	
	public void insertTokenInfo_spe(String sValue, int iTable, int iEntry) {
		TokenInfo oTokenInfo = new TokenInfo(sValue, iTable, iEntry) ;
	} // insertTokenInfo_spe
	
	public ArrayList<TokenInfo> getLineList() {
      return this.lineList ;
	} //
	
	public void setLineList(ArrayList<TokenInfo> oLineList) {
	      this.lineList = oLineList ;
	} //
	
	
	public String toString(String temp) { // output Lineinfo
		String opt = "" ;
		for ( int i = 0 ; i < lineList.size() ; i++ ) 
		  opt += lineList.get(i).toString_value() ; 	
		opt +=  temp + "\n" ;
		
		for ( int i = 0 ; i < lineList.size() ; i++ ) 
			  opt += lineList.get(i).toString_table() ; 	
			opt += "\n" ;
	    return opt ;
	} // toString
	
	
} // LineInfo
