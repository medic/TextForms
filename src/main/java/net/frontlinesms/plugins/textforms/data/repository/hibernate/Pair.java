package net.frontlinesms.plugins.textforms.data.repository.hibernate;

public class Pair<A,B> {
	
	private A a;
	private B b;
	
	public Pair(A a, B b){
		this.a = a;
		this.b = b;
	}
	
	public void setB(B b) {
		this.b = b;
	}
	
	public B getB() {
		return b;
	}
	
	public void setA(A a) {
		this.a = a;
	}
	
	public A getA() {
		return a;
	}
}
