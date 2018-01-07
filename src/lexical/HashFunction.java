package lexical;

import java.util.Arrays;

public class HashFunction {
  private String[] theArray ;
  private int arraySize		; // arraySize
  
  HashFunction( int size) {
	arraySize = getNextPrime(size) ; // find the smallest prime bigger than initial array size
	theArray  = new String[500]   ;
	Arrays.fill(theArray, "-1"); // fill it with "-1"
  } //HashFunciton
  
  public void hashFunction(String[] stringForArray) { // Table1 to Table4
	 for ( int i = 0 ; i < stringForArray.length; i++ ) {
	   String newElementVAl = stringForArray[i] ;
	   theArray[i] = newElementVAl ;
	 } // for
  } // hashFunction
  
  
  public void hashFuction2( String[] stringsForArray){ // Table5 to Table6
    for ( int i = 0 ; i < stringsForArray.length ; i++ ) {
      String newElementVal = stringsForArray[i] ;
      
      long arrayIndex = 0 ;
      for ( int  y = 0 ; y < newElementVal.length() ; y++ ) { // ASCII�ۥ[
    	 arrayIndex =  arrayIndex + newElementVal.charAt(y) ; 
      }
    	  
      arrayIndex = arrayIndex % 100; //��100�l��
      //System.out.println(arrayIndex);
      
	while(theArray[(int) arrayIndex] != "-1" ) { //collision push back one position
      arrayIndex++ ; 
      arrayIndex %= arraySize;

    } // while
      
      theArray[(int) arrayIndex] = newElementVal ; // give the Value inside the array
    } // for
    
    
  } // hashFucntion2
  
  public boolean isPrime(int number) { // It's Prime or not
	  if ( number % 2 == 0 ) 
	    return false ;
	  
	  for ( int i = 3 ; i * i <= number ; i += 2 ) {
	    if ( number % i == 0 )
	      return false ;
	  } //for
	  
	  return true ;
  } //isPrime
  
  public int getNextPrime( int minNumberToCheck) { // Finde the Prime bigger than initial ArraySize
	for ( int i = minNumberToCheck ; true; i++) 
	  if ( isPrime(i)) 
	    return i ;
  } // getNextPrime
  
  public String[] getArray() {
	  
	return theArray;
	  
  } // getArray()
  
  public void cleanArray() { // Delete the null  inside the Array 
      theArray = Arrays.stream(theArray)
              .filter(s -> (s != "-1" && s.length() > 0))
              .toArray(String[]::new);  
              
              
  } // cleanArray() 
   
  
} // Class HashFunction
