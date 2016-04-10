package sml;

/**
 * This class makes the statement labelled L2 the next one to execute,
 * if the contents of register 'register' is not zero 
 *
 * @author Daryl Smith
 */


public class BnzInstruction extends Instruction {

	private int register;
	private String L2;
	
	public BnzInstruction(String label, String op) {
		super(label, op);
	}
		
	public BnzInstruction(String label, int register, String L2) {
		this(label, "bnz");
		this.register = register;
		this.L2 = L2.trim();
	}
	
	@Override
	public void execute(Machine m) {
		if (m.getRegisters().getRegister(register) != 0)
			m.setPc(m.getLabels().indexOf(L2));
	}
	
	@Override
	public String toString() {
		return super.toString() + " register " + register + " value is " + L2;
	}
}