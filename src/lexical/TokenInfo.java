package lexical;

public class TokenInfo {
  private String value ; // token Name
  private int	 table ; // token table which table it's
  private int	 entry ; // index in the table
  private int    subrountine ; // 
  private int    type =0  ; // type of tb5
  private int    pointer =0 ; // pointer to TABLE 7
  
  private boolean error = false ;
  private boolean same  ;  //����
  
  
  TokenInfo(String sValue){
    this.value = sValue ;
    this.table = 0  ; // initial value
    this.entry = 0  ; // initial value
    this.same = false ;
  } // Constructor1
  
  TokenInfo( String sValue, int iTable){
	this.value = sValue ;
	this.table = iTable ;
	this.entry = 0      ;
	this.same = false ;
  }// Constructor2
  
  public TokenInfo( String sValue, int iTable, int iEntry){
	this.value = sValue ;
	this.table = iTable ;
	this.entry = iEntry ;
	this.same = false ;
	this.type = 0 ;
	this.type = 0 ;
  }// Constructor3
  
  
  public TokenInfo() {
	// TODO Auto-generated constructor stub
}

public void setValue( String sValue) {
	  this.value = "sValue" ;
  } // setValue
  
  public void setTable( int iTable) {
	  this.table = iTable ;
  } // setTable
  
  public void setEntry( int iEntry) {
	  this.entry = iEntry ;
  } // setEntry
  
  
  public void setError( boolean bError) {
	  this.error = bError ;
  } //
  
  
  
  public void setAll(String sValue, int iTable, int iEntry) {
		this.value = sValue ;
		this.table = iTable ;
		this.entry = iEntry ;
		this.same = false ;
  } //

  public void setSubroutine( int iSubrountine) {
	  this.subrountine = iSubrountine ;
  } // setSubrountine
  
  public void setType ( int iType ) {
	  this.type = iType ;
  } // setType
  
  public void setPointer ( int iPointer) {
	  this.pointer = iPointer ;
  } // setPointer
  
  public void setTrue (  ) {
	  this.same = true ;
  } // setTrue
  
  public boolean getError() {
	 return this.error ; 
  }//
  public String getValue() {
	  return this.value ;
  } //getValue
  
  public int getTable() {
	  return this.table ;
  } // getTable
  
  
  public int getEntry() {
	  return this.entry ;
  } // getEntry
  
  public int getSubroutine( ) {
	  return  this.subrountine ;
  } // getSubrountine
  
  public int getType ( ) {
	   return this.type ;
  } // getType
  
  public int getPointer ( ) {
	  return this.pointer  ;
  } // getPointer
  
  public boolean getSame (  ) {
	   return this.same ;
  } // getTrue
  
  
  
  
  
  
  public String toString_value() {	// output the token Value
	  return this.getValue() + "\t" ;
  }// toString()
  
  public String toString_table() {	// ouput the token table and entry
	  return "(" + this.getTable() + "," + this.getEntry() + ")"  ;
  }// toString()
  
  
} //Class TokenInfo
