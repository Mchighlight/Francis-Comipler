package lexical;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import francis.francis;

public class main {

	
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in) ;
		System.out.println("/**********************Mission One Lexcical analysis*********************/");
		System.out.println("*********************************X86Anlysis******************************/");
		System.out.println("*************************************************************************/");
		System.out.println("Please Input the exact FileName in the Folder");
		String name = scanner.nextLine();
		myFile x86 = new myFile(name) ; // Reading File
		ArrayList<String[]> tableList = new ArrayList<String[]>(); // Hash TableList
		   
		/*************************************Table1 to Table 2******************************************/
	    for ( int i = 1 ; i <= 2 ; i++) { //buildup Table1 to Table2 and add  them into Hash TableList
	      String input = "table" + i ;
	      String[] table = ReadTable(input) ;
	      tableList.add(table) ;
	    } // for
		/***********************************************************************************************/
	    
	    
	 
	    
	    
	    /******************************************IntegerTable******************************************/
	    HashFunction table_hash = new HashFunction(x86.getIntegerList().length) ;//Buildup Integer Table
	    table_hash.hashFuction2(x86.getIntegerList());
	    String[] table = table_hash.getArray() ;
	    tableList.add(table) ;
	    /***********************************************************************************************/
	    
	    /*******************************************RealTable*****************************************/
	    table_hash = new HashFunction(x86.getRealList().length) ; //Buildup Real Table
	    table_hash.hashFuction2(x86.getRealList());
	    table = table_hash.getArray() ;
	    tableList.add(table) ;
	    /***********************************************************************************************/
	    	    
	    /*******************************************Buildup the LexicalFile*****************************/
	    x86.toLexical(tableList); // convert the File into lexical File
	    x86.findSymbol();         // Find out the Symbol Table
	    x86.toLexical_output();   // Display the Lexical File and output its "txt" File	  
	    //System.out.println("/**********************************************************************************/");
	    //System.out.println("The Ouput Lexical File in the folder");
	    //System.out.println("/**********************************************************************************/" + "\n");
	    tableList.add(4,x86.getSymbolList()); // Add Symbol Table into the Hash TableList
	    /***********************************************************************************************/    

	    
	    
	    /**********************************************Check The Table************************************************/
	    // System.out.println("/*************************************************************************************/");
	    //System.out.println("Choose Which Table you want to check, Input1 . If you want to check all of Table,Input2");
	    //System.out.println("/*************************************************************************************/");
        //int option = scanner.nextInt() ;
	    //if( option == 1 ) {
	    //	System.out.println("Input the table you want to check 1~5");
	    //	int tableOption = scanner.nextInt();
	    //	x86.showTable(tableOption, tableList); // Choose the Table you Want to check
	    //	System.out.println("Successful,Selected Ouput Table in the Folder");
	    //} else if(option ==2  ) {
        //  x86.showAllTable(tableList); // See All the Table
        //  System.out.println("Successful, All of output Table in the Folder" );
        //  
	    //} else{
	    //	System.out.println("Fail,Wrong Input");
	    //}// else other input
	    /**************************************************************************************************************/
	      System.out.println("/**********************************************************************************/");
	      System.out.println("Mission One Lexcial Analyze Completed,If you want to other result, Please Run Again") ;
	      System.out.println("/**********************************************************************************/");
        /************************************************Lexical Mission One*****************************************************/
	    
	      
	    /************************************************Francis Complier Mission Three*****************************************************/  
			System.out.println("/**********************Mission Three francis Complier**************************/");
			System.out.println("*********************************francis Complier******************************/");
			System.out.println("*******************************************************************************/");
			francis myFrancis = new francis(x86.getLexicalFile(),name ,tableList) ;
			
	    /***********************************************************************************************************************************/  
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
	    
    } // main
	
	public static String[] ReadTable( String input) throws IOException { //Read the .Table File and build the Table Hash 
		ArrayList<String> tableList = new ArrayList<String>();
	    FileReader fr = new FileReader(input + ".table"); 
	    BufferedReader  br = new BufferedReader(fr);
	    String line,tempstring ;
	    int i=0;
	    while((line = br.readLine())!=null) { //br.readLine()�O��Ū��txt�ɪ��C�@����,��Ū�쪺��Ʀs��line
	    	
	    	
	         tempstring = line;  //�A�Nline�ᵹtempstring�h�x�s
	         
	         tempstring = tempstring.substring(tempstring.indexOf(" ")+1, tempstring.length()) ;
             tableList.add(tempstring) ;

	    }//while
	    
	    String[] table_String = tableList.stream().toArray(String[]::new); //  ArrayList to String Array
 	    HashFunction table_hash = new HashFunction(table_String.length) ; // build the HashTable
	    table_hash.hashFunction(table_String);
	    table_hash.cleanArray(); // clean the null space
	    String[] table = table_hash.getArray() ;
	   
	    
		return table;
		
	} // ReadTable

}
