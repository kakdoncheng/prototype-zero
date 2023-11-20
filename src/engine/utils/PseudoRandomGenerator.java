package engine.utils;

import java.util.Random;

public class PseudoRandomGenerator {
	
	private Random r;
	private String alphabet=" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
	
	public PseudoRandomGenerator(long seed){
		r=new Random(seed);
		alphabet="";
		for(char c=0; c<128; c++){
			alphabet+=c;
		}
	}
	
	private char $charAt(int a){
		return this.alphabet.charAt(a);
	}
	
	public int $int(int lower, int upper){
		return lower+r.nextInt((upper-lower));
	}
	
	public double $double(double lower, double upper){
		return lower+(r.nextDouble()*(upper-lower));
	}
	
	public char $char(){
		return $charAt($int(0,alphabet.length()));
	}
	
	public String $string(int length){
		String out="";
		for(int i=0; i<length; i++)
			out+=$char();
		return out;
	}
}
