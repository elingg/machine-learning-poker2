package edu.stanford.cs229.ml;

import java.io.Serializable;

public class Fraction implements Serializable {

	private int numerator;
	private int denominator;
	
	public Fraction(int n, int d)
	{
		numerator=n;
		denominator=d;
	}
	
	public int getNumerator() {
		return numerator;
	}
	public void setNumerator(int numerator) {
		this.numerator = numerator;
	}
	public int getDenominator() {
		return denominator;
	}
	public void setDenominator(int denominator) {
		this.denominator = denominator;
	}
	
	public String toString() {
		return Float.toString((float) numerator / (float) denominator);
	}
	
	
}
