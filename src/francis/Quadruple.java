package francis;

import java.util.ArrayList;

import lexical.TokenInfo;

public class Quadruple {
 private boolean isForward ; // need to do the forward reference or not
 private String  statement ; // Quadruple statement
 ArrayList<TokenInfo> quadruple ;  //Quadruple TokenInfo
 
 Quadruple ( String iStatement, ArrayList<TokenInfo> oQuadruple) {
	 this.statement = iStatement ;
	 this.quadruple = oQuadruple ;
	 //System.out.println(" " +this.statement + "  YYYYYYYYYYYYYYYYYYY         "   +  this.quadruple.size());
	 this.isForward = false      ; // set the initial isForward is false, no need to deal with forward reference
 } // Constructor 1
 
 
 Quadruple ( String iStatement, ArrayList<TokenInfo> oQuadruple, boolean bForward ) {
	 this.statement = iStatement ;
	 this.quadruple = oQuadruple ;
	 //System.out.println(" " +this.statement + "  YYYYYYYYYYYYYYYYYYY         "   +  this.quadruple.size());
	 this.isForward = bForward     ;
 } // Constructor 2
 	
	
 public Quadruple() {
	// TODO Auto-generated constructor stub
}


public void setStatement( String sStatement ) {
	  this.statement = sStatement  ;
 } // setValue
 
 public void setQuadruple( ArrayList<TokenInfo> oQuadruple ) {
	  this.quadruple = oQuadruple ;
 } // setTable
 
 public void setForward( boolean bIsForwand) {
	  this.isForward =  bIsForwand;
 } // setEntry
 
 public String getStatement( ) {
	  return this.statement   ;
} // getStatement
 
 public boolean getForward() {
	 return this.isForward ;
 } //
 
 
 public ArrayList<TokenInfo> getQuadruple () {
	  return this.quadruple ;
 } // isForward

 
 



 public void dealForward( int forwardValue,int situation,String tempForward ) { // deal with the forward Referrence
	 
	 
	 
	 
	 if (situation == 1) { // GO TO
		 for ( int i = 0 ; i < quadruple.size() ; i++ )  {
			 if( this.quadruple.get(i).getEntry() == -1)  {
				 this.quadruple.get(i).setEntry(forwardValue);  
			 } 
		 } // for
		 this.statement = this.statement.replaceAll("-1", Integer.toString(forwardValue)) ;
	 }else if ( situation == 2) { // Assignment if
		 int tempEntry = 0 ;
		 for ( int i = 0 ; i < quadruple.size() ; i++ )  // find out the Quadruple have how many TokenInfo 
			 if( this.quadruple.get(i).getEntry() == -1)  
	            tempEntry++ ; 
		 
		 if ( tempEntry == 2) {  // eg: (AND,T3,Q,T4)
			 this.quadruple.get(2).setEntry(forwardValue-1); 
			 //String changeValue = this.quadruple.get(1).getValue().replaceAll("-1",Integer.toString(forwardValue--) ) ; //set first Temp TokenInfo
			 this.quadruple.get(2).setValue("T" +  Integer.toString(forwardValue-1));
			 this.statement.replaceFirst("T-1", "T" +  Integer.toString(forwardValue-1)) ;
			 //System.out.println(this.statement);
			 
			 this.quadruple.get(3).setEntry(forwardValue); 
			 //changeValue = this.quadruple.get(3).getValue().replaceAll("-1",Integer.toString(forwardValue) ) ;  //set second Temp TokenInfo
			 this.quadruple.get(3).setValue("T" +  Integer.toString(forwardValue));
			 this.statement.replaceAll("T-1", "T" +  Integer.toString(forwardValue)) ;
		 } else if ( tempEntry == 0 ) { // eg: (GT,X,Y,T4)
			 if ( this.quadruple.get(3).getValue().equals(tempForward) == true) {
				 this.quadruple.get(2).setEntry(forwardValue-1); 
				 //System.out.println(this.statement);
				 //System.out.println( this.quadruple.get(2).getValue());
				 //String changeValue = this.quadruple.get(2).getValue().replaceAll("-1",Integer.toString(forwardValue-1) ) ; //set only one Temp TokenInfo
				 //System.out.println(changeValue + "FUCK "  );
				 this.quadruple.get(2).setValue("T" +  Integer.toString(forwardValue-1));
				 this.statement = this.statement.replaceAll("T-1", "T" +  Integer.toString(forwardValue-1)) ;
				 //System.out.println(this.statement);
		     } // if
		 } else if ( tempEntry == 1) {
				 this.quadruple.get(3).setEntry(forwardValue); 
				 String changeValue = this.quadruple.get(3).getValue().replaceAll("-1",Integer.toString(forwardValue) ) ; //set only one Temp TokenInfo
				 this.quadruple.get(3).setValue(changeValue);	
				 this.statement = this.statement.replaceAll("T-1", changeValue) ;
				 //System.out.println(this.statement);
		     }
	     
			 //System.out.println(this.statement);
		  //
	 } else if (situation == 3) {
		   this.quadruple.get(1).setEntry(forwardValue);
		   this.statement = this.statement.replaceAll("-1", Integer.toString(forwardValue)) ;
	 }// else if 
	 
	 
	 } // dealForward
 
 public String showQuadruple() { // output the Quadruple Line 
	 String quadrupleForm = "" ;
	 //System.out.println(this.quadruple.size());
	 int num = this.quadruple.size() ;
	   if ( num == 1 )
		   quadrupleForm =  "(" + this.quadruple.get(0).toString_table() + ",  ,  ,  )  " + "\t" + this.statement ;
	   else if ( num == 2 )
		   quadrupleForm =  "(" + this.quadruple.get(0).toString_table() + ",,  ," + this.quadruple.get(1).toString_table()+") " + "\t" + this.statement ;
	   else if ( num == 3 )
		   quadrupleForm =  "(" + this.quadruple.get(0).toString_table() + "," + this.quadruple.get(1).toString_table() + ",  ," + 
	                              this.quadruple.get(2).toString_table()  +") " + "\t" +this.statement ;
	   else if ( num == 4 )
		   quadrupleForm =  "(" + this.quadruple.get(0).toString_table() + "," + this.quadruple.get(1).toString_table() + "," + 
                                  this.quadruple.get(2).toString_table() + "," +  this.quadruple.get(3).toString_table()  +") " + "\t" + this.statement ;
	   else
		   quadrupleForm =  "No shit, Motherfucker \n";
		  
		  // System.out.println(quadrupleForm);	   
	   return  quadrupleForm;	   
	 
 } // showQuadruple
 
 
	
} // Quadruple
