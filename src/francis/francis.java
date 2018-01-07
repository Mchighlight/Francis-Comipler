package francis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import lexical.LineInfo;
import lexical.TokenInfo;

public class francis {
	private ArrayList<LineInfo> lexicalFile = new ArrayList<LineInfo>() ; // Store lexical File
	private ArrayList<LineInfo> statement = new ArrayList<LineInfo>() ; // Store Statement


	private ArrayList<String> table7 = new ArrayList<String>() ; // Store Information table
	private ArrayList<Quadruple> myQuadruple = new ArrayList<Quadruple>() ; //store quadruple table
	private ArrayList<TokenInfo> table5 = new ArrayList<TokenInfo>() ; // Store randomNumber table
	private int curQuadruple ; // current quadruple number
	private int arraySize = 500		; // arraySize
	private int subrountine ;  // current subroutine

	private int  randomNumber = 0 ; // randomNumber(T)-->Deal with some Statement Like Assignment or If

	private String FileName;  // output need the File Name
	private String errorType ; // Which errorType the Statement it's
	
	private String optError ; // output Statement error

	private String label = "" ; // Which Lable is it eg: L91 ENS ;     L92 IF X AND Y THEN X = X + 2 ELSE X = X + 3

	private String[] type =  {"ARRAY", "BOOLEAN", "CHARACTER", "INTEGER", "LABEL", "REAL"} ; // 6Type

	private ArrayList<String[]> tableList = new ArrayList<String[]>(); // Hash TableList(Not Using)





	public francis( ArrayList<LineInfo> oLexicalFile, String sFileName, ArrayList<String[]> oTableList ) { //Constructor 1
		this.lexicalFile = oLexicalFile ;
		this.tableList = oTableList ;
		this.FileName  = sFileName ;
		this.optError = "" ;
		this.curQuadruple = 0 ;
		toStatement() ;
		this.toLexical_output();
		this.makeQuadruple();
		this.toQuadruple_output();
	} // constructor1


	public  void setQuadruple( ArrayList<Quadruple> oQuadrupble) { // set the Quadruple
		this.setQuadruple(oQuadrupble);
	} // setQuadruple()

	public  ArrayList<Quadruple> getQuadruple() {	// get the Quadruple
		return  this.myQuadruple ;
	} // getQuadrupble()


	
	public boolean skip(String tokenString) { // if the TokenInfo have exist inside the table5
		
		for ( int i = 0 ; i < this.table5.size() ; i++ ) {
			if ( this.table5.get(i) != null && this.table5.get(i).getValue().equals(tokenString) == true ) return true ; 
		}//for
		return false ;
	} //
	 
	public  void makeTable5(TokenInfo  token) {  // make the Table5 1.Call by all the other make Function
	      long arrayIndex = 0 ; // all of grammar plus together
	      for ( int  y = 0 ; y < token.getValue().length() ; y++ ) { // ASCII�ۥ[
	    	 arrayIndex =  arrayIndex + token.getValue().charAt(y) ;
	      }

	      arrayIndex = arrayIndex % 100; //��100�l��
	      //System.out.println(arrayIndex);

		while( this.table5.get((int) arrayIndex) != null || skip(token.getValue()) == true )  { //collision push back one position			 
	      ++arrayIndex ; // push back one position
	      arrayIndex %= arraySize;
          break ;
	    } // while

		
		  if ( skip(token.getValue()) == true)
          token.setEntry((int) arrayIndex );
		  this.table5.add((int) arrayIndex, token); // add token to  the null location

	} // makeTable5




	public void toStatement() { //add LexicalFile to Statement
		int statement = 0 ; // statement amount
		ArrayList<TokenInfo> lexical = new ArrayList<TokenInfo>() ;
		ArrayList<TokenInfo> tempToken = new ArrayList<TokenInfo>() ;

		for ( int j = 0 ; j < lexicalFile.size(); j++) {
			lexical = lexicalFile.get(j).getLineList() ; // get the LexicalFile LineInfo

			for ( int i = 0 ; i < lexical.size()  ; i++ ) {
				  if ( lexical.get(i).getValue().equals("PROGRAM") || lexical.get(i).getValue().equals("GTO")||
					   lexical.get(i).getValue().equals("VARIABLE")|| lexical.get(i).getValue().equals("CALL") ||
					   lexical.get(i).getValue().equals("LABEL")|| lexical.get(i).getValue().equals("INPUT") ||
					   lexical.get(i).getValue().equals("DIMENSION")|| lexical.get(i).getValue().equals("OUTPUT") ||
					   lexical.get(i).getValue().equals("IF") || lexical.get(i).getValue().equals("ENP")||
					   lexical.get(i).getValue().equals("ENS") || lexical.get(i).getValue().equals("=")
					 ){ // it it's Statement



					 statement++ ; // Statement plus one
					 i = lexical.size();
				  }
				} // for

		    if ( statement <= 1  ) {
		    	if (lexical.get(lexical.size()-1).getValue().equals(";")) { // is correct Statement
		    		tempToken.addAll(lexical) ; // add other to last LineInfo
			    	LineInfo tempLineInfo = new LineInfo(tempToken) ; // use to add to this.statement
			      	this.statement.add(tempLineInfo) ;
			      	statement = 0 ;	// reset statement
			      	tempToken = new ArrayList<TokenInfo>() ; // reset  tempToken
		    	} //
		    	else { // is not ; keep adding to tempToken
		    	   tempToken.addAll(lexical) ;
		    	} //
		    } else if ( statement > 1  ) { // not correct Statement but still add to Statement
		      j-- ;	// move to last lineInfo
		      statement = 0 ; // reset statement
		      LineInfo tempLineInfo = new LineInfo(tempToken) ; // still add the Syntax error Statement to this.statement
		      this.statement.add(tempLineInfo) ;
		      tempToken = new ArrayList<TokenInfo>() ; // reset tempToken
		    } //

		} //for


	} // toStatement



	public boolean makeStatement(int line, int start,ArrayList<TokenInfo> tempToken, boolean isIf) { // Make the Statement Find out Statement 1.Call by makeQuadruple(isIf is false) and makeIf( isIf is true)
		boolean isError = false ; // syntax error
		this.errorType = "" ; // which Statement have error
		String errorStatement = "" ;
		//System.out.println( "after Enter Statement " + tempToken.get(start).getValue());
		if ( tempToken.get(start).getValue().equals("PROGRAM") == true) {          // If Statement first TokenInfo is PROGRAM
     
		  if ( this.makeProgram(tempToken, 0)== true  ) {
       		  isError = false ;
       		//System.out.println( "after Enter Statement " + tempToken.get(start).getValue() + line );
       	  }else{
       		  errorStatement = "PROGRAM" ;
       		  isError = true ;
       	  }
        }//if PROGRAM
        else if (tempToken.get(start).getValue().equals("VARIABLE")== true ) {   // If Statement first TokenInfo is VARAIABLE
          if ( this.makeVariable(tempToken, 0, 0, null)== true  ){
        	  //System.out.println( "after Enter Statement " + tempToken.get(start).getValue() + line );
        	  isError = false ;
          }
          else{
        	  errorStatement = "VARIABLE" ;
        	  isError = true ;
          }
        } // else if VARAIABLE
        else if (tempToken.get(start).getValue().equals("DIMENSION")== true) {   // If Statement first TokenInfo is DIMENSION
       	  if ( this.makeVariable(tempToken, 0, 1, null)== true ) { // call the makeVaraiable
       		//System.out.println( "after Enter Statement " + tempToken.get(start).getValue() + line );
       		  isError = false ;
       	  }
       	  else{
       		 errorStatement = "DIMENSION" ;
       		  isError = true ;
       	  }
        } // else if  DIMENSION
        else if (tempToken.get(start).getValue().equals("SUBROUTINE")== true) {
        	if (this.makeSubroutine(tempToken, 0)) {
           		//System.out.println( "after Enter Statement " + tempToken.get(start).getValue() + line );
         		  isError = false ;
        	}//
        	else {
        		 errorStatement = "SUBROUTINE" ;
           		  isError = true ;
        	} 	
        }// else if SUBROUTINE 
        else if (tempToken.get(start).getValue().equals("LABEL")== true ) {   // If Statement first TokenInfo is LABEL
       	  if ( this.makeLabel(tempToken, 0,true,null)== true ) {
       		//System.out.println( "after Enter Statement " + tempToken.get(start).getValue() + line );
       		  isError = false ;
       	  }
       	  else{
       		 errorStatement = "LABEL" ;
       		  isError = true ;
       	  }
        } // else if LABEL
        else if (tempToken.get(start).getValue().equals("GTO")== true ) {   // If Statement first TokenInfo is GTO
       	  if ( this.makeGto(tempToken, 0,isIf)== true ) {
       		//System.out.println( "after Enter Statement " + tempToken.get(start).getValue() + line );
       		  isError = false ;
       	  }
       	  else {
       		 errorStatement = "GTO" ;
       		  isError = true ;
       	  }
        } // else if GTO
        else if (tempToken.get(start).getValue().equals("CALL")== true  )  {  // If Statement first TokenInfo is CALL
       	  if ( this.makeCall(tempToken, 0)== true ) {
       		//System.out.println( "after Enter Statement " + tempToken.get(start).getValue() + line );
       		  isError = false ;
       	  }
       	  else {
       		 errorStatement = "CALL" ;
       		  isError = true ;
       	  }
        } // else if CALL
        else if (tempToken.get(start).getValue().equals("INPUT")== true  ) {    // If Statement first TokenInfo is INPUT
       	  System.out.print("I didnt do  the INPUT Statement So sorry  ");
        } // else if INPUT
        else if (tempToken.get(start).getValue().equals("OUTPUT")== true ) {
          System.out.print("I didnt do  the OUTPUT Statement So sorry  ");  // If Statement first TokenInfo is OUTPUT
        } // else if OUTPUT
        else if (tempToken.get(start).getValue().equals("ENP")== true ) {   // If Statement first TokenInfo is ENP
       	  if ( this.makeEndMainSubroutine(tempToken,0)== true  ) {
       		//System.out.println( "after Enter Statement " + tempToken.get(start).getValue() + line );
       		  isError = false ;
       	  }
       	  else {
       		 errorStatement = "ENP" ;
       		  isError = true ;
       	  }
        } // else if  ENP
        else if (tempToken.get(start).getValue().equals("ENS")== true ) {   // If Statement first TokenInfo is ENS
         if ( this.makeEndMainSubroutine(tempToken,0)== true  ) {
        	 //System.out.println( "after Enter Statement " + tempToken.get(start).getValue() + line );
        	 isError = false ;
         }
         else {
        	 errorStatement = "ENS" ;
        	 isError = true ;
         }
        } // else if  ENS
        else if (tempToken.get(start).getValue().equals("IF")== true  ) {  // If Statement first TokenInfo is IF
       	 if ( this.makeIf(tempToken, 0, line)== true ) {
       		//System.out.println( "after Enter Statement " + tempToken.get(start).getValue() + line );
       		 isError = false ;
       	 }
       	 else  {
       		 errorStatement = "IF" ;
       		 isError = true ;
       	 }
        } // else if IF
        else if (tempToken.get(start).getTable() == 5 ) {     // If Statement first TokenInfo is ASSIGNMENT
       	 if ( this.makeAssignment(tempToken, start, isIf)== true ) {
       		//System.out.println( "after Enter Statement " + tempToken.get(start).getValue() + line );
       		isError = false ;
       	 }
       	 else {
       		 errorStatement = "ASSIGNMENT" ;
       		isError = true ;
       	 } //
        } // else if Assignment
        else {
       	 isError = true ; // Cannot Define which it is
       	 errorStatement = "not know  " ;
        } // else Syntax error


		if ( isError == true ) {
			int temp = line + 1 ;
			this.optError = this.optError + temp + " Line have Syntax error \n" + errorStatement + " Statement is not correct \n" + this.errorType ;
			System.out.println(line+1 + "Line have Syntax error \n" + errorStatement + " Statement is not correct \n" + this.errorType); // printout this Line have error and its which Statement
			this.label = "" ; // Lable Identifier eg: L91 ENS ;
			return false ;
		}
		else {
			System.out.println(line+1 + "Line Complier Success" +tempToken.get(start).getValue()); // printout Success
			this.label = "" ; // Lable Identifier eg: L91 ENS ;
			return true ;
		} //else








	} // makeStatement


	public void makeQuadruple () { // Make the Quadruple form and deal with forward Reference 1. Call makeStatment
		/******************************************************************Do the first make Quadruple form****************************************************************************************/
		for ( int i = 0 ;i < 500 ; i++) this.table5.add(null) ; // Initial the ArrayList




        
		for ( int i = 0 ; i < this.statement.size() ; i++ ) { // start from first Statement
			this.label = "" ; // eg: L91 L92
			LineInfo tempStatement = statement.get(i) ;	 // get This Line Statement
			int start = 0 ; // Statement Arraylist  start number
			int isLabel = 0 ; // It's Label or not
			ArrayList<TokenInfo> tempToken = new ArrayList<TokenInfo>() ; // tempToken will add to Quadruple

			for ( int j = 0  ; j < table5.size(); j++ ) { // find out its Label Identifier or not, If it's need to do Forward Reference, change the Pointer of  Label to current Quadruple
				if (  table5.get(j) != null && table5.get(j).getTable() == tempStatement.getLineList().get(start).getTable()  &&
					 table5.get(j).getEntry() == tempStatement.getLineList().get(start).getEntry()  &&  table5.get(j).getType() == 5 && table5.get(j).getSame()== true ) { // 1.it's same TokenInfo 2.It's Label 3.Its forward Reference
					//System.out.println("before Enter Label Deal with Forward Reference" + table5.get(j).getValue() ) ;
					 int temp = this.curQuadruple + 1 ;
					 //System.out.println(this.curQuadruple);
					 table5.get(j).setPointer(temp); // set the Label Pointer to next Quadruple(later need to deal with)
					 //System.out.println(this.table5.get(j).getPointer() + " " +this.table5.get(j).getValue());
					 this.label = tempStatement.getLineList().get(0).getValue() ;  // Label later to print out
				     tempStatement.getLineList().remove(0) ; //??????????????BUG remove the first TokenInfo(LABEL TokenInfo)
					 j = table5.size() ; // break loop
					 isLabel = 1 ;
				}// if
			} // for

			tempToken = tempStatement.getLineList() ;
			//System.out.println("before Enter Statement  " +  tempToken.get(0).getValue()  + "   Line of Statement" + i);
			if ( makeStatement(i,0,tempToken,false ) == true ){// start at 0, line is current Value(i), It's not iF Statement
              /*******************Test Success********************************************************/
			} //
			else { // makeStatement== false
			  if (isLabel == 1 ) { // if makeStatement is false need to change label to original
					for ( int j = 0  ; j < table5.size(); j++ ) { // find out its Label Identifier or not, If it's need to do Forward Reference, change the Pointer of  Label to current Quadruple
						if ( table5.get(j) != null && table5.get(j).getTable() == tempStatement.getLineList().get(start).getTable()  &&
							 table5.get(j).getEntry() == tempStatement.getLineList().get(start).getEntry()  &&  table5.get(j).getType() == 5 && table5.get(j).getSame()== true ) { // 1.it's same TokenInfo 2.It's Label 3.Its forward Reference
							 table5.get(j).setPointer(0); // set the Label Pointer to 0
							 j = table5.size() ; // break loop
						}// if
					} // for
			  } // if

			} //else


		} // for
		/*********************************************************************************************************************************************************************************************/


		/***************************************************Do the Forward Reference********************************************************************************************/
		for ( int i = 0 ; i < this.myQuadruple.size() ; i++ ) { // find out which Quadruple need to do Forward Reference
		  if ( this.myQuadruple.get(i).getForward() == true ) { // It's true
		    for (  int j = 0 ; j < this.table5.size() ; j++ ) { // find out this which Label in Table5 need to do Forward Reference
		    	if ( this.table5.get(j) != null && this.table5.get(j).getValue().equals(this.myQuadruple.get(i).getQuadruple().get(1).getValue() ) == true
		    	  && this.myQuadruple.get(i).getQuadruple().get(0).getValue().equals("GTO")
		    	  && this.table5.get(j).getPointer() !=0) {  // Find out its Value and Entry are same in Table5
		    		this.myQuadruple.get(i).dealForward(this.table5.get(j).getPointer(), 1,""); // Set it to the Quadruple location
		    		this.myQuadruple.get(i).setForward(false ); // No need to do Forward Reference
					//System.out.println("FUCKKKKYESSSSSSSSSSSSSSSSSSSSS"+ i );
		    	} //
		    	else if ( this.table5.get(j) != null && this.table5.get(j).getValue().equals(this.myQuadruple.get(i).getQuadruple().get(1).getValue()) && // deal with CALL STATEMENT
		    			  this.myQuadruple.get(i).getQuadruple().get(0).getValue().equals("CALL") ){
		    		this.myQuadruple.get(i).dealForward(this.table5.get(j).getEntry(), 3,""); // Set it to the Quadruple location
		    		this.myQuadruple.get(i).setForward(false ); // No need to do Forward Reference
		    		
		    	}//
		    } // for
		  } //
		} // for


		/*********************************************************************************************************************************************************************/

	} // makeQuadruple


	public boolean makeEndMainSubroutine( ArrayList<TokenInfo> tempToken, int start ) { // make the ENS,ENP Statement 1.call by makeStatment, makeIf
		 if ( tempToken.get(tempToken.size()-1).getTable() == 1 && tempToken.get(tempToken.size()-1).getEntry() ==1 ) { // the Last TokenInfo is ";" eg: ENS; ENP;

			 if ( tempToken.size() == 2) { // eg: ENS ; only have two TokenInfo
				 ArrayList<TokenInfo> addToken = new ArrayList<TokenInfo>() ; // ArrayList use to add to Quadruple
				 Quadruple tempQuadruple  = new Quadruple() ; // tempQuadruple use to add to Quadruple table
				 if ( ( tempToken.get(start).getTable()==2 && tempToken.get(start).getEntry()== 6 ) ||
					  ( tempToken.get(start).getTable()==2 && tempToken.get(start).getEntry()== 7 )) { // ENP(2,6) or ENS(2,7)
					 addToken.add(tempToken.get(start)) ; // add END Statement start = 0
					 tempQuadruple = new Quadruple(this.label +  " "+ addToken.get(0).getValue(),addToken) ;
					 this.myQuadruple.add(tempQuadruple) ;// add to Quadruple table
					 this.curQuadruple++ ; // update current Quadruple number
					 //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
					 //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
					 return true ;
				 } // if
				 else {
					 this.errorType = this.errorType + tempToken.get(0).getValue()  + "this is not End Statement \n" ;
					 return false ;
				 } //else
			 }//
			 else {
				 this.errorType = this.errorType  +tempToken.get(tempToken.size()-1).getValue()  + " is not the Identifier\n" ;
				 return false ;
			 } // not ;
			 } // if
			 else {

				 this.errorType = this.errorType + tempToken.get(tempToken.size()-1).getValue()  + " is not Correct END statement \n" ;
				 return false ;
			 }




	} // EndMainSubroutine




	public int whichType ( String Type) { // Decide it's which type in 6 DataType ;
	  for ( int i = 0 ; i <this.type.length ;i++ )
		  if ( Type.equals(this.type[i] ))
			  return i+1 ; // plus one because it need to put in Pointer

	  return 0 ; // is Not the correct Datatype
	} // whichType




	public boolean isMultiVar( ArrayList<TokenInfo> addToken, int type,TokenInfo subroutine ) { // Decide it's which type of Variable and add its to Quadruple table 1. call by makeVariable
		int isType = 0, isDot = 0 ; // Type amount and Dot amount
		for ( int i = 0 ; i < addToken.size() ; i++) { // Find out the amount of Dot and Type
			if ( addToken.get(i).getTable() == 5 ) isType++ ;
			if ( addToken.get(i).getTable() == 1 && addToken.get(i).getEntry() == 11 ) isDot++ ;
		} // for


		if ( (isType - isDot == 1 && isDot != 0 ) || (  isDot == 0 && isType ==1 )) { // Condition1: X,Y,Z condition2:X

		  if ( subroutine != null)	{ // It's not Subroutine don't need to deal with subroutine
			int duplicated = 0 ; // Decide the Subroutine Identifier is Duplicated or not
			for ( int i = 0 ; i < this.table5.size() ;i++  )  // find out the subroutine in the table5
			  if ( this.table5.get(i) != null && this.table5.get(i).getValue().equals(subroutine.getValue())) duplicated++ ; //It's Duplicate

			if ( duplicated == 0) { // It's Duplicated
				int temp = this.curQuadruple + 1 ;
			    subroutine.setPointer(temp); // add the pointer point to it's next location of Quadruple
			    this.makeTable5(subroutine); // add to the Subroutine
				  

			    for ( int i = 0 ; i < this.table5.size() ;i++  ) {// find out the location of Subroutine in Table5
			      if ( this.table5.get(i)!= null && this.table5.get(i).getValue().equals(subroutine.getValue() )) {
			    	  this.subrountine = i  ; // location plus one because  i start from 0
			    	  this.table5.get(i).setEntry(this.subrountine);
			    	  //System.out.println(this.table5.get(i).getEntry());
			    	  //System.out.println(this.subrountine);
			      }
			    }
			    
			    
			    
			} //
			else {
				this.errorType = this.errorType + " Subroutine Identifier is duplicated \n" ;
				return false ;
			} //



		  } // if
 
		  
		 

		  for ( int i = 0 ; i < addToken.size() ; i++ ) { //add the Identifier to Quadruple
			  if ( addToken.get(i).getTable() == 5 )  {
			    addToken.get(i).setSubroutine(this.subrountine); // set the subroutine
			    
			    addToken.get(i).setType(type); //set the DataType
			    ArrayList<TokenInfo> tempToken = new ArrayList<TokenInfo>() ; // use the ArrayList to add into Quadruple
			    tempToken.add(addToken.get(i)) ; // add to ArrayList

			    this.makeTable5(addToken.get(i)); // add to table5
			    Quadruple tempQuadruple = new Quadruple(tempToken.get(0).getValue() ,tempToken) ; // add To Quadruple form
			    this.myQuadruple.add(tempQuadruple) ;
			    this.curQuadruple++ ;
				//System.out.print(addToken.get(i).getValue() + addToken.get(i).getSubroutine());
			  } // if
		  } // for
		} // if �ŦX��k
		else {
			this.errorType = this.errorType + "The" + this.type[type--] + "'s Identifer and dot amount is not Correct \n" ;
			return false ;
		} //


		//System.out.println();
		return true ;
	} // boolean isMultiVar


	public boolean makeArray( ArrayList<TokenInfo> arrayToken, int type ) { // add the array into Quadruple table 1. call by isMulArray
		int isType = 0, isDot = 0 ; // type and Dot amount
		int state = 0 ; // it's should do which type

		if (  type == 4 || type == 6 ) {  // It's INTEGER REAL
			state = 1 ; // number
		} else if ( type == 1 || type ==2 || type == 3 || type == 5   ) { // It's ARRAY BOOLEAN CHARACTER LABEL
			state = 2 ; // char
		} //



		if (state == 1 ) { //it's is number array
			if ( type == 6 ) { // it's REAL
				for ( int i = 0 ; i < arrayToken.size() ; i++) {
					if ( arrayToken.get(i).getTable() == 3 && arrayToken.get(i).getTable() == 4 ) isType++ ; // It's INTEGER and REAL
					if ( arrayToken.get(i).getTable() == 1 && arrayToken.get(i).getEntry() == 11 ) isDot++ ; // It's Dot
				} // for
			} //
			else { // It's INTEGER
				for ( int i = 0 ; i < arrayToken.size() ; i++) {
					if ( arrayToken.get(i).getTable() == 3  ) isType++ ; // only INTEGER
					if ( arrayToken.get(i).getTable() == 1 && arrayToken.get(i).getEntry() == 11 ) isDot++ ; // It's Dot
				} // for
			} //
		} else if ( state ==2) { // it's char array
			for ( int i = 0 ; i < arrayToken.size() ; i++) {
				if ( arrayToken.get(i).getTable() == 5  ) isType++ ; // It's Identifier
				if ( arrayToken.get(i).getTable() == 1 && arrayToken.get(i).getEntry() == 11 ) isDot++ ; // It's Dot
			} // for
		} // else if






		if ( (isType - isDot == 1 && isDot != 0 ) || (  isDot == 0 && isType ==1 )) { //eg: A  (4,5)     or     A   (5)

 			  if ( arrayToken.get(0).getTable() == 5 )  { // eg: A(4,5)-->first TokenInfo is Identifier
				  arrayToken.get(0).setSubroutine(this.subrountine); //set the Subroutine to current Subroutine
				  arrayToken.get(0).setType(1); // Set the Identifier Type is Array

				  if (this.table7.size() != 0 ) // if the table7 is not empty
				    arrayToken.get(0).setPointer(this.table7.size()+1); // set the Identifier pointer is current table7 size
				  else
					  arrayToken.get(0).setPointer(1) ; //is empty set the Identifier pointer is 1


				  
			      ArrayList<TokenInfo> tempToken = new ArrayList<TokenInfo>() ; //ArrayList use to add to Quadruple form

			      this.table7.add(String.valueOf(type)) ; // add the Datatype of Array to table7
			      this.table7.add(String.valueOf(isType)) ; // add How Many Dimension to table7
			      //System.out.println("Datatype is " + String.valueOf(type) + "Array Dimension is " + String.valueOf(isType));
				  for ( int i = 1 ; i < arrayToken.size() ; i++ ) {

						if (state == 1 ) { //it's is number array
							if ( type == 6 ) { // it's REAL
						      if ( arrayToken.get(i).getTable() == 3 &&  arrayToken.get(i).getTable() == 4  )
							    this.table7.add(arrayToken.get(i).getValue()) ; // add the Number to table7
							} //
							else { // It's INTEGER
							  if ( arrayToken.get(i).getTable() == 3   ) {
								this.table7.add(arrayToken.get(i).getValue()) ; // add the Integer to table7
							  //System.out.println("Array Value is " + arrayToken.get(i).getValue());
							  }
							} //
						} else if ( state ==2) { // it's char array
							  if ( arrayToken.get(i).getTable() == 5   )
								this.table7.add(arrayToken.get(i).getValue()) ; // add the Char to  table 7
						} // else if


				  } // for


                  
				  tempToken.add(arrayToken.get(0)) ; // Put the TokenInfo to ArrayList
			      this.makeTable5(arrayToken.get(0)); // make the Identifier to table5
				  Quadruple tempQuadruple = new Quadruple(arrayToken.get(0).getValue(),tempToken) ; //add to the Identifier to QuadRuple,arrayToken.get(0)-->Identifier TokenInfo
				  this.myQuadruple.add(tempQuadruple) ; // add to the Quadruple table
				  this.curQuadruple++ ; // current Quadruple Update
				  //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
		          //for ( int i = 0 ; i < arrayToken.size() ; i++ ) System.out.print(arrayToken.get(i).getValue());
		            //System.out.println("Enter the makeArray");
				  return true ;
			  }// if
			  else {
				  this.errorType = this.errorType + arrayToken.get(0).getValue()  + " is not the Identifier\n" ;
				  return false ;
			  } // else

		} // if
			else {
				this.errorType = this.errorType + "The" + this.type[type--] + " amount is not Correct \n" ;
				return false ;
			} // else

	} // makeArray


	public boolean isMulArray( ArrayList<TokenInfo> addToken,int type,TokenInfo subroutine) { // make the Array(be careful with bug ),decide it's which type of Array and its syntax is correct or not 1. call makeArray 2. call by makeVariable, makeSubroutine,makeStatement,makeIf
       int isType = 0, isDot = 0, isBracket = 0  ; // Type is Amount of Identifier,REAL,INTEGER,BOOLEAN,CHARACTER
		int duplicated = 0 ; // subroutine duplicated
		 int tempCurQuadrupleNumber  = 0; // use to safe subroutine Pointer
		 int lastSubroutine = this.subrountine ; // original subroutine Pointer
		for ( int i = 0 ; i < addToken.size() ; i++) {
			//System.out.println(addToken.get(i).getValue());
			if ( addToken.get(i).getTable() == 5 ) isType++ ; // it's Identifier
			if (  addToken.get(i).getTable() == 1 && addToken.get(i).getEntry() == 3  ) // eg: ) next Delimiter is not null
			  if (  i+1 < addToken.size() && addToken.get(i+1) != null  )
				  if ( addToken.get(i+1).getTable() == 1 && addToken.get(i+1).getEntry() == 11  ) // eg: ),
				isDot++ ;
			if ( addToken.get(i).getTable() == 1 && addToken.get(i).getEntry() == 2  ) // eg: )
					isBracket++ ;
		} // for

		if ( (isType - isDot == 1 && isDot != 0  && isBracket == isType ) || (  isDot == 0 && isType ==1&& isBracket == isType  ) ) { // condition1: A(1)  condition2: B(12,13)
			int  j = 0  ;
			while ( j < addToken.size() ){
			  if ( addToken.get(j).getTable() == 5  ) {	 // It's Identifier
				  //System.out.println(addToken.get(j).getValue()); 
				  ArrayList<TokenInfo> arrayToken = new ArrayList<TokenInfo>() ; // single array eg:  A(1)    or    B(1,2,3,4)
				  arrayToken.add(addToken.get(j)) ; // Add the Identifier TokenInfo to ArrayList
				  j++ ;

				  if ( addToken.get(j).getTable() == 1 && addToken.get(j).getEntry() == 2 ) { // if the TokenInfo is "(" eg: A ( 1,2)
					  
                     boolean stop = false ;
					 while ( stop == false  ) { // If the TokenInfo is ")", than break
						 if (addToken.get(j).getTable() == 1  &&  addToken.get(j).getEntry() == 3 ) stop = true ;
						 //System.out.println(addToken.get(j).getValue());
						 arrayToken.add(addToken.get(j)) ; // add TokenInfo arrayToken to let makeArray do function
						 j++ ;
					 } // while

					 
                    //for ( int i = 0 ; i < arrayToken.size() ; i++ ) System.out.print(arrayToken.get(i).getValue());
                    //System.out.println("Enter isMulArray");

					 if ( j == 0 && subroutine != null ) { // If it's first array and it's subroutine Statement
						 //System.out.println("Enter Array Subroutine");
						 tempCurQuadrupleNumber = this.curQuadruple+ 1 ; // add the pointer to subroutine
							for ( int i = 0 ; i < this.table5.size() ;i++  ) // Decide the subroutine it's Duplicated or not
							  if ( this.table5.get(i) != null &&  this.table5.get(i).getValue().equals(subroutine.getValue())) duplicated++ ;

							if ( duplicated == 0) {	// not the duplicated
							    subroutine.setPointer(tempCurQuadrupleNumber); // set the Current Subroutine to next Quadruple Statement
							    this.makeTable5(subroutine); // add Subroutine to Statement


							    for ( int i = 0 ; i < this.table5.size() ;i++  ) // find out the subroutine location in table5
							      if ( this.table5.get(i) != null && this.table5.get(i).getValue().equals(subroutine.getValue() ))
							    	  this.subrountine = i ; // change the current subroutine to next Quadruple

							} // if
							else {
								this.errorType = this.errorType + " Subroutine Identifier is duplicated \n" ;
								return false ;
							} // else
					 } // if ")"
					 
					 if ( makeArray(arrayToken,type)  == false ) { // true will continue
						 if ( subroutine != null) { // if it's subroutine Statement need to change the Value have been Modify
						   this.subrountine = lastSubroutine ; // set the subroutine number to the original value
						    for ( int i = 0 ; i < this.table5.size() ;i++  ) // find out the subroutine  location in table 5
							      if ( this.table5.get(i).getValue().equals(subroutine.getValue() ))
							    	  this.table5.remove(i) ;	//Delete it
						 } // if
						 return false ;
					 } // if

					 
					 //System.out.println(addToken.get(j).getValue());
					 if ( j < addToken.size() && addToken.get(j).getTable() == 1 && addToken.get(j).getEntry() == 11  ) { // Not Stop from dot move to next Identifier
						 //System.out.println("Keep Doing Array");

					 } // if
					 else {
					   return true ; // only one Array --> A(12)
					 }//else
				  } //  if
				  else {
					  this.errorType = this.errorType + addToken.get(j).getValue()  + " is not ( \n" ;
					  return false ;

				  } //

			  } // if
			  else {
				this.errorType = this.errorType +  addToken.get(j).getValue()   +" is not Identifier in Array \n" ;
				return false ;

			  } // else


			  j++ ;
			} // while


		} // if �ŦX��k
		else {
			this.errorType = this.errorType + "the amout of Array not correct \n" ;
			return false ;
		} //



		return true;


	} // isMulArray()


	public boolean makeProgram( ArrayList<TokenInfo> tempToken, int start) { // make the Program Statement 1. call by makeStatement makeIf

		if ( tempToken.get(start+1).getTable() == 5 ) {// eg: PROGRAM A1 ; -->second TokenInfo is Identifier

			if ( tempToken.get(start+2).getTable() == 1
				  && tempToken.get(start+2).getEntry() == 1 ) { // is ";"
				   TokenInfo tempTokenInfo  = new TokenInfo() ;
					int duplicated = 0 ; // Decide the PROGRAM Identifier is Duplicated or not
					for ( int i = 0 ; i < this.table5.size() ;i++  )  // find out the Duplicated
					  if (  this.table5.get(i) != null && this.table5.get(i).getValue().equals(tempToken.get(start+1).getValue())&&this.table5.get(i).getType()==0) duplicated++ ;


				   if ( duplicated == 0) { // It's not Duplicated Program
					   //this.curQuadruple++ ; // current QuadRuple number add one
					   tempToken.get(start+1).setPointer(this.curQuadruple+1); // add the Program pointer to current Quadruple number
					   //System.out.println(tempToken.get(start+1).getPointer() + "TESSSSSSSSST");
					   this.makeTable5(tempToken.get(start+1)); // Add the Program Identifier to Table5



					   for ( int i = 0 ; i < this.table5.size() ; i++)  { // find out the Program Identifier in the table5 location
						   if ( this.table5.get(i) != null && tempToken.get(start+1).getValue().equals(this.table5.get(i).getValue()) == true ){
							   this.subrountine = i; // location add 1 update the subroutine
						   } // if
					   }//for

					   //System.out.println(this.subrountine);
				      return true ;
				   } //
				   else {
						this.errorType = this.errorType + " Subroutine Identifier is duplicated \n" ;
				   } //


			} // if is <Identifier> and ";"
			else {
				errorType = errorType +  tempToken.get(start+2).getValue()  +" Is not ; \n" ;
			} //

		} //
		else {
			errorType = errorType +  tempToken.get(start+1).getValue()  +" Is not Identifier \n" ;
		} //

		return false;
	} // boolean isProgram



	public boolean makeVariable( ArrayList<TokenInfo> tempToken, int start,int isVarOrDim,TokenInfo subroutine) { // make the Variable Statement, Dimension Statement, Subroutine Statement 1.Call isMultiVar, isMulArray, makeLabel 2. call by makeSubroutin,makeStatement,makeIf
		start++ ; //��U�@��Token is----> DataType VARIABLE INTEGER:X,Y,I;
		ArrayList<TokenInfo> addToken = new ArrayList<TokenInfo>() ; // add to Quadruple table
		int  type = whichType(tempToken.get(start).getValue()) ; // which type is Datatype call WhichType()
		//System.out.println( " The DataType in Variable is "+ type);

		if (  type  != 0 ){ //if is <DATATYPE>
			start++ ; // ����":"
		  if ( tempToken.get(start).getTable() == 1 && tempToken.get(start).getEntry() == 12  ) {// if is ":"

			  start++ ; //����Identifier

            int f= 0 ;
            
			if (tempToken.get(tempToken.size()-1).getTable() == 1 && tempToken.get(tempToken.size()-1).getEntry() == 1 ) { // if Last TokenInfo ";"
				for ( int i = start ; i < tempToken.size()-1 ; i++ ) {  // AfterDatatype to ";"Token eg: X,Y,Z
					  addToken.add(tempToken.get(i)) ; //put it into addToken(To add to Quadruple table)
					  //System.out.println(addToken.get(f).getValue());
					  f++ ;
				}
				
				
				if ( isVarOrDim == 0 ) { // VARIEABLE is 0
					if ( type  == 5 ) { // DATATYPE is LABEL
						addToken.add(tempToken.get(tempToken.size()-1)) ;
						if ( makeLabel( addToken,-1,false,subroutine )  == false ) // use makeLabel to add Label Identifier to Quadruple-->onlyLabel is false
							errorType = errorType +  "Is not  correct" + this.type[5] + " Variable \n" ;
						else
							return true ;
					} // is Label
					else if ( type == 1){ // DATATYPE is ARRAY
						if ( isMulArray( addToken,type,subroutine)  == false )
							errorType = errorType +  "Is not  correct" + this.type[0] + " Varieable  \n" ;
						else
							return true ;
					} else { // DATATYPE is BOOLEAN, CHARACTER, INTEGER, REAL
						if ( isMultiVar( addToken, type,subroutine )  == false ) // use isMultiVar to add DATATYPE Identifier to Quadruple
							errorType = errorType +  "Is not  correct" + this.type[type--] + " Varieable \n" ;
						else
							return true ;
					} // else

				} // if
				else if (isVarOrDim == 1  ) { // DIMENSION is 1
					if ( isMulArray( addToken,type,subroutine )  == false )
						errorType = errorType +  "Is not  correct" + this.type[0] + " Dimension \n" ;
					else
						return true ;
				} // else if



			} //
			else {
				errorType = errorType + tempToken.get(tempToken.size()-1).getValue()  +" Is not ; \n " ;
			} // else




		  } // if is ":" and last Token is ";"
		  else {
			  errorType = errorType + tempToken.get(start).getValue()   +  "Is not :\n " ;
		  } // else

		}//if is <DATATYPE>
		else {
			errorType = errorType +  tempToken.get(start).getValue()   +  "Is not DATATYPE\n" ;
			return false;
		} // else  not <DATATYPE>



		return false;

	} // boolean isVariable


	public boolean makeLabel(ArrayList<TokenInfo> tempToken, int start,boolean onlyLabel,TokenInfo subroutine) { // make the label(Need to deal with forward Reference eg: L91 ENS--> Deal in the makeQuadruple Function) 1.Call by makeVariable,makeStatement,makeIf,other using DataType Function
		start++ ;
		int isType = 0, isDot = 0 ;
		int duplicated = 0 ; // subroutine duplicated
		int tempCurQuadrupleNumber  = 0; // use to safe subroutine Pointer
		int lastSubroutine = this.subrountine ; // original subroutine Pointer

		if (tempToken.get(tempToken.size()-1).getTable() == 1 && tempToken.get(tempToken.size()-1).getEntry() == 1 ) { // if is ";"
			for ( int i = start ; i < tempToken.size() ; i++) { // dot and Identifier amount is correct or not
				if ( tempToken.get(i).getTable() == 5 ) isType++ ;
				if (tempToken.get(i).getTable() == 1 && tempToken.get(i).getEntry() == 11 ) isDot++ ;
			} // for


			if ( (isType - isDot == 1 && isDot != 0 ) || (  isDot == 0 && isType ==1 )) { // condition1: X,Y,Z condition2: X
				  for ( int i = start ; i < tempToken.size() ; i++ ) { // add Identifier to Quadruple
					  if ( tempToken.get(i).getTable() == 5 )  { // It's Identifier


						if ( i == 0 && subroutine != null ) { // If it's first array and it's subroutine Statement
						  tempCurQuadrupleNumber = this.curQuadruple+1 ; // add the pointer to subroutine
							for ( int j= 0 ; j < this.table5.size() ;j++  ) // Decide the subroutine it's Duplicated or not
							  if (  this.table5.get(j) != null && this.table5.get(j).getValue().equals(subroutine.getValue())) duplicated++ ;

							   if ( duplicated == 0) {	// not the duplicated
								 subroutine.setPointer(tempCurQuadrupleNumber); // set the Current Subroutine to next Quadruple Statement
								 this.makeTable5(subroutine); // add Subroutine to Statement


								 for ( int j = 0 ; j < this.table5.size() ;j++  ) // find out the subroutine location in table5
						           if (  this.table5.get(j) != null && this.table5.get(j).getValue().equals(subroutine.getValue() ))
								     this.subrountine = j+ 1 ; // change the current subroutine to next Quadruple

							    } // if
							    else {
								  this.errorType = this.errorType + " Subroutine Identifier is duplicated \n" ;
								  return false ;
								} // else
						 } // if






						tempToken.get(i).setSubroutine(this.subrountine); // set subroutine
						tempToken.get(i).setType(5); // set type is Label
						tempToken.get(i).setTrue(); // set it is  Forward Reference(deal in makeQuadruple)
					    ArrayList<TokenInfo> templateToken = new ArrayList<TokenInfo>() ; // templateToken is ArrayList because my Quadruple's quadruple tokenInfo is ArrayList
					    templateToken.add(tempToken.get(i)) ; // add TokenInfo to ArrayList

					    this.makeTable5(tempToken.get(i)); //add TokenInfo to Table5

					    Quadruple tempQuadruple = new Quadruple() ;

					    if ( onlyLabel == true ) { // if only have label need to add the forward reference
					      tempQuadruple = new Quadruple(this.label + " " + tempToken.get(i).getValue(),templateToken) ;
					    }//if
					    else {
					      tempQuadruple = new Quadruple(tempToken.get(i).getValue(),templateToken) ;
					    }//else

						this.myQuadruple.add(tempQuadruple) ;
					    this.curQuadruple++ ;;
					    System.out.println("Currenct QUadruple number = " + this.curQuadruple);
					  } // if
				  } // for
				} // if �ŦX��k
				else {
					this.errorType = this.errorType + "The Label" + " amount is not Correct \n" ;
					return false ;
				} //

		} //
		else {
			errorType = errorType + tempToken.get(tempToken.size()-1).getValue()   + "Is not ; \n " ;
			return false ;
		} // else

	    return true  ;

	} // makeLabel


	public boolean makeGto( ArrayList<TokenInfo> tempToken, int start,boolean isIf ) { // Make the GTO Statement( Need to deal with forward Reference-->deal with makeQuadruple Function ) 1.Call by makeStatement, makeIf
		int init = start ; // record the start location
		start++ ; // move to next TokenInfo
		if (tempToken.get(tempToken.size()-1).getTable() == 1 && tempToken.get(tempToken.size()-1).getEntry() == 1 ) { // If the last TokenInfo it's ";"
		  if (  tempToken.get(start).getTable() == 5 ) { // If it's Identifier TokenInfo
		    ArrayList<TokenInfo> templateToken = new ArrayList<TokenInfo>() ;  // make A new ArrayList to add to Quadruple
		    templateToken.add(tempToken.get(init)) ; //eg: GTO  init = 0
		    Quadruple tempQuadruple = new Quadruple() ;

		    TokenInfo forward = new TokenInfo(tempToken.get(start).getValue(),6,-1) ; // eg: L91, set -1 to the pointer(Need to deal with Forward Reference in makeQuadruple)
		    templateToken.add(forward) ;
		    if ( isIf == false )
		      tempQuadruple = new Quadruple(this.label+  " " +tempToken.get(init).getValue() +  " "+tempToken.get(init+1 ).getValue(),templateToken,true) ; //set the forward Reference Boolean to True(Need to deal with Forward Reference in makeQuadruple)
		    else 
		      tempQuadruple = new Quadruple(tempToken.get(init).getValue() + " " +tempToken.get(init+1 ).getValue(),templateToken,true) ; //set the forward Reference Boolean to True(Need to deal with Forward Reference in makeQuadru	
		    this.myQuadruple.add(tempQuadruple) ;// add to Quadruple table
		    this.curQuadruple++ ; // update current Quadruple number
		    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
		    return true ;
		  }// if
		  else{
			  errorType = errorType + tempToken.get(start).getValue()  + "Is not Identifier\n " ;
		  }// else
		}//if
		else {
			errorType = errorType +  "Is not :\n " ;
		} //


		return false;

	} // makeGto


	public boolean makeSubroutine(ArrayList<TokenInfo> tempToken, int start ) { // make the subroutine( 1.Variable and Program Statement Combine 2.Need to change the this.subroutine)1. Call makeVariable(call isArray,isMultiVar, isMulArray)2. Call by makeStatement,makeIf
		start++ ; //move to next TokeInfo
		if (tempToken.get(start).getTable() == 5 ) { // If it's Identifier Info-->SUBROUTINE A3(INTEGER:X,Y,K)
			start++ ; // Move to next TokenInfo
			if ( tempToken.get(start).getTable() == 1 && tempToken.get(start).getEntry() == 2  ) { // if It's "("
			  if (tempToken.get(tempToken.size()-2).getTable() == 1 && tempToken.get(tempToken.size()-2).getEntry() == 3) { // if It's ")"
				  start++ ;// Move to next TokenInfo
				  ArrayList<TokenInfo> templateToken = new ArrayList<TokenInfo>() ; // ArrayList use to add to Quadruple
				  TokenInfo tempVar = new TokenInfo("VARIABLE",2,25) ; // Add this because later will call makeVariable Function
				  TokenInfo tempSubroutine = tempToken.get(1) ; // the Subroutine Identifier eg: SUBROUTINE  "A3"  (INTEGER:X,Y,K) ;
				  templateToken.add(tempVar) ; // add to ArrayList
				  boolean stop = false ;
 				  while (  stop == false) { // add the TokeInfo between "(" and ")" eg: (INTEGER:X,Y,K)
 					  templateToken.add(tempToken.get(start)) ;
					  start++ ;
					  if (  tempToken.get(start).getTable() == 1  && tempToken.get(start).getEntry() == 3) stop = true ;
				  } // while

 				  				  
				  if (tempToken.get(tempToken.size()-1).getTable() == 1 && tempToken.get(tempToken.size()-1).getEntry() == 1) { // If last TokenInfo is ";"
					  templateToken.add(tempToken.get(tempToken.size()-1)) ; // add the ";" to the ArrayList
	 				  //for ( int i = 0 ; i < templateToken.size() ; i++ ) System.out.print(templateToken.get(i).getValue() + " ");
	 				  //System.out.println();
					  if ( this.makeVariable(templateToken, 0,0,tempSubroutine) == true ) { // start = -1, varAndDim = 0, eg: templateToken--> INTEEGER:X,Y,K ; tempSubroutine-->A3
						  return true ;
					  } // if
					  else {
						  errorType = errorType +  "this is not Correct Variable \n " ;
					  } // else
				  } // if
				  else {
					  errorType = errorType +  tempToken.get(tempToken.size()-1).getValue()  + "Is not ; \n " ;
				  } //




			  } // if
			  else {
				  errorType = errorType +  tempToken.get(tempToken.size()-2).getValue()  + "should have ) \n " ;
			  } // else

			}// if
			else {
				errorType = errorType +  tempToken.get(start).getValue()  + "Is not ( \n " ;
			} // else
		} //
		else {
			errorType = errorType +  tempToken.get(start).getValue()  + "Is not Identifier\n " ;
		} //

		return false ;
	} // makeSubroutine




	public boolean isCall(ArrayList<TokenInfo> addToken,TokenInfo call,TokenInfo callId) { // call is Call TokenInfo, callID is callStatemnet's Identifier TokenInfo, This Function add Quadruple and add Call TokenInfo to Table7 1.Call by MakeCall
		int isType = 0, isDot = 0 ; // the amount of Datatype and Dot
		String table7String = "" ; //
		for ( int i = 0 ; i < addToken.size() ; i++) {
			if ( addToken.get(i).getTable() == 3 ||   addToken.get(i).getTable() == 4 || addToken.get(i).getTable() == 5  ) isType++ ; // it's REAL, INTEGER, IDENTIFIER then isType plus one
			if ( addToken.get(i).getTable() == 1 && addToken.get(i).getEntry() == 11 ) isDot++ ; // It's Dot
			 table7String = table7String + addToken.get(i).getValue() ; // eg: CALL A3(  "W, 136,A,57.9 ")
		} // for

	    //System.out.println("table7String = " + table7String + " isDot "  + isDot + " isType " + isType );
		if ( (isType - isDot == 1 && isDot != 0 ) || (  isDot == 0 && isType ==1 )) { //eg: condition1 CALL S1(W)  condition2 CALL S1(W,136,A,57.9)
			ArrayList<TokenInfo> templateToken = new ArrayList<TokenInfo>() ; // ArrayList used to add to Quadruple

			 this.table7.add(Integer.toString(isType)) ;//How many Value eg: CALL A3(  "W, 136,A,57.9 ")--->isType value is 4, add to the table7
			 TokenInfo tempTable7 = new TokenInfo(   "(" + table7String + ")", 7, this.table7.size()  ) ; // eg: (  W, 136,A,57.9 ),the table is 7,the Entry is  table7 Pointer
             //System.out.println(callId.getEntry()); have Bug 
			 
	
			  //System.out.println(this.subrountine);
			 for ( int i = 0 ; i < addToken.size() ; i++ ) { //
				 if ( addToken.get(i).getTable() == 3 ||   addToken.get(i).getTable() == 4 || addToken.get(i).getTable() == 5  ) { // It's IDENTIFIER, REAL, INTEGER

				   if (addToken.get(i).getTable() == 5  ) { // It's Identifier need to check it's Subroutine
		
					  for ( int j = 0 ; j < this.table5.size();j++) { // Findout the the Identifier have current subroutine in Table5
							if ( this.table5.get(j) != null && addToken.get(i).getValue().equals(this.table5.get(j).getValue() )== true  && this.table5.get(j).getSubroutine() == this.subrountine) { // it's 1.same Identifier 2.current subroutine
								addToken.set(i,this.table5.get(j)) ; // add the Identifier(in the table5) to operand
								//System.out.println(this.table5.get(j).getValue() + "    DM  " + this.table5.get(j).getSubroutine());
							}// if
					  } // for		      
				   } // if

				   
		
				
				
				   this.table7.add(Integer.toString(addToken.get(i).getTable())) ; // add It's Table to Table7
				   this.table7.add(Integer.toString(addToken.get(i).getEntry())) ; // add It's Entry to Table7
				 } // if

			 } //for

            
	
			 
			 
			templateToken.add(call) ; //add the call Statement
			templateToken.add(callId) ; // add Call Identifier 	be careful I'm not add this into Table5
			//callId.setEntry(16); // A3
			templateToken.add(tempTable7) ; // add eg: (X,Y,Z)

		    Quadruple tempQuadruple = new Quadruple(this.label + " " + call.getValue() + " " + callId.getValue() + tempTable7.getValue(),templateToken,true) ; // add to Quadruple Table
		    //System.out.println(this.label + call.getValue() + " " + callId.getValue() + tempTable7.getValue());
		    this.myQuadruple.add(tempQuadruple) ;
		    this.curQuadruple++ ; // update current Quadruple Number
		    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
		    return true ;
		}//
		else {
			this.errorType = this.errorType +  "Variable amount is not Correct \n" ;
		} //



		return false;

	} // isCall


	public boolean makeCall(ArrayList<TokenInfo> tempToken, int start) { // make the Call Statement 1. Call isCall(to do add TokenInfo to Table7 and add to Quadruple) 2. Call by MakeStatment, Make If 3.Be careful of isCall Function
		start++ ; // move to next TokenInfo --> CALL "A3"(I,J,K)
		if (tempToken.get(start).getTable() == 5 ) { // If it's Identifier TokenInfo --> CALL A3(I,J,K)
			start++ ; // move to next TokenInfo  --> CALL A3"("I,J,K)
			if ( tempToken.get(start).getTable() == 1 && tempToken.get(start).getEntry() == 2  ) { // If it's "("
				  if (tempToken.get(tempToken.size()-2).getTable() == 1 && tempToken.get(tempToken.size()-2).getEntry() == 3) { // if the last two TokenInfo  is ")"
				    if ( tempToken.get(tempToken.size()-1).getTable() == 1 && tempToken.get(tempToken.size()-1).getEntry() == 1) { // If the last TokenInfo is ";"
					 start++ ;// move to nextTokenInfo-->--> CALL A3("I",J,K)
				      ArrayList<TokenInfo> templateToken = new ArrayList<TokenInfo>() ; // Arraylist use to add to Quadruple
				      boolean stop =false ;
	 				  while (stop == false) { // add TokenInfo between "(" and ")" eg: X,Y,Z
						  templateToken.add(tempToken.get(start)) ;
						  start++ ;
						  if (tempToken.get(start).getTable() == 1  && tempToken.get(start).getEntry() == 3) stop = true ;
					  } // while
                         //for ( int i = 0; i < templateToken.size() ; i ++ ) System.out.print(templateToken.get(i).getValue());
                         //System.out.println("   " +   tempToken.get(0).getValue() +  " " +tempToken.get(1).getValue());
	 				  
	 				  
					  if (isCall(templateToken, tempToken.get(0), tempToken.get(1))  == true) // tempToken.get(0)-->CALL Statement, tempToken.get(1)-->CALL Identifier
						  return true ;
					  else
						  errorType = errorType +  tempToken.get(tempToken.size()-2).getValue()  + "not Correct Call Statement \n " ;
				    } //
				    else {
				    	 errorType = errorType +  tempToken.get(tempToken.size()-1).getValue()  + "should have ; \n " ;
				    } // else


				  } //
				  else {
					  errorType = errorType +  tempToken.get(tempToken.size()-2).getValue()  + "should have ) \n " ;
				  } // else
			} //if
			else {
				errorType = errorType +  tempToken.get(start).getValue()  + "Is not ( \n " ;
			} // else

		}//
		else {
			errorType = errorType +  tempToken.get(start).getValue()  + "Is not Identifier\n " ;
		} //



		return false ;
	} //




	public TokenInfo ReversePolish(ArrayList<TokenInfo> prevEqual, ArrayList<TokenInfo> nextEqual,boolean dot,int isBracket,boolean isIf,boolean haveBracket ) { // Do the �B�� 1.call by makeAssignment(Notice the is the If statement or not), makeIf(be careful) 2.This Function need to Debug
       /*************************************************************************Variable Announce*******************************************************************/
		java.util.Stack<TokenInfo> operand = new java.util.Stack<TokenInfo>(); // operand Stack
		java.util.Stack<TokenInfo> operator = new java.util.Stack<TokenInfo>(); // operator Stack
		    if ( haveBracket == false) // if have Bracket then  prevEqual dont have Identifier
			  operand.push(prevEqual.get(0)) ; // push prevEqual first Identifier to operand stack eg: "A"(X) = B + 2.7
			operator.push(prevEqual.get(prevEqual.size()-1)) ; // add "=" to operator stack



		int k = 0 ; // Stop Condition
		boolean bDot = false ; // have Dot is Array eg: A(X) = "B(X,Y)" + 2.7
		boolean error = false ; // not useing
		boolean onlyOneBracket = false ; // eg: Z = Z + (Z)
      /****************************************************************************************************************************************************************/

	   /********************************************************Reverse Polish FirstTime(Add the Operand and Operator)***********************************************************************/
	    while ( k < nextEqual.size()  && error == false ) {	// push all of nextEqual Value
    		//System.out.println(nextEqual.get(k).getValue());
    		//System.out.println(isBracket + "Bracket amount");
	    	if ( nextEqual.get(k).getTable() == 1  && nextEqual.get(k).getEntry() >=2 && nextEqual.get(k).getEntry() <=12 && nextEqual.get(k).getEntry() != 4  ) {// it's operand and the operand it's not "="
	    		int curEntry = 0 ; // nextEqual value,
	    		int lastEntry = 0 ; // the top of Operator stack

	    		if ( nextEqual.get(k).getEntry() == 9 ) curEntry = 5 ;// nextEqual operator is "^" give it level = 5
	    		if ( nextEqual.get(k).getEntry() == 7 || nextEqual.get(k).getEntry() ==  8 ) curEntry = 4 ;// nextEqual operator is "*" "/" give it level = 4
	    		if ( nextEqual.get(k).getEntry() == 5 || nextEqual.get(k).getEntry() ==  6 ) curEntry = 3 ;// nextEqual operator is "+"	"-" give it level = 3
	    	    if (nextEqual.get(k).getEntry() == 2) {	// nextEqual operator is "("   give it level = 2
	    				isBracket++ ;
	    				curEntry = 13 ;
	    	    } // is bracket(

	    		if ( operator.peek().getEntry() == 9 ) lastEntry = 5 ;// Top Stack of operator is "^" give it level = 5
	    		if ( operator.peek().getEntry() == 7 || operator.peek().getEntry() ==  8 ) lastEntry = 4 ;// Top Stack of operator is "*" "/" give it level = 4
	    		if ( operator.peek().getEntry() == 5 || operator.peek().getEntry() ==  6 ) lastEntry = 3 ;// Top Stack of operator is "+"	"-" give it level = 3
	    	  if ( operator.peek().getEntry() == 2) {	// Top Stack of operator  is "("   give it level = 2
	    	    	lastEntry = 2 ;
	    	  } // is bracket(
					if ( operator.peek().getEntry() == 4 ) lastEntry = 1 ;// Top Stack of operator is "=" give it level = 1



	    		if ( bDot == true ) { // is dot( is Array) eg: A(x) = B(I,J) + 2.7 ;
	    			if ( nextEqual.get(k).getEntry() == 11 || nextEqual.get(k).getEntry() ==  3 ) {	// If nextEqual is ")" or ","
	    			}//
	    			else {
							errorType = errorType +    "Not correct Array \n " ;
	    				return null ; // return false
	    			}
	    		}

    			

	    		if (  nextEqual.get(k).getEntry() == 11 ){
	    			bDot = true ; // If nextEqual have "," or "A(B)"
	    			if (nextEqual.get(k).getEntry() == 11) curEntry = 11 ; // biggest Entry ,
	    		} //
	    		else if ( k-1 >= 0 ) {
	    			if (nextEqual.get(k-1).getTable() == 5 &&  nextEqual.get(k).getEntry() == 2)  {
		    			bDot = true ; // If nextEqual have "," or "A(B)"
		    			if (nextEqual.get(k).getEntry() == 11) curEntry = 11 ; // biggest Entry ,
	    			}//
	    			
	    		}// else if 
	    		ArrayList<TokenInfo> addToken = new ArrayList<TokenInfo>() ; // ArrayList use to add to Quadruple




    			if ( nextEqual.get(k).getEntry() == 3  ) {// If nextEqual is ")", need to deal with Array firt
    				int leftBracket = 0 ; // left bracket
    				if ( bDot == false ){//it's not dot Array eg: A(X) = a + 12 ;
    				  int temp = this.curQuadruple ; //record currenct Quadruple number, If it's have error need to delete the CurQuadruple
					  ArrayList<TokenInfo> tempPrevEqual = new ArrayList<TokenInfo>() ;
				      ArrayList<TokenInfo> tempNextEqual = new ArrayList<TokenInfo>() ;
				      if ( operator.peek().getEntry() == 2 ) { // only have(Z)
				    	  leftBracket++ ; // leftBracket need to be 1 so can Enter into Condition
				    	  tempNextEqual.add(operator.get(operator.size()-1));// get"("
				      } //
    				  while(operator.peek() !=null&& operator.peek().getEntry() != 2) {	// If the top operator stack is "(" break the loop
								if ( tempNextEqual.size() == 0 )  { // If tempNextEqual is null
									tempNextEqual.add(operand.pop()); //add operand 
									tempNextEqual.add(0,operator.pop()) ;//add operator
								}
								else {
									tempNextEqual.add(0,operand.pop());
									tempNextEqual.add(0,operator.pop()) ;
								}//
			    		    if ( operator.peek().getEntry() == 2 && operator.peek() != null ) {
										leftBracket++ ; // 
									}
    				  } // while

    				  
    				  
    				  
    				  if ( leftBracket == 1 ) {
							//isBracket-- ; // isBracket need to miner one because end of bracket
                        if ( tempNextEqual.get(0).getTable() ==1 && tempNextEqual.get(0).getEntry() == 2 ) {// "(Z)"							
							operator.pop(); // pop"("
							tempNextEqual.add(operand.get(operand.size()-1));// get"Z"	may not use						
							haveBracket  = true ;   
							onlyOneBracket = true ; // is  only have one dot (Z)
							isBracket-- ; // isBracket need to miner one because end of bracket
						}//if
						else {
						  operator.pop() ; // pop off "("
						  tempNextEqual.add(0,operand.pop()); // add eg: (J-1)  "J"
						  tempPrevEqual.add(prevEqual.get(prevEqual.size()-1)) ; // add = to TempPrevEqual
						  //for ( int i = 0 ; i < tempNextEqual.size();i++) System.out.print(tempNextEqual.get(i).getValue());
						  //System.out.println("   tempNextEqual");
						  //for ( int i = 0 ; i < tempPrevEqual.size();i++) System.out.print(tempPrevEqual.get(i).getValue());
						  //System.out.println("   tempPrevEqual");
						  TokenInfo tempToken = new TokenInfo();
						  haveBracket = true ; // need to deal with bracket
						  //System.out.println(isBracket + "Bracket amount");
						  isBracket-- ; // isBracket need to miner one because end of bracket
						  tempToken = ReversePolish(tempPrevEqual,tempNextEqual,false,isBracket,isIf,haveBracket) ;
						    if ( tempToken != null ) {
							  operand.add(tempToken) ; // add dot after calculated
							  //System.out.println("Return Bracket Value " + operand.peek().getValue());
							  haveBracket  = false ;
							}//if
						    else {
						      //System.out.println("FUCK UP");
							  for ( ; temp < this.curQuadruple ; temp++) this.myQuadruple.remove(temp--) ;
								this.curQuadruple = this.myQuadruple.size() ;
							  errorType = errorType +    "fail to make the Array = \n " ;
							  return null ;
						    } //else
					
	
						 }//else 
    				  } // if



    				} // if
    				else { // is array operate //Here I only care about one situation B(I,J)=(J-1)*M + I

    					int isDot = 0 ;
    					ArrayList<TokenInfo> tempPrevEqual = new ArrayList<TokenInfo>() ;
    					ArrayList<TokenInfo> tempNextEqual = new ArrayList<TokenInfo>() ;
    					//addToken.add(nextEqual.get(k)) ;
    					//System.out.println(nextEqual.get(k).getValue());
    					if ( operator.peek().getEntry() == 2 ) leftBracket++ ;// eg: A(X)
    					while(operator.peek() != null && operator.peek().getEntry() != 2  ) {
    						if (operator.peek().getEntry() == 11 ) {
    							isDot++ ;
    							operator.pop();// pop ","
    							addToken.add(0,operand.pop()) ; // add I J
    						}
    						
    		
                            if ( operator.peek() != null &&operator.peek().getEntry() == 2  ) leftBracket++ ; // it's "("
    					} // while
                       
						  //System.out.println("YES isDot ZERO" + isDot + leftBracket + operator.peek().getValue());
    					if ( leftBracket ==1 && operator.peek() != null ) {
    					  if (  isDot == 1) { // Two Dimesion
      						isBracket-- ;// bracket miner one because find the "("
      						addToken.add(0,operand.pop()) ;
  	    					addToken.add(0,operator.pop()) ; // add (
  	    					tempPrevEqual.add(operand.pop()); // add Identifier to previousEqual
  	    					//System.out.println(tempPrevEqual.get(0).getPointer());
  	    					addToken.add(nextEqual.get(k)) ; // add )
  	    					TokenInfo tempToken = new TokenInfo( "=",1,4) ; // make =
  	    					tempPrevEqual.add(tempToken) ; // add = to previousEqual
  	    					//for ( int i = 0 ; i < addToken.size();i++) System.out.println(addToken.get(i).getValue());

  	    					
  	    					
  	    					tempNextEqual.add(addToken.get(0)) ; // add ( to nextEqual
  	    					tempNextEqual.add(addToken.get(2)) ; // add J to nextEqual
  	    					tempToken = new TokenInfo( "-",1,6) ;// make-
  	    					tempNextEqual.add(tempToken) ;       // add - to nextEqual
  	    					//for ( int f = 0 ; f < this.tableList.get(2).length ; f++ )
  	    					//	if ( this.tableList.get(2)[f].equals("1")) 
  	    					tempToken = new TokenInfo( "1",3,49) ;// make 1
  	    					tempNextEqual.add(tempToken) ;       // add 1 to nextEqual
  	    					tempNextEqual.add(addToken.get(3)) ; // add ) to nextEqual
  	    					tempToken = new TokenInfo( "*",1,7) ;// make *
  	    					tempNextEqual.add(tempToken) ; // add  * to nextEqual
  	    					
                             
  	    					String numOfColumn = this.table7.get(tempPrevEqual.get(0).getPointer()+1)  ;  // B Column Location    	    					
  	    					//System.out.println(numOfColumn);
  	    					//for ( int f = 0 ; f < this.tableList.get(2).length ; f++ )
  	    						//if ( this.tableList.get(2)[f].equals(numOfColumn))
  	    
  	    					tempToken = new TokenInfo( numOfColumn,3,Integer.parseInt(numOfColumn) - 1  + 49) ;// make M
  	    					tempNextEqual.add(tempToken) ;       // add M to nextEqual

  	    					tempToken = new TokenInfo( "+",1,5) ;// make +
  	    					tempNextEqual.add(tempToken) ; // add  + to nextEqual
  	    					tempNextEqual.add(addToken.get(1)) ; // add I to nextEqual
                            
  	    					//for ( int i = 0 ; i < tempNextEqual.size();i++) System.out.print(tempNextEqual.get(i).getValue());
                            //System.out.println("   TempNextEqual");
  	    					//for ( int i = 0 ; i < tempPrevEqual.size();i++) System.out.print(tempPrevEqual.get(i).getValue());
                            //System.out.println("   TempPrevEqual");
                            

  	    					
  	    					tempToken = ReversePolish(tempPrevEqual,tempNextEqual,true,isBracket,isIf,false) ;

  	    					if ( tempToken != null ) {
										bDot = false ;
										dot = false ;
								
								//System.out.println("Return isDot value " + tempToken.getValue());
  	    						operand.add(tempToken) ; // add dot Statement after calculated
  	    					}else {

  	    						errorType = errorType +    "fail to make the Array = \n " ;
  	    						return null ;
  	    					} //else


    					  }//if
    					  else if (isDot == 0 ) {
    						  

    						  isBracket-- ;// bracket miner one because find the "("
    						  operator.pop() ; // pop "("
    						  TokenInfo tempToken = new TokenInfo( "=",1,4) ; // make =
    						  addToken.add(tempToken) ;
    						  addToken.add(operand.pop()) ;
    						  addToken.add(1,operand.pop()) ;
    						  
    			    		  this.randomNumber++ ;
    			    	      TokenInfo tempRandomNumber = new TokenInfo("T" + Integer.toString(this.randomNumber),0,this.randomNumber) ;
    			    		  addToken.add(tempRandomNumber);
    			    		  
    			    		  Quadruple tempQuadruple = new Quadruple(addToken.get(3).getValue() + addToken.get(0).getValue() + addToken.get(1).getValue() + "("+ addToken.get(2).getValue()+")" ,addToken) ;
    			    		  this.myQuadruple.add(tempQuadruple) ;
    			    		  this.curQuadruple++ ;
    			    		  //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
    			    		  operand.add(tempRandomNumber) ;
    			    		  bDot = false ;
    			    		  
    			    		  //System.out.println("Enter isDot == 0 " + addToken.get(3).getValue() + addToken.get(0).getValue() + addToken.get(1).getValue() + "("+ addToken.get(2).getValue()+")");
    					  }else {
    						 return null ; // Sorry I cant not Caculate not more two dimension Array
    					  } //

    					} //
    					else {
    						errorType = errorType  + "Is not correct Array \n " ;
    						return null ;

    					}
    					/// deal with array
    				} // �n�Ҽ{����ARRAY


    			} //
    			else {
		    		if ( curEntry >= lastEntry || isBracket != 0) { // if isBracket is more than one need to add till ")"
		    			operator.push(nextEqual.get(k)) ;
		    		} else if (curEntry < lastEntry  && isBracket == 0){ // if isBracket more than one need to deal with bigger operator first




		    			addToken.add(operator.pop()) ;
		    			addToken.add(operand.pop()) ;
		    			addToken.add(1,operand.pop()) ;




		    			this.randomNumber++ ;
		    			TokenInfo tempRandomNumber = new TokenInfo("T" + Integer.toString(this.randomNumber),0,this.randomNumber) ;
		    			addToken.add(tempRandomNumber);

		    			//operator.push(nextEqual.get(k)) ;
							k-- ; // move to before
		    			operand.push(tempRandomNumber) ;

		    		    Quadruple tempQuadruple = new Quadruple(addToken.get(3).getValue() +" = " + addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue() ,addToken) ;
		    		    //System.out.println("Enter Have no Bracket normal Operation " + addToken.get(3).getValue() +" = " + addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue());
		    		    this.myQuadruple.add(tempQuadruple) ;
		    		    this.curQuadruple++ ;
		    		    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);


		    		}//
    			} //





	    	} // if



	    	else if (nextEqual.get(k).getTable() == 5 || nextEqual.get(k).getTable() == 3 || nextEqual.get(k).getTable() == 4) {
					if (nextEqual.get(k).getTable() == 5) { // If it's Identifier need to check it's current subroutine or not
						int haveIdentifier = 0 ;
						for ( int j = 0 ; j < this.table5.size();j++) { // Findout the the Identifier have current subroutine in Table5
							if ( this.table5.get(j) != null && nextEqual.get(k).getValue().equals(this.table5.get(j).getValue() )== true  && this.table5.get(j).getSubroutine() == this.subrountine) { // it's 1.same Identifier 2.current subroutine
								operand.add(this.table5.get(j)) ; // add the Identifier(in the table5) to operand
								haveIdentifier++ ;
							}//

							} // for
						if ( haveIdentifier == 0 ) {
							errorType = errorType +   nextEqual.get(k).getValue()  + "should in Table5 \n " ;
							return null ;
						}// if
						
						}// if
					
		
					else {
						operand.add(nextEqual.get(k)) ; // not the Identifier it's Number or Real
					}//
	    	} //
	    	else {

	    		errorType = errorType +   nextEqual.get(k).getValue()  + "Cannot find this value \n " ;
	    		return null ;
	    	} //



	    	k++ ;
	    } // while

	   /****************************************************************************************************************************************************************************/
		  //for ( int i = 0 ; i < operand.size();i++) System.out.print(operand.get(i).getValue() + " ");
		  //System.out.println( " Operand Value");
		  //for ( int i = 0 ; i < operator.size();i++) System.out.print(operator.get(i).getValue() + " ");
		  //System.out.println( " Operator Vaue");

	   /****************************************************************************ReversePolishSecondTime( Calculate the operand and operator)************************************/

	    ArrayList<TokenInfo> addToken = new ArrayList<TokenInfo>() ;
	    Quadruple tempQuadruple = new Quadruple() ;
	    if ( operator.size() == 1 && prevEqual.size() >= 2 ) {// only have "="
	      	addToken.add(operator.pop()) ;
	      	addToken.add(operand.pop()) ;
	      	addToken.add(operand.pop()) ;

	      	if ( dot == true) { // is dot not using
	      		this.randomNumber++ ;
	      		TokenInfo tempToken = new TokenInfo("T" + Integer.toString(this.randomNumber),0,this.randomNumber) ;
	      		addToken.add(tempToken) ;
	      		tempQuadruple = new Quadruple(addToken.get(3).getValue()  + addToken.get(0).getValue() +")" + addToken.get(1).getValue() + "(" + addToken.get(2).getValue() +")" ,addToken) ;
		    	this.myQuadruple.add(tempQuadruple) ;
			    this.curQuadruple++ ;
			    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
			    return tempToken ;
	      	} //
	      	else {
		    	if ( prevEqual.size() == 3) { // A(X) = I
			    		addToken.add(prevEqual.get(1)) ;
			    		tempQuadruple = new Quadruple(addToken.get(2).getValue() + "(" + addToken.get(3).getValue() +")" + addToken.get(0).getValue() + addToken.get(1).getValue() ,addToken) ; //A(X) = I
			    		//System.out.println("Enter size ==1 preEqual ==3 is " + addToken.get(2).getValue() + "(" + addToken.get(3).getValue() +")" + addToken.get(0).getValue() + addToken.get(1).getValue());
		    	} //
		    	else if ( prevEqual.size() == 2){// I =2
				    tempQuadruple = new Quadruple(addToken.get(2).getValue() + addToken.get(0).getValue() + addToken.get(1).getValue(),addToken) ;// J = T1
				    //System.out.println("Enter size ==1 preEqual ==2 is " + addToken.get(2).getValue() + addToken.get(0).getValue() + addToken.get(1).getValue());
		    	} // else
		    	else{
		    		return null ; // sorry i can Calculate it
		    	} //


		    	this.myQuadruple.add(tempQuadruple) ;
			    this.curQuadruple++ ;
			    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
			    return prevEqual.get(0) ;
	      	} // is  not dot




	    } //
	    else if ( operator.size() == 2 && ( prevEqual.size() >= 2 || haveBracket == true ) ){ // have two Operator
    		addToken.add(operator.pop()) ; // add operator
    		addToken.add(operand.pop()) ; // add first Stack
    		addToken.add(1,operand.pop()) ; // add in front of first stack
    		if ( haveBracket  == true) { // It's is dot Bracket  (J-2), (Z)
   	    	    this.randomNumber++ ;
	    	    TokenInfo tempRandomNumber = new TokenInfo("T" + Integer.toString(this.randomNumber),0,this.randomNumber) ;
	    	    addToken.add(tempRandomNumber);
	    	    tempQuadruple = new Quadruple(addToken.get(3).getValue() + " = "+ addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue() ,addToken) ;
	    	    //System.out.println("Enter Operator == 2 haveBracket " +  addToken.get(3).getValue() + " = "+ addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue() );
			    this.myQuadruple.add(tempQuadruple) ;
			    this.curQuadruple++ ;
			    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
			    if ( onlyOneBracket == true ) { // Z = Z + (Z) ;
			       	operand.push(tempRandomNumber) ; // eg: T7 = Z + Z ; add T7
			       	addToken = new ArrayList<TokenInfo>() ;
			       	
		    		addToken.add(operator.pop()) ; // =
		    		addToken.add(operand.pop()) ; // T7
		    		addToken.add(operand.pop()) ; // Z
		    		tempQuadruple = new Quadruple(addToken.get(2).getValue() +  addToken.get(0).getValue() + addToken.get(1).getValue() ,addToken) ;
		    		//System.out.println("Enter only have one Dot " + addToken.get(2).getValue() +  addToken.get(0).getValue() + addToken.get(1).getValue() );
				    this.myQuadruple.add(tempQuadruple) ;
				    this.curQuadruple++ ;
				    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
		    		return prevEqual.get(0) ;
			    }//
			    else {
		    	    return tempRandomNumber ;	// T1 = J -1 Return T1
			    }// else 

    		}//
    		else {
    	    	if (prevEqual.size() == 3) { //A(K)
        			this.randomNumber++ ;
        			TokenInfo tempRandomNumber = new TokenInfo("T" + Integer.toString(this.randomNumber),0,this.randomNumber) ;
        			addToken.add(tempRandomNumber);
        			operand.push(tempRandomNumber) ;
    			    tempQuadruple = new Quadruple(addToken.get(3).getValue() + " = "+ addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue() ,addToken) ;
    			    //System.out.println("Enter operator = 2 preEqual = 3 FirstStep  " + addToken.get(3).getValue() + " = "+ addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue());
    			    this.myQuadruple.add(tempQuadruple) ;
    			    this.curQuadruple++ ;
    			    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
                    /*********************************************A(K)= T1**************************************************************************/
    			    addToken = new ArrayList<TokenInfo>() ;
    		      	addToken.add(operator.pop()) ; //=
    		      	addToken.add(operand.pop()) ;//T1
    		      	addToken.add(operand.pop()) ;//A
    		      	addToken.add(prevEqual.get(1)) ;//K
    		      	tempQuadruple = new Quadruple(addToken.get(2).getValue() + "(" + addToken.get(3).getValue() +")" + addToken.get(0).getValue() + addToken.get(1).getValue() ,addToken) ;
    		      	//System.out.println("Enter operator = 2 preEqual = 3 SecondStep  " + addToken.get(2).getValue() + "(" + addToken.get(3).getValue() +")" + addToken.get(0).getValue() + addToken.get(1).getValue());
    	    	} //if
    	    	else if (prevEqual.size() == 2) {

    	    	 if ( isIf == false ) { // X = Y + Z     (+,Y,Z,X)
    				    /***********************************************************X=Y+Z*********************************************************************************/
    			      	if ( dot == true) { // is dot  B(I,J) // Different with input
    			      		this.randomNumber++ ; 
    			      		TokenInfo tempToken = new TokenInfo("T" + Integer.toString(this.randomNumber),0,this.randomNumber) ;
    			      		addToken.add(tempToken) ;
    			      		tempQuadruple = new Quadruple(addToken.get(3).getValue() + " = "+ addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue() ,addToken) ;
    			      		//System.out.println("Enter dot== true T4 " + addToken.get(3).getValue() + " = "+ addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue());
    				    	this.myQuadruple.add(tempQuadruple) ;
    					    this.curQuadruple++ ;
    					    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
    					    /**************************************T4 = T3 + I ;***************************************/
    					    operand.push(tempToken) ; // T4 add to operand
    					    addToken = new ArrayList<TokenInfo>() ;
    					    addToken.add(operator.pop()) ;
    			      		addToken.add(operand.pop()) ;
    			      		addToken.add(1,operand.pop()) ;
       			      		this.randomNumber++ ; 
    			      		tempToken = new TokenInfo("T" + Integer.toString(this.randomNumber),0,this.randomNumber) ;
    			      		addToken.add(tempToken) ;
    			      		
    			      		tempQuadruple = new Quadruple(addToken.get(3).getValue()  + addToken.get(0).getValue() + addToken.get(1).getValue() + "(" + addToken.get(2).getValue() +")" ,addToken) ;
    			      		//System.out.println("Enter dot== true  T5" + addToken.get(3).getValue()  + addToken.get(0).getValue() + addToken.get(1).getValue() + "(" + addToken.get(2).getValue() +")" );
    				    	this.myQuadruple.add(tempQuadruple) ;
    					    this.curQuadruple++ ;
    					    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
    					    return tempToken ;
    					    /************************************************T5 = B(T4)**************************************/
    			      	} // if dot false 
    			      	else {
    			      		addToken.add(operand.pop()) ;
    			      		tempQuadruple = new Quadruple(addToken.get(3).getValue()  + " = " + addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue(),addToken) ;
    			      		//System.out.println("Enter operator = 2  prev = 2 is not If  " + addToken.get(3).getValue()  + addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue()  );
    			      	} // else
    	    	 }//
    	    	 else {// x = x + 2
    	    	    this.randomNumber++ ;
    	    	    TokenInfo tempRandomNumber = new TokenInfo("T" + Integer.toString(this.randomNumber),0,this.randomNumber) ;
    	    	    addToken.add(tempRandomNumber);
    	    	    operand.push(tempRandomNumber) ;
    			    tempQuadruple = new Quadruple(addToken.get(3).getValue() + " = "+ addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue() ,addToken) ;
    			    //System.out.println("Enter operator = 2 if FirstOperate "  +addToken.get(3).getValue() + " = "+ addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue()  );
    			    this.myQuadruple.add(tempQuadruple) ;
    			    this.curQuadruple++ ;
    			    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
    			    addToken = new ArrayList<TokenInfo>() ;
    			    addToken.add(operator.pop()) ;// =
    		      	addToken.add(operand.pop()) ; // T1
    		      	addToken.add(operand.pop()) ; // X
    		      	tempQuadruple = new Quadruple(addToken.get(2).getValue() + addToken.get(0).getValue() + addToken.get(1).getValue(),addToken) ;// X=T2
    		      	//System.out.println("Enter operator = 2 if SecondOperate "  + addToken.get(2).getValue() + addToken.get(0).getValue() + addToken.get(1).getValue()) ;


    	    	 }// isIf Statment x = x +1

    	    	}//
    	    	else {
    	    		return null ; // sorry i can Calculate it
    	    	} // else if
	
    		} // else 
    		
		    this.myQuadruple.add(tempQuadruple) ;
		    this.curQuadruple++ ;
		    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
		    return prevEqual.get(0) ;//  send back final TokenInfo

	    } // else if
	    else if ( operator.size() > 2 && ( prevEqual.size() >= 2 || haveBracket == true ))  {
             int lastEntry = 0 ; // last Operator in Stack(like - + )
	    	 if ( operator.get(operator.size()-2).getEntry() == 9 ) lastEntry = 5 ;// Top Stack of operator is "^" give it level = 5
	    	 if ( operator.get(operator.size()-2).getEntry() == 7 || operator.get(operator.size()-2).getEntry() ==  8 ) lastEntry = 4 ;// Top Stack of operator is "*" "/" give it level = 4
	    	 if ( operator.get(operator.size()-2).getEntry() == 5 || operator.get(operator.size()-2).getEntry() ==  6 ) lastEntry = 3 ;// Top Stack of operator is "+"	"-" give it level = 3
             
             //int stop = operator.size() ;
	    	 while ( operator.size() > 0 ) {
		    		int curEntry = 0 ; // nextEqual value,
                      
		    		if ( operator.peek().getEntry() == 9 ) curEntry = 5 ;// nextEqual operator is "^" give it level = 5
		    		if ( operator.peek().getEntry() == 7 || operator.peek().getEntry() ==  8 ) curEntry = 4 ;// nextEqual operator is "*" "/" give it level = 4
		    		if ( operator.peek().getEntry() == 5 || operator.peek().getEntry() ==  6 ) {
		    			curEntry = 3 ;// nextEqual operator is "+"	"-" give it level = 3
		    		}//
		    		//System.out.print(curEntry);
		    		
                    if ( curEntry > lastEntry) {
        		    	addToken = new ArrayList<TokenInfo>() ;
            			addToken.add(operator.pop()) ;
            			addToken.add(operand.pop()) ;
            			addToken.add(1,operand.pop()) ;
            			

            			this.randomNumber++ ;
            			TokenInfo tempRandomNumber = new TokenInfo("T" + Integer.toString(this.randomNumber),0,this.randomNumber) ;
            			addToken.add(tempRandomNumber);
            			operand.push(tempRandomNumber) ;
            			
            		    tempQuadruple = new Quadruple(addToken.get(3).getValue() +" = " + addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue() ,addToken) ;
            		    //System.out.println("Enter operator >  2 Operator first " + addToken.get(3).getValue() +" = " + addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue() );
            		    this.myQuadruple.add(tempQuadruple) ;
            		    this.curQuadruple++ ;
            		    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
                    } //
                    else {
                      break ;
                    } //

                    //stop = operator.size() ;
	    	 } // while
	    	
	    	
	    	
			  Collections.reverse(operand); //// may have bug
			  Collections.reverse(operator);
			  
			  
			  //for ( int i = 0 ; i < operand.size();i++) System.out.print(operand.get(i).getValue() + " ");
			  //System.out.println( " Operand Value");
			  //for ( int i = 0 ; i < operator.size();i++) System.out.print(operator.get(i).getValue() + " ");
			  //System.out.println( " Operator Vaue");
			  
			  
			  
			  operator.pop(); // Delete the =
			  if ( haveBracket  == false) { // If have bracket prevEqual dont have Identifier
				  operand.pop();
	              operand.add(0, prevEqual.get(0)) ;
			  }
			  operator.add(0, prevEqual.get(prevEqual.size()-1));
		    while ( operator.size() > 0 ) {




		    	addToken = new ArrayList<TokenInfo>() ;
    			addToken.add(operator.pop()) ;
    			addToken.add(operand.pop()) ;
    			addToken.add(operand.pop()) ;




    			this.randomNumber++ ;
    			TokenInfo tempRandomNumber = new TokenInfo("T" + Integer.toString(this.randomNumber),0,this.randomNumber) ;
    			addToken.add(tempRandomNumber);

    			operand.push(tempRandomNumber) ;

    		    tempQuadruple = new Quadruple(addToken.get(3).getValue() +" = " + addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue() ,addToken) ;
    		    //System.out.println("Enter operator > 2 operator is not = "+ addToken.get(3).getValue() +" = " + addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue());
    		    this.myQuadruple.add(tempQuadruple) ;
    		    this.curQuadruple++ ;
    		    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);

		    	if ( operator.size() ==1 ) {

                     
			      	
			      	if ( haveBracket  == true) { // It's have Bracket need to stop add the =
			      		return tempRandomNumber ;
			      	}//
			      	else {// need to deal with "="
			    		addToken = new ArrayList<TokenInfo>() ;
				      	addToken.add(operator.pop()) ;
				      	addToken.add(operand.pop()) ;
				      	addToken.add(operand.pop()) ;
			    		if (prevEqual.size() ==3) {
				    		addToken.add(prevEqual.get(1)) ; //A(K)
				    		tempQuadruple = new Quadruple(addToken.get(2).getValue() + "(" + addToken.get(3).getValue() +")" + addToken.get(0).getValue() + addToken.get(1).getValue() ,addToken) ;
				    		//System.out.println("Enter operator > 2 prevEqual = 3 operator is = " + addToken.get(2).getValue() + "(" + addToken.get(3).getValue() +")" + addToken.get(0).getValue() + addToken.get(1).getValue()   );
			    		}else if ( prevEqual.size() == 2 ) {
			    			tempQuadruple = new Quadruple(addToken.get(2).getValue() + addToken.get(0).getValue() + addToken.get(1).getValue(),addToken) ;
			    			//System.out.println("Enter operator > 2 prevEqual = 2 operator is = " + addToken.get(2).getValue() + addToken.get(0).getValue() + addToken.get(1).getValue() );
			    		} else {
			    		  return null ;
			    		} //



		    		    //operator.pop() ;
		    		    this.myQuadruple.add(tempQuadruple) ;
		    		    this.curQuadruple++ ;
		    		    //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
		    		    return prevEqual.get(0) ;	
			      	} //else

		    	} //if



		    } //	while


	    } // else if


		/***********************************************************************************************************************************************************************************/






		return null ;
	} //


	public boolean makeAssignment(ArrayList<TokenInfo> tempToken, int start,boolean isIf) { // make Assignment Statement 1.call ReversePolish to do �B��(���a��n�p��IF Statement) 2. call by makeStatment, makeQuaruple, MakeIf 3.be Careful operator and operand Debug
		ArrayList<TokenInfo> prevEqual = new ArrayList<TokenInfo>() ;// TokenInfo previous =  eg: "A(X)" = a +2.7 ;   "I" = 2 ;
		ArrayList<TokenInfo> nextEqual = new ArrayList<TokenInfo>() ;// TokenInfo after    =  eg: A(X) = "a +2.7" ;   "I" = "2" ;
		int isType = 0, isDot = 0  ; // The amout of Dot and DataType ;
        //for ( int i = 0 ; i < this.table5.size() ; i++ ) if ( this.table5.get(i) != null ) System.out.println(this.table5.get(i).getValue());

		/*************************************************Initial prevEqual*************************************************************************/
		if ( tempToken.get(start).getTable() == 5 ) { // If it's Identifier  eg: "A"(X) = a +2.7 ;   "I" = 2 ;
		  if (tempToken.get(tempToken.size()-1).getTable() == 1 && tempToken.get(tempToken.size()-1).getEntry() == 1) {// If the last TokenInfo is ";"
			start++ ; // Move to next TokenInfo  analyze whether is "Array" or "Identifier"
			if ( tempToken.get(start).getTable() == 1 && tempToken.get(start).getEntry() == 2 ) { // If it's "("
				start++ ; // Move to next TokenInfo
				int j = start ; // start at Identifier TokenInfo eg: A("X") = a +2.7 ;   A("X",Y) = a +2.7 ;
				int haveIdentifier = 0 ;
				//System.out.println(this.subrountine + tempToken.get(0).getValue());
		          for ( int k = 0 ; k < this.table5.size();k++) { // Findout the the Identifier have current subroutine in Table5
		        	  if (  this.table5.get(k) != null && tempToken.get(0).getValue().equals(this.table5.get(k).getValue() )== true  && this.table5.get(k).getSubroutine() == this.subrountine) { // it's 1.same Identifier 2.current subroutine
							prevEqual.add(this.table5.get(k)) ; // add TokenInfo to prevEqual--> A X Y
							haveIdentifier++ ;
		        	  }//
                      
		          }// for table5
               
					if ( haveIdentifier == 0) {
		        		  errorType = errorType +   tempToken.get(0).getValue()  + "should in Table5 \n " ;
		        		  //System.out.println("YESs");
		        		  return false ;
		        	  }
				//prevEqual.add(tempToken.get(0)) ; // Add the Token Identifier "A"(X) = a +2.7 ;
				
			  boolean stop = false ;	
			  while ( stop == false  ) { // It's not ")=" eg: A(X ")=" a +2.7 ;\

					if ( tempToken.get(j).getTable() == 5 ) { // If it's Identifier A("X","Y") = a +2.7 ;
						haveIdentifier = 0 ;
				          for ( int k = 0 ; k < this.table5.size();k++) { // Findout the the Identifier have current subroutine in Table5
				        	  if (  this.table5.get(k) != null && tempToken.get(j).getValue().equals(this.table5.get(k).getValue() )== true  && this.table5.get(k).getSubroutine() == this.subrountine) { // it's 1.same Identifier 2.current subroutine
				        		  //System.out.println(this.table5.get(k).getValue());
									isType++ ; //DataType plus one
									prevEqual.add(this.table5.get(k)) ; // add TokenInfo to prevEqual--> A X Y
									haveIdentifier++ ;
				        	  }//
                              
				          }// for table5
                       
							if ( haveIdentifier == 0) {
				        		  errorType = errorType +   tempToken.get(j).getValue()  + "should in Table5 \n " ;
				        		  //System.out.println("YESs");
				        		  return false ;
				        	  }
					} // if is Table5
					if (tempToken.get(j).getTable() == 1 && tempToken.get(j).getEntry() == 11 ) isDot++ ; // It's Dot eg:A(X","Y) = a +2.7 ;
				    j++ ;
				    if (tempToken.get(j+1) != null &&  tempToken.get(j).getTable() == 1 && tempToken.get(j).getEntry() == 3 &&
                     	    tempToken.get(j+1).getTable() == 1 && tempToken.get(j+1).getEntry() == 4 )  stop = true ;
			  } // while
			     if ( tempToken.get(j+1) != null) prevEqual.add(tempToken.get(j+1)) ; // add =    eg:A(X,Y) "=" a +2.7 ;

	             //for ( int i = 0 ; i < prevEqual.size() ; i++ ) System.out.print(prevEqual.get(i).getValue());
	             //System.out.println("YES"); 
	     
			     
			  if ((isType - isDot == 1 && isDot != 0 ) || (  isDot == 0 && isType ==1 )) { // eg: condition1 A(X,Y) = 2.7 condition2 A(X) = 2.7
				  if ( tempToken.get(j).getTable() == 1 && tempToken.get(j).getEntry() == 3 ) { // ")"
					  if ( prevEqual.get(prevEqual.size()-1).getTable() == 1 && prevEqual.get(prevEqual.size()-1).getEntry() == 4) { // =   eg: A,I,J,K,=
					    start = j+2 ; // go to nextEqual
					  } // if
					  else {
						  errorType = errorType +   prevEqual.get( prevEqual.size()-1).getValue()  + "should have = \n " ;
						  return false ;
					  } // else =
				  } // if
				  else {
					  errorType = errorType +  prevEqual.get( prevEqual.size()-2).getValue()  + "should have ) this is not array \n " ;
					  return false ;
				  } // else )
			  } //
			  else {
				  this.errorType = this.errorType + "The Identifier in Array" + " amount is not Correct \n" ;
				  return false ;
			  } //  amount is not Correct


			} //
			else {// If it's not Array
			  if ( tempToken.get(start).getTable() == 1 && tempToken.get(start).getEntry() == 4) { //eg: I "=" 2
					int haveIdentifier = 0 ;
					//System.out.println(this.subrountine + tempToken.get(0).getValue());
			          for ( int k = 0 ; k < this.table5.size();k++) { // Findout the the Identifier have current subroutine in Table5
			        	  if (  this.table5.get(k) != null && tempToken.get(0).getValue().equals(this.table5.get(k).getValue() )== true  && this.table5.get(k).getSubroutine() == this.subrountine) { // it's 1.same Identifier 2.current subroutine
								prevEqual.add(this.table5.get(k)) ; // add TokenInfo to prevEqual--> A X Y
								haveIdentifier++ ;
			        	  }//
	                      
			          }// for table5
	               
						if ( haveIdentifier == 0) {
			        		  errorType = errorType +   tempToken.get(0).getValue()  + "should in Table5 \n " ;
			        		  //System.out.println("YESs");
			        		  return false ;
			        	  }
				  prevEqual.add(tempToken.get(start)) ; // add the = to prevEqual---> I=
				  start++ ;
			  } //
			  else {
				  errorType = errorType +   tempToken.get(start).getValue()  + "should have = \n " ;
				  return false ;
			  } // is not =
			} // is not (  Array
			/****************************************************************************************************************************************/


            
			/****************************************************nextEqual**************************************************************************/
			for ( int j = start ; j < tempToken.size() - 1 ; j++ ) { //  the TokenInfo between "=" and ";"   A(X,Y) = "a +2.7" ;

		        if (tempToken.get(j).getTable() == 5 ) { // If it's Identifier need to check it's current subroutine or not
		          int haveIdentifier = 0 ;
		          for ( int k = 0 ; k < this.table5.size();k++) { // Findout the the Identifier have current subroutine in Table5
		        	  if ( this.table5.get(k) != null && tempToken.get(j).getValue().equals(this.table5.get(k).getValue() )== true  && this.table5.get(k).getSubroutine() == this.subrountine) { // it's 1.same Identifier 2.current subroutine
		        		  nextEqual.add(this.table5.get(k)) ; // add the Identifier(in the table5) to nextEqual
		        		  haveIdentifier++ ;
		        		  //System.out.println(this.table5.get(k).getValue());
		        	  }//

		          }// for table5
					if ( haveIdentifier == 0) {
		        		  errorType = errorType +   tempToken.get(j).getValue()  + "should in Table5 \n " ;
		        		  //System.out.println("YESs");
		        		  return false ;
		        	  }
		        }//		
		        else {
					nextEqual.add(tempToken.get(j)) ; //  other is not Identifier all add to nextEqual
		        }//


			} // for
			/***************************************************************************************************************************************/
             //for ( int i = 0 ; i < prevEqual.size() ; i++ ) System.out.print(prevEqual.get(i).getValue());
             //System.out.println();
             //for ( int i = 0 ; i < nextEqual.size() ; i++ ) System.out.print(nextEqual.get(i).getValue());
             //System.out.println();
             
			/************************************Do the Operand and operator Reverse Polish************************************************/


            if ( ReversePolish(prevEqual, nextEqual,false, 0,isIf,false) != null ){ // use ReversePolish to do operand and operator Calculate
            	for ( int i = 0 ; i < this.table5.size() ; i++ ) {
            		if ( this.table5.get(i) != null && this.table5.get(i).getValue().equals(tempToken.get(0).getValue())){
            			this.table5.get(i).setError(true);
            		}
            	}// for
            	return true ; // If the Operator Success will return  a TokenInfo
            }//
            else {
            	errorType = errorType +    "Can not analyze the assignment \n " ;
            	return false ;
            } //

			/*************************************************************************************************************************************/





		  }// if
		  else {
			  errorType = errorType +  tempToken.get(tempToken.size()-1).getValue()  + "should have ; \n " ;
		  } // ;
		} //
		else {
			errorType = errorType +  tempToken.get(start).getValue()  + "Is not Identifier\n " ;
		} // Table 5

		return false ;
	} //

   
  private  int conditionTemp = 0 ;
   
   public TokenInfo ReversePolish_If(ArrayList<TokenInfo>  tempToken,boolean haveBracket) {
		  java.util.Stack<TokenInfo> operand = new java.util.Stack<TokenInfo>(); // Operand stack
		  java.util.Stack<TokenInfo> operator = new java.util.Stack<TokenInfo>();// Operator stack
		  int isBracket = 0 ; // How many Bracket
		  ArrayList<TokenInfo> addToken = new ArrayList<TokenInfo>() ; // 
		  for ( int j=0; j < tempToken.size() ; j++) {
			  if (  tempToken.get(j).getValue().equals("AND") || tempToken.get(j).getValue().equals("OR") ||
				   tempToken.get(j).getValue().equals("NOT") || tempToken.get(j).getValue().equals("GT") ||
				   tempToken.get(j).getValue().equals("GE")  || tempToken.get(j).getValue().equals("LT")  ||
				   tempToken.get(j).getValue().equals("LE")  || tempToken.get(j).getValue().equals("EQ")  ) { // Condition Operator
				  operator.push(tempToken.get(j)) ;
			  } // if Operator
			  else if ( tempToken.get(j).getTable()==5 || tempToken.get(j).getTable()==3 || tempToken.get(j).getTable()==4) { // the Operand can be Identifier, Real, Integer
				  if (tempToken.get(j).getTable()==5 ){ // If it's Identifier need to check it Subroutine
					  int haveIdentifier = 0 ;
		          for ( int k = 0 ; k < this.table5.size();k++) {
		        	  if ( this.table5.get(k) != null && tempToken.get(j).getValue().equals(this.table5.get(k).getValue()) 
		        			  && this.table5.get(k).getSubroutine() == this.subrountine && this.table5.get(k).getError() == true) {
		        		  operand.add(this.table5.get(k)) ; // add to operand
		        		  haveIdentifier++ ;
		        	  } // if
		          }// for table5
		             
		          //System.out.println(haveIdentifier);
		          if ( haveIdentifier == 0 ) {
		        	  this.errorType = this.errorType + "Identifier should have Value \n" ;
		        	  return null ; 
		          }
		             
		          
				  } // if it's table5
				  
				  
				  else{ // It's Real or Integer
					  operand.add(tempToken.get(j)) ; // add to operand
				  } //
			  } // else if table5
			  else if ( tempToken.get(j).getTable() ==1 && tempToken.get(j).getEntry() == 2) { // If it's "("
				  isBracket++ ; // bracket add one
				  operator.push(tempToken.get(j)) ; // add to operator
			  } // else if (
			  else if ( tempToken.get(j).getTable() ==1 &&  tempToken.get(j).getEntry() == 3 ) { // If it's ")" Need to deal with Array
				  int leftBracket = 0 ; // amount of "("
				  int temp = this.curQuadruple ; // If it's error can use to delete the modify Data
				  ArrayList<TokenInfo> tempCondition = new ArrayList<TokenInfo>() ; // tempCondition use to add to ReversePolish_If
				  while( operator.peek().getEntry() != 2 && operator.peek() !=null) { // It's not ")"
					if ( tempCondition.size() == 0 ) {  // If tempCodition Begin is empty
						tempCondition.add(operand.pop());//add first Operand
						tempCondition.add(0,operator.pop()); 
					}//
					else {
						tempCondition.add(0,operand.pop());
						tempCondition.add(0,operator.pop()); 	
					}//
					if ( operator.peek().getEntry() == 2 && operator.peek() != null ) leftBracket++ ; // It's "(" 
				  } // while  
				  	  
					  
				  if ( leftBracket == 1 ) {// It's correct Bracket type
					  isBracket-- ; // miner the Bracket
 				  operator.pop() ; // delete the "("
 				  tempCondition.add(0,operand.pop()); // add eg: X GT Y
				  TokenInfo tempBracket= new TokenInfo();
				  haveBracket = true ; // need to deal with bracket
				  tempBracket = ReversePolish_If( tempCondition,true) ; // Maybe have Bug    
				    if (tempBracket == null ){
					  errorType = errorType + "Is not correct Bracket in If Statement \n " ;
					  for ( ; temp < this.curQuadruple ; temp++) this.myQuadruple.remove(temp--) ; // delete the Modify Data in Quadruple
					  this.curQuadruple = this.myQuadruple.size() ;
					  return null ;
				    }//
				  haveBracket = false ; // close the haveBracket
				  operand.add(tempBracket) ; //add the return TokenInfo to operand
				  }	// if
				  else {
					for ( ; temp < this.curQuadruple ; temp++) this.myQuadruple.remove(temp--) ;
					this.curQuadruple = this.myQuadruple.size() ;
				    return null  ;
				  }	  
			  } // else if )

			  else {
				  errorType = errorType +  tempToken.get(j).getValue()  + "Is not operand or operator \n " ;
				  return null ;
			  } //else







		  }// for Condition

		  Quadruple tempQuadruple = new Quadruple() ;
		 // java.util.Stack<TokenInfo> newOperand = new java.util.Stack<TokenInfo>();
		  //java.util.Stack<TokenInfo> newOperator = new java.util.Stack<TokenInfo>();
		  Collections.reverse(operand); //// may have bug Reverse the Operand Because  Basic Operation  algorithm
		  Collections.reverse(operator); // Reverse the Operator Because Operation   algorithm
		  TokenInfo tempRandomNumber = new TokenInfo() ; // RandomNUmber T1 T2,.........T4s




		  while ( operator.size() >0 ) {


			  tempRandomNumber = new TokenInfo() ;
		    	addToken = new ArrayList<TokenInfo>() ;
 			addToken.add(operator.pop()) ;
 			addToken.add(operand.pop()) ;
 			addToken.add(operand.pop()) ;



 			if ( operator.size() == 0  &&  haveBracket == false) { // be careful of haveBracket, Because haveBracket dont need return true RandomNumber,it return T-1
 				this.randomNumber++ ; // is last one e.g : T1
 				tempRandomNumber = new TokenInfo("T" + Integer.toString(this.randomNumber),0,this.randomNumber) ;
 				
 			}else {
 				tempRandomNumber = new TokenInfo("T" + Integer.toString(-1),0,-1) ; // Later need to deal with forward Refernce in makeIf Function

 			}
 			addToken.add(tempRandomNumber);// add Random Number to ArrayList

 			operand.push(tempRandomNumber) ;

 		    tempQuadruple = new Quadruple(addToken.get(3).getValue() +" = " + addToken.get(1).getValue() + " "+addToken.get(0).getValue() +" "+ addToken.get(2).getValue() ,addToken) ; // T1 = X AND Y
 		    //System.out.println("Enter ReversePolish " + addToken.get(3).getValue() +" = " + addToken.get(1).getValue() + addToken.get(0).getValue() + addToken.get(2).getValue() );
 		    this.myQuadruple.add(tempQuadruple) ; // add to Quadruple Table
 		    this.curQuadruple++ ;
 		   //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
 		    conditionTemp++ ; //use in makeIf,to Deal with forward Reference in Random Number

		  } // while


	   
	   return tempRandomNumber ;// If it's all true return the RandomNumber 
   }// return null
	


	public boolean makeIf( ArrayList<TokenInfo> tempToken, int start,int line) {// Make IF Statement  1.call makeStatement to do Condition and Statement(need to check all other statement ) 2.call By makeStatement and makeQuaruple
      /***********************************************************************Variable announce**********************************************/
		int isIf = 0, isThen = 0, isElse = 0 ; // The Amount of IF THEN STATEMENT 
		ArrayList<TokenInfo> ifCondition = new ArrayList<TokenInfo>() ; // IF STATEMENT 
		ArrayList<TokenInfo> thenStatement = new ArrayList<TokenInfo>() ; // THEN STATEMENT
		ArrayList<TokenInfo> elseStatement = new ArrayList<TokenInfo>() ; // ELSE STATEMENT
        this.conditionTemp = 0 ;
	  /**************************************************************************************************************************************/

	  /*****************************************FindOut Condition Statemen1 Statement2 String************************************************/
		for( int i = 0 ; i < tempToken.size()-1 ; i++) {
		  if ( tempToken.get(i).getValue().equals("IF")) isIf++ ;
		  else if ( tempToken.get(i).getValue().equals("THEN")) isThen++ ;
		  else if ( tempToken.get(i).getValue().equals("ELSE")) isElse++ ;
		  if ( isIf ==1 && isThen == 0 && isElse == 0 ) ifCondition.add(tempToken.get(i)) ; // the Condition TokenInfo between If and Then
		  else if ( isIf ==1 && isThen == 1 && isElse == 0 ) thenStatement.add(tempToken.get(i)) ; // the thenStatement TokenInfo between Then and Else
		  else if ( isIf ==1 && isThen == 1 && isElse == 1 ) elseStatement.add(tempToken.get(i) ) ; // the elseStatement TokenInfo
		}// for

	  /***************************************************************************************************************************************/
         
		String tempForward ;
		
		if ( (tempToken.get(tempToken.size()-1).getTable() == 1 && tempToken.get(tempToken.size()-1).getEntry() == 1)) { // If The Last TokenInfo is ";"
			if ( isIf == 1 && isThen ==1 && isElse == 1 ){
			  /******************************************Condition****************************************************/
              
              ifCondition.remove(0) ;// eg: IF X GT Y AND Q
              thenStatement.remove(0); //eg: THEN X = X+1
              thenStatement.add(tempToken.get(tempToken.size()-1)) ; // add";" to then Statement
              elseStatement.remove(0); // eg: ELSE X = X + 2 ;
              elseStatement.add(tempToken.get(tempToken.size()-1)) ; // add";" to else Statement
              
			  Quadruple tempQuadruple = new Quadruple() ;
			  ArrayList<TokenInfo> addToken = new ArrayList<TokenInfo>() ; // arrayList use to add to Quadruple
              
              if (ReversePolish_If(ifCondition,false) != null) { // finde the Condidion Reverse Polish
    			  addToken.add(tempToken.get(0)) ; // add IF
    			  //System.out.println(tempToken.get(0).getValue());
    			  addToken.add(this.myQuadruple.get(this.myQuadruple.size()-1).getQuadruple().get(3)) ; // add the T1 eg: T1= T4 AND Q  
    			  tempForward = this.myQuadruple.get(this.myQuadruple.size()-1).getQuadruple().get(3).getValue() ;
    			  //System.out.println("Return Condition Temp " +  this.myQuadruple.get(this.myQuadruple.size()-1).getQuadruple().get(3).getValue());
              }//
              else {
            	  errorType = errorType  + "Is not correct If Condition \n " ;
            	 return false ; 
              }//


			  /*****************************************************************************************************************************************************/

			  this.curQuadruple++ ;
			  //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
			  //System.out.println("tempQuadruple Condition " + this.curQuadruple);
			  int       tempQuadrupleNumberCondition = this.curQuadruple ;// 17
			  int temp = this.curQuadruple + 1 ;
			  TokenInfo tempTokenInfo  = new TokenInfo(" GO TO " + temp, 6,this.curQuadruple+1) ; // add  GO TO STAMENT1 Quadruple Location (6,17)
			  
			  addToken.add(tempTokenInfo) ; // add first Go to 
			  tempTokenInfo  = new TokenInfo(" GO TO -1"  , 6,-1) ; // add  GO TO STATMENT 2 LATER NEED TO DEAL WITH FORWARD REFERRENCE (6,20)
			  addToken.add(tempTokenInfo) ; // add second Go to 
  		      tempQuadruple = new Quadruple(addToken.get(0).getValue() +" " + addToken.get(1).getValue() + addToken.get(2).getValue()+ ", ELSE " + addToken.get(3).getValue() ,addToken) ;// (IF,T1,(6,17),(6,20)) IF T1 GO TO 17,ELSE GO TO 20
  		      //System.out.println("Finish Condition " + addToken.get(0).getValue() +" " + addToken.get(1).getValue() + addToken.get(2).getValue()+ ", ELSE " + addToken.get(3).getValue() );
  		      this.myQuadruple.add(tempQuadruple) ;// add The If condition to Quadruple Statement

			  /************************************************Deal with first Statement*****************************************************************************/

  		      if (this.makeStatement(line, 0, thenStatement,true) == false ) return false ; // Do the statement1
			  int tempQuadrupleNumberStatement1 = this.curQuadruple ; // GTO Remember it Location eg: X = T2
			  /*****************************************************************************************************************************************************/

			  /************************************************Deal with second Statement***************************************************************************/

  		      if (this.makeStatement(line, 0, elseStatement,true) == false) return false ;


  		       if ( ! thenStatement.get(0).getValue().equals("GTO")) { // if then statement is not GTO  than need GTO to the location after finished  else
  	  		       addToken = new ArrayList<TokenInfo>() ;
  	  		       tempTokenInfo = new TokenInfo("GTO",2,11) ; // make a GTO
  	  		       addToken.add(tempTokenInfo);
  	  		       this.curQuadruple++  ;//move to new QUadrupale(6,22)
  	  		   //System.out.println("Currenct QUadruple number = " + this.curQuadruple);
  	  		       tempTokenInfo = new TokenInfo("",6,this.curQuadruple+1) ;//add one because later we need to add T1
  	  		       addToken.add(tempTokenInfo) ;
  	  		       tempQuadruple = new Quadruple("GO TO After THEN Quaruple",addToken ) ;// GTO (6,22)
  	  		       this.myQuadruple.add(tempQuadrupleNumberStatement1 , tempQuadruple);// add GTO , (6,22)
  	  		   this.myQuadruple.get(tempQuadrupleNumberCondition-1).dealForward(tempQuadrupleNumberStatement1+2,1,"");// // deal forward Reference  EG: (IF,T1,(6,17),(6,-1))
  		       }//
  		       else {
  		    	 this.myQuadruple.get(tempQuadrupleNumberCondition-1).dealForward(tempQuadrupleNumberCondition+2,1,""); // deal forward Reference  EG: (IF,T1,(6,17),(6,-1)) have GTO STATEMENT
  		       } //


  		       int startCondition =  tempQuadrupleNumberCondition -  conditionTemp  ; // ConditionTemp set in RerversePolish_If
  		       //System.out.println(startCondition);
  		       //System.out.println(conditionTemp);
  		       
  		      
               for ( int i = 0 ; i < conditionTemp;i++ ){
            	  this.randomNumber++ ;
            	  //System.out.println(startCondition);
            	  //System.out.println(this.myQuadruple.get(startCondition-1).getQuadruple().get(3).getValue());
            	  this.myQuadruple.get(startCondition-1).dealForward(this.randomNumber, 2,tempForward); // Change the random number inside the Condition is second
            	  startCondition = startCondition + 1 ;
               }//
               
               
 
  		       return true ;

			  /*****************************************************************************************************************************************************/



			}//if
			else {
			  if (isIf !=1) {
				  errorType = errorType  + "Should have only one IF ;\n " ;
			  } else if(isThen != 1 ){
				  errorType = errorType  + "Should have only one THEN ;\n " ;
			  } else if(isElse !=1){
				  errorType = errorType  + "Should have only one ELSE ;\n " ;
			  }

			}//else
		}//
		else {
			errorType = errorType +  tempToken.get(start).getValue()  + "Is not ;\n " ;
		}// else



		return false ;
	} // makeIf


	public void toQuadruple_output() { // Output the  Quadruple
	      String opt = "" ;
	      for (  int i = 0 ; i < this.myQuadruple.size() ; i++ ) {
	    	 int temp = i + 1 ;
	    	 opt = opt + temp +"  "+ this.myQuadruple.get(i).showQuadruple() + "\n";  // output the Quadruple each line
	      } // for i lexcialFile


	      System.out.print(opt ); // show in the Editor
	      //System.out.println(this.FileName);
	      try {
	    	    if ( this.FileName.equals("e1") == true) this.FileName = "input" ;
	    	    BufferedWriter out = new BufferedWriter(new FileWriter(this.FileName.replaceAll("input", "outputFrancis") + ".txt")); // change the output Name
	    	    out.write(opt+ "\n"+this.optError);  //Replace with the string
	    	                                             //you are trying to write
	    	    out.close();
	    	}
	    	catch (IOException e)
	    	{
	    	    System.out.println("Exception Cant creat output File");

	    	}
	    } // toString

    public void toLexical_output() { // Output the lexical File
        String opt = "" ;
        for (  int i = 0 ; i < this.statement.size() ; i++ ) {
          opt = opt + this.statement.get(i).toString("")  ;
        } // for i lexcialFile



        System.out.print(opt );
        /*
        try {
      	    BufferedWriter out = new BufferedWriter(new FileWriter(this.FileName.replaceAll("input", "outputHC")));
      	    out.write(opt);  //Replace with the string
      	                                             //you are trying to write
      	    out.close();
      	}
      	catch (IOException e)
      	{
      	    System.out.println("Exception Cant creat output File");

      	}

      	*/
      } // toString

} // francis
