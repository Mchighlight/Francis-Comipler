package lexical;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class myFile {
	
	private String FileName ; // Input File name
	private ArrayList<String> myList = new ArrayList() ; // Initial File Transfer it into ArrayList
	private String[] delimiter = { ";", "(", ")" , "=", "+", "-", "*", "/", "↑",
			                       "'", ",", ":"  } ; // Deleimiter String Array
	//private String[] stringList ; // Store String Tablee
	//private String[] numberList ; // Store Number Table
	
	private String[] symbolList ; // Store Symbol Table	                           
	private String[] integerList ;// Store Integer Table
	private String[] realList    ;// Store Real    Table
	
	private String[] trashList   ;// Store Trash   Table 
	
	
	private BufferedReader br; // use for Reading File
	
	private ArrayList<LineInfo> lexicalFile = new ArrayList<LineInfo>() ; // Store lexical File
	
	private ArrayList<String> SymbolArrayList = new ArrayList<String>();  // Templating String Array( refer it as Global ArrayList)
	
	
	//private ArrayList<Boolean> isAnno = new ArrayList<Boolean>() ;
	//private ArrayList<String> Anno = new ArrayList<String>() ;
 	
	myFile(String Name){
	  this.FileName = Name + ".txt"	;
	  System.out.println(FileName);
	  try {
		readFile() ;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		System.out.println("No,This File Please run again"); 
	}
	} // Constructor

	
	public void readFile() throws IOException { // Read theFile and 切token
	    FileReader fr = new FileReader(FileName); 
	    br = new BufferedReader(fr);
	    String line,tempstring ;
	    int i=0;
	    while((line = br.readLine())!= null) { //br.readLine()是指讀取txt檔的每一行資料,把讀到的資料存到line
	         tempstring = line;  //再將line丟給tempstring去儲存
	         //System.out.println(line);

	          this.myList.add(tempstring.toString()) ;
             i++ ;
	    }//while
	    
      
	    deleteWhiteSpace() ; // Delete Whitespace 
	    //deleteAnnotation() ; // Delete Annotation
	    //findString() ; // Find out the String Array to buildup the "String HashTable"
	    findNumber() ;

	} // readFile
	
	public ArrayList getFile() { // return the File after processing
		return myList;
		
	} // getFile
	
	//public String[] getStringList(){ // get the String Table(not transfer into Table yet)
	//	return this.stringList ;
	//}//
	
	//public String[] getNumberList(){
	//	return this.numberList ;
	//}//
	
	
	public String[] getIntegerList(){
		return this.integerList ;
	}//
	
	public String[] getRealList(){
		return this.realList ;
	}//
	
	
	public String[] getSymbolList(){
		return this.symbolList ;
	}//
	
	public String getFileName() {
		return this.FileName ;
	}//
	
	public ArrayList<LineInfo>  getLexicalFile() {
		return this.lexicalFile ;
	} // get lexicalFile
	
	
	
	private void deleteWhiteSpace() { // delete the whiteSpace
      for ( int i = 0 ; i < myList.size() ; i++ ) {
        for ( int y = 0 ; y < delimiter.length  ; y++ ) {
    	    if ( myList.get(i).toString().contains(delimiter[y]) ) // 加空白 
    		      myList.set(i, myList.get(i).toString().replaceAll( "\\" + delimiter[y], " " + "\\" + delimiter[y] + " " ) ) ;	
    	    //if ( myList.get(i).toString().contains(delimiter[8])) {
    	    //	myList.set(i, myList.get(i).toString().replaceAll("\\"+ delimiter[y], " "  + delimiter[y] + " " ) ) ;	
    	    //	System.out.println(delimiter[8]);
    	    //} 	
        }//  for y    
        
      } // for x
      

	} // deleteWhiteSpace

	
	/*
	private void deleteAnnotation() { // delete the annotation
	  for ( int i = 0 ; i < myList.size() ; i++ ) {
        if ( myList.get(i).toString().contains(";"))   {
          int index = myList.get(i).toString().indexOf(";") ;
         // Anno.add(myList.get(i).toString().substring(index + 1, myList.get(i).toString().length())) ;
            myList.set(i, myList.get(i).toString().substring(0, index+1));
         //   isAnno.add(false) ;
        }
	  } // for x
	} // deleteAnnotation
	*/
	
	/*
    private void findString() {	// Find out the string array to buildup the string table,And delete the whiteSpace in the token
    	
      ArrayList<String> stringArrayList = new ArrayList<String>(); 
      for(int i = 0 ; i < myList.size() ; i++ ) {
        if ( myList.get(i).toString().contains("'"))   {
          int firstIndex = myList.get(i).toString().indexOf("'") ;
          int lastIndex  = myList.get(i).toString().lastIndexOf("'") ;
          String str = myList.get(i).toString().substring(firstIndex+1, lastIndex-1) ; // cut out the string token
          str = str.replaceAll("\\s+", ""); // delete the whitespace in the string token
          stringArrayList.add(str) ; // Add it into String Array
          str = myList.get(i).toString().substring(0, firstIndex+1) + " " + str + " " +
                myList.get(i).toString().substring(lastIndex, myList.get(i).toString().length()) ;// connect the token have been cut from token cutted
          myList.set(i, str) ;
        }//if
      } //for
      
      
      this.stringList = stringArrayList.stream().toArray(String[]::new);  //ArrayList to StringArray

    }//findString()
    */
    
    public void findNumber() { // Find out the Number Array to buildup the Number Table
    
      //ArrayList<String> numberArrayList = new ArrayList<String>(); //Temp Number ArrayList
      
      ArrayList<String> integerArrayList = new ArrayList<String>(); // Temp Integer ArrayList
      ArrayList<String> realArrayList = new ArrayList<String>(); // Temp Real ArrayList
      
      for ( int i = 0 ; i < myList.size() ; i++) {
    	  String[]  temp = myList.get(i).toString().split("\\s+");
    	  for ( int j = 0 ; j < temp.length ; j++ ) {
    	    if (  isNumberOrReal(temp[j]) == 1 )
    	    	integerArrayList.add(temp[j]) ;
    	    else if (isNumberOrReal(temp[j]) == 2 ) 
    	    	realArrayList.add(temp[j]);
    	  } // for j
      }// for i
      
      

      this.integerList = integerArrayList.stream().toArray(String[]::new); // ArrayList to String Array
      this.realList    = realArrayList.stream().toArray(String[]::new); // ArrayList to String Array  

      
      this.integerList = Arrays.stream(this.integerList) // Delete the null in the String Array
              .filter(s -> (s != "" && s.length() > 0))
              .toArray(String[]::new); 
      
      this.realList = Arrays.stream(this.realList) // Delete the null in the String Array
              .filter(s -> (s != "" && s.length() > 0))
              .toArray(String[]::new); 
      
      
      
      
      
    }// findNumber()
    
    
    public void findSymbol() { // Find out the symbol String Array to buildup the String Table
    	
        this.symbolList =  SymbolArrayList.stream().toArray(String[]::new); // ArrayList to String Array
        this.symbolList = Arrays.stream(this.symbolList) // Delete the null in the String Array
                .filter(s -> (s != "" && s.length() > 0))
                .toArray(String[]::new); 
        
    	
	    HashFunction table_hash = new HashFunction(symbolList.length) ; // build the hashTable
	    table_hash.hashFuction2(symbolList); // Use the HashTable defined by Teacher
	    String[] table = table_hash.getArray() ;
	    this.symbolList = table ;
        
	
        for( int i = 0 ; i < lexicalFile.size() ;i++) {
          ArrayList<TokenInfo> tempLineList = lexicalFile.get(i).getLineList() ;
          for ( int j = 0 ; j < tempLineList.size() ; j++) {
            if ( tempLineList.get(j).getTable() == 5 ) { // Find out the Symbol Table location
              for ( int k = 0 ; k < table.length ; k++) {
            	  
            	  if(tempLineList.get(j).getValue().equals(table[k])) {
            		  lexicalFile.get(i).getLineList().get(j).setEntry(k); //set Entry
            		  //k = table.length ; // break 
            	  } // if equal value in table 5
              } // for k
            } // if is SysmbolList
          }// for j LineList
        } // for i lexicalFile
        
    } // findSymbol() 
    
    
    private int isNumberOrReal(String temp) { // Define its number or not INTEGER 1 REAL 2
    	int count = 0 ;
    	int dot   = 0 ;
    	temp = temp.toUpperCase() ; // Value in NumberTable is upperCase
    
    	for ( int i = 0 ; i < temp.length() ; i++ ) { // is Integer
    	  if ( temp.charAt(i) >= '0' && temp.charAt(i) <= '9'  ) {
      	    count++ ;  
    	  }
    	  else {
    		  count-- ;
    	  } //
    	  if ( temp.charAt(i) == '.' )  {
    		  dot++ ; // isDot
    		  count++ ;
    	  }
    	} // for
    	
        
        if (  count == temp.length() && dot == 0 ) {
            return 1 ;	
        } else if ( count == temp.length()-1 && dot == 1) {	
      	  return 2  ;
        } else{
   		 return 0;	
        }

    
    } // isNumber
    
    public void toLexical(ArrayList<String[]> tableList) { // transform myList to Lexical List
    	//for ( int i = 0 ; i < tableList.get(0).length ; i++) System.out.println(tableList.get(0)[i]);
      for ( int i = 0 ; i < myList.size() ; i++) {
    	  String[]  temp = myList.get(i).toString().split("\\s+"); //如果是空白就切token 
    	  temp = Arrays.stream(temp) // Delete the null in the String Array
                  .filter(s -> (s != "" && s.length() > 0))
                  .toArray(String[]::new);
    	  
    	  LineInfo oLineInfo = new LineInfo() ;

    	  
     	  for ( int j = 0 ; j < temp.length ; j++  ) {
    	    oLineInfo.insertTokenInfo(whichTable(tableList,temp[j])); // add tokenInfo into LineInfo

    	  } // for j
     	  
     	  lexicalFile.add(oLineInfo) ; // Add line by line into Lexical File
      } // for i
      
    } // toLexical
    
    private TokenInfo whichTable(  ArrayList<String[]> tableList, String temp ) { // Define the token Table and add its entry and value
        String  temp_og = temp ; // initial token

    	int table = 0, entry = 0 ; // temporary table and entry
        for ( int i = 0 ; i < tableList.size() ; i++ ) {
          String[] tempStringArray = tableList.get(i) 	;  // get currenct table
          for ( int j = 0 ; j < tempStringArray.length ;j++) {
        	if ( i == 0 || i == 2 || i == 1 ) temp = temp.toUpperCase() ; // Register and Instruction Table don't need to Consider the token Case
        	
        	
            if ( ( temp.equals(tempStringArray[j] ) && ( i != 2 && i != 3))  )  { // it's not real integer 
              table = i + 1 ;
              entry = j + 1 ;
            }  else if ( temp.equals(tempStringArray[j] ) && ( i == 2 || i == 3)) { // it's String and Number Table // real and integer
              table = i + 1 ;
              entry = j  ;
            } // else if 
 
          } // for j
          temp = temp_og ; // set it to initial token
        } // for i
    	
        TokenInfo oTokenInfo = null;
        if ( table == 0 ) { // It's Symbol Table
          oTokenInfo = new TokenInfo( temp, 5 ) ;
          SymbolArrayList.add(temp) ;
        } else {
          oTokenInfo = new TokenInfo( temp, table, entry ) ;
        } // else
         
        

        return oTokenInfo ;
    	
    } // whichTable
    

    
    public void toLexical_output() { // Output the lexical File
      String opt = "" ;
      for (  int i = 0 ; i < lexicalFile.size() ; i++ ) {
        opt = opt + lexicalFile.get(i).toString("")  ;  	  
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
    
    public void showTable( int tableNum, ArrayList<String[]> tableList ) { // Output the Table you want to check
    	String opt = "" ;
    	String[] temp = tableList.get(tableNum-1) ; // table value
    	
    	for ( int i = 0 ; i < temp.length ; i++ ) {
    		if ( temp[i] == "-1" ) temp[i] = "" ;
    		opt = opt + i + "\t" + temp[i] + "\n" ;
    	} //
    	
        try {
    	    BufferedWriter out = new BufferedWriter(new FileWriter( "Table" + tableNum + "_output" + ".table"));
    	    out.write(opt);  //Replace with the string 
    	                                             //you are trying to write  
    	    out.close();
    	}
    	catch (IOException e)
    	{
    	    System.out.println("Exception Cant creat output TableFile");

    	}
    	
    } // showTable
    
    public void showAllTable(ArrayList<String[]> tableList ) {// Output 7 tables
    	for ( int i = 1 ; i < 6 ; i++) { // Output Table one after another
    		showTable(i,tableList) ;
    	} // for
    } // showAllTable
    
    
} // class myFile
