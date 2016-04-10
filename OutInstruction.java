package sml;

/**
 * This class prints the contents of register op1 on the Java console (using println) 
 * 
 * @author Daryl Smith
 */

public class OutInstruction extends Instruction {

	private int op1;

	public OutInstruction(String label, int op1) {
		super(label, "out");
		this.op1 = op1;
	}

	@Override
	public void execute(Machine m) {
		int value1 = m.getRegisters().getRegister(op1);
		System.out.println("Value of register " + op1 + " is " + value1);
	}

	@Override
	public String toString() {
		return super.toString() + " " + op1 + " to console ";
	}	
}