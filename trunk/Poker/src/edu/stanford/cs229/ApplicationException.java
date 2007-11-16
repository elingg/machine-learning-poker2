package edu.stanford.cs229;

//Represents a general application exception
public class ApplicationException extends Exception{
  public ApplicationException() {
	  super();
  }
  
  public ApplicationException(String s) {
	  super(s);
  }
  
  public ApplicationException(Throwable t) {
	  super(t);
  }
}
