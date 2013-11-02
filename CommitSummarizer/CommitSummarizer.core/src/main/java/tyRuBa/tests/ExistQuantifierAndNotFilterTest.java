package tyRuBa.tests;

import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class ExistQuantifierAndNotFilterTest extends TyrubaTest {

	public ExistQuantifierAndNotFilterTest(String arg0) {
		super(arg0);
	}
	
	public void setUp() throws Exception {
		TyrubaTest.initfile = false;
		super.setUp();
	}

	public void testSimpleNot() throws ParseException, TypeModeError {
		frontend.parse("planet :: String\n" 
			+ "MODES (F) IS MULTI END\n");

		frontend.parse("orbits :: String, String\n"
			+ "MODES\n"
			+ "(B,F) IS SEMIDET\n"
			+ "(F,F)  IS MULTI\n"
			+ "(F,B) IS NONDET\n"
			+ "END\n");

		frontend.parse("no_moon :: String\n"
			+ "MODES (F) IS NONDET END\n");

		frontend.parse("no_moon(?x) :- " +
	}
	
	public void testBadNot() throws ParseException, TypeModeError {
		frontend.parse("planet :: String\n" 
			+ "MODES (F) IS MULTI END\n");

		frontend.parse("orbits :: String, String\n"
			+ "MODES\n"
			+ "(B,F) IS SEMIDET\n"
			+ "(F,F)  IS MULTI\n"
			+ "(F,B) IS NONDET\n"
			+ "END\n");

		frontend.parse("no_moon :: String\n"
			+ "MODES (F) IS NONDET END\n");
		try {
			frontend.parse("no_moon(?x) :- NOT(orbits(?m,?x)), planet(?x).");
			fail("This should have thrown a TypeModeError because " +
		} catch (TypeModeError e) {
		}
	}
	
	public void testBadExist() throws Exception {
		TyrubaTest.initfile = true;
		super.setUp();
		
		frontend.parse("foo :: ?t\n" +
		
		try {
			frontend.parse("foo(?x) :- EXISTS ?x : member(?x,[bar]).");
			fail("This should have thrown a TypeModeError because " +
		} catch (TypeModeError e) {
			System.err.println(e.getMessage());
		}
		
		frontend.parse("foo1 :: String\n" +
		frontend.parse("foo1(?x) :- " +
		test_must_succeed("foo1(bar)");
		test_must_succeed("foo1(foo)");
	}

}