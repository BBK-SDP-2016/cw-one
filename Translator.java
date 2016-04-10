package sml;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.util.NoSuchElementException;

/*
 * The translator of a <b>S</b><b>M</b>al<b>L</b> program.
 */
public class Translator {

    // word + line is the part of the current line that's not yet processed
    // word has no whitespace
    // If word and line are not empty, line begins with whitespace
    private String line = "";
    private Labels labels; // The labels of the program being translated
    private ArrayList<Instruction> program; // The program to be created
    private String fileName; // source file of SML code

    private static final String SRC = "src";

    public Translator(String fileName) {
        this.fileName = SRC + "/" + fileName;
    }

    // translate the small program in the file into lab (the labels) and
    // prog (the program)
    // return "no errors were detected"
    public boolean readAndTranslate(Labels lab, ArrayList<Instruction> prog) {
		try (Scanner sc = new Scanner(new File(fileName))) {
			// Scanner attached to the file chosen by the user
			labels = lab;
			labels.reset();
			program = prog;
			program.clear();

			try {
				line = sc.nextLine();
			} catch (NoSuchElementException ioE) {
				return false;
			}

			// Each iteration processes line and reads the next line into line
			while (line != null) {
				// Store the label in label
				String label = scan();

				if (label.length() > 0) {
					Instruction ins = getInstruction(label);
					if (ins != null) {
						labels.addLabel(label);
						program.add(ins);
					}
				}

				try {
					line = sc.nextLine();
				} catch (NoSuchElementException ioE) {
					return false;
				}
			}
		} catch (IOException ioE) {
			System.out.println("File: IO error " + ioE.getMessage());
			return false;
		}
		return true;
	}

    // line should consist of an MML instruction, with its label already
    // removed. Translate line into an instruction with label label
    // and return the instruction
    public Instruction getInstruction(String label) {
        if (line.equals(""))
            return null;

        String ins = scan();
        //System.out.println("Instruction from scan() method: " + ins);                      
        try {
            //build up class name dynamically from supplied instruction
            Class<?> className = Class.forName(getClassName(ins));
            //System.out.println("className dynamically built up: " + className);         
            Class<?>[] parameterList = getConstructorParamTypes(className.getDeclaredConstructors()[0]);
            List<Object> params = new ArrayList<>();
            params.add(label);
            for (int i = params.size(); i < parameterList.length; i++) {
                if (parameterList[i].isPrimitive()) {
                	params.add(scanInt());
                }
                else {
                	params.add(scan());
                }                       
            }
            return (Instruction) className.getDeclaredConstructor(parameterList).newInstance(params.toArray());
        	} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            System.out.println("An exception was raised attempting to locate: " + e.getMessage());               
          }
        return null;
    }

    private String scan() {
		line = line.trim();
		if (line.length() == 0)
			return "";

		int i = 0;
		while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
			i = i + 1;
		}
		String word = line.substring(0, i);
		line = line.substring(i);
		return word;
	}
    /*
     * Return the first word of line as an integer. If there is any error, return the maximum int
     */ 
    public int scanInt() {
        String word = scan();
        if (word.length() == 0) {
            return Integer.MAX_VALUE;
        }

        try {
            return Integer.parseInt(word);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    /*
     * Method to build up and return a class name dynamically, from a supplied instruction
     */    
    private String getClassName(String ins) {
        return "sml."  + ins.substring(0, 1).toUpperCase() + ins.substring(1, ins.length()) + "Instruction";
    }

    /*
     * Reflective method to determine the class constructor parameter types
     */
    private Class<?>[] getConstructorParamTypes(Constructor<?> constructor) {
        List<Class<?>> classCollection = Arrays.stream(constructor.getParameterTypes()).map(c -> {
            if (c.isPrimitive()) {
                return int.class;
            } else {
                try {
                    return c.newInstance().getClass();
                } catch (IllegalAccessException | InstantiationException e) {
                    System.out.println("The following exception was raised attempting to obtain paramater types: " + e.getMessage());
                }
            }
            return null;
        }).collect(Collectors.toList());
        return classCollection.toArray(new Class<?>[classCollection.size()]);
    }
}