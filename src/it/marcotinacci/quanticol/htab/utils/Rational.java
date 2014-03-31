package it.marcotinacci.quanticol.htab.utils;

import java.math.BigInteger;
import java.security.InvalidParameterException;

// FIXME immuatable class

/**
 * Rational numbers class. It keeps numerator and denominator in order to 
 * maintain the accuracy of a rational number and can be implied to represent
 * probabilities without loss of precision after many operations.
 * @author Marco Tinacci
 *
 */
public class Rational {

	public static void main(String[] args) {
		// tests
		Rational a = new Rational(1,10); // a = 1/3
		System.out.println("a = "+ a);
		System.out.println("double(a) = "+ a.getDouble());
		
		Rational b = new Rational(9,10); // b = 4/6
		System.out.println("b = "+ b);
		System.out.println("double(b) = "+ b.getDouble());
		
		Rational c = Rational.times(a,b); // c = a+b = 1
		System.out.println("c = a * b = "+c.getDouble());
		System.out.println("c = double(a) * double(b) = "+ (a.getDouble()*b.getDouble()));
		
		Rational z = new Rational(0,10); // z = 0
		System.out.println("z = " + z);
		System.out.println("z == 0 ? "+ z.equals(ZERO));
		System.out.println("a == 0 ? "+ a.equals(ZERO));
	}

	////////////////////////////////////////////////////////////
	// 						DATA							  //
	////////////////////////////////////////////////////////////
	
	private BigInteger _numerator;
	private BigInteger _denominator;
	
	////////////////////////////////////////////////////////////
	// 						METHODS						  	  //
	////////////////////////////////////////////////////////////
	
	public Rational(Integer num, Integer den) {
		this(BigInteger.valueOf(num),BigInteger.valueOf(den));
	}
	
	public Rational(BigInteger num, BigInteger den){
		_numerator = num;
		if(den.equals(BigInteger.ZERO)) 
			throw new InvalidParameterException();
		else
			_denominator = den;
		reduce();
	}
	
	public Double getDouble(){
		return _numerator.doubleValue()/_denominator.doubleValue();
	}
	
	public void add(Rational a){
		Rational r = Rational.sum(this, a);
		_numerator = r.getNumerator();
		_denominator = r.getDenumerator();
	}
	
	public void mult(Rational a){
		Rational r = Rational.times(this, a);
		_numerator = r.getNumerator();
		_denominator = r.getDenumerator();
	}

	public void div(Rational a){
		Rational r = Rational.divide(this, a);
		_numerator = r.getNumerator();
		_denominator = r.getDenumerator();
	}
	
	protected void reduce(){
		// greatest common divisor
		BigInteger gcd = _numerator.gcd(_denominator);
		// reduce numerator
		_numerator = _numerator.divide(gcd);
		// reduce denominator
		_denominator = _denominator.divide(gcd);
	}
	
	////////////////////////////////////////////////////////////
	// 					STATIC METHODS						  //
	////////////////////////////////////////////////////////////
	
	// FIXME zero and one are not immutable objects
	public static final Rational ZERO = new Rational(0,1);
	public static final Rational ONE = new Rational(1,1);
	
	public static Rational sum(Rational a, Rational b){
		return new Rational(
				a.getNumerator().multiply(b.getDenumerator()).add(b.getNumerator().multiply(a.getDenumerator())), 
				a.getDenumerator().multiply(b.getDenumerator()));
	}
	
	public static Rational times(Rational a, Rational b){
		return new Rational(
				a.getNumerator().multiply(b.getNumerator()), 
				a.getDenumerator().multiply(b.getDenumerator()));
	}
	
	public static Rational divide(Rational a, Rational b){
		return Rational.times(a, Rational.inverse(b));
	}
	
	public static Rational inverse(Rational a){
		return new Rational(a.getDenumerator(), a.getNumerator());
	}
	
//	public static Integer gcd(Integer a, Integer b) {
//		Integer t;
//		while(b != 0){
//			t = b;
//			b = a % b;
//			a = t;
//		}
//		return a;
//	}
	
	////////////////////////////////////////////////////////////
	// 					GETTERS & SETTERS					  //
	////////////////////////////////////////////////////////////
	
	public BigInteger getNumerator() {
		return _numerator;
	}

	public void setNumerator(Integer num) {
		this._numerator = BigInteger.valueOf(num);
		reduce();
	}

	public BigInteger getDenumerator() {
		return _denominator;
	}

	public void setDenumerator(Integer den) {
		if(den == 0) 
			throw new InvalidParameterException();
		else{
			this._denominator = BigInteger.valueOf(den);
			reduce();
		}
	}

	////////////////////////////////////////////////////////////
	// 						OVERRIDES						  //
	////////////////////////////////////////////////////////////
	
	@Override
	public String toString() {
		//return getDouble().toString();
		return _numerator.toString() + "/" + _denominator.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_denominator == null) ? 0 : _denominator.hashCode());
		result = prime * result
				+ ((_numerator == null) ? 0 : _numerator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rational other = (Rational) obj;
		if (_denominator == null) {
			if (other._denominator != null)
				return false;
		} else if (!_denominator.equals(other._denominator))
			return false;
		if (_numerator == null) {
			if (other._numerator != null)
				return false;
		} else if (!_numerator.equals(other._numerator))
			return false;
		return true;
	}	
}
