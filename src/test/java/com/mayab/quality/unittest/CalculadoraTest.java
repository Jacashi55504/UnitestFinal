package com.mayab.quality.unittest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CalculadoraTest {

	@Test
	void test() {
		fail("Not yet implemented");
	}
	@Test
	void test2() {
		System.out.println("Hola que hace testeando el 2");
	}
	@Test
	void testSuma() {
		//Setup
		Calculadora calculadora = new Calculadora();
		double num1 = 10;
		double num2 = 5;
		double expResult = 15;
		//Excercise
		double result = calculadora.suma(num1, num2);
		//Assertion
		assertEquals(result, expResult);
		
	}

}
