import java.io.*;
import java.util.*;

// Class for Symbol Table Entry
class Symbol {
    String name;
    int address;
    Symbol(String name, int address) {
        this.name = name;
        this.address = address;
    }
}

// Class for Literal Table Entry
class Literal {
    String value;
    int address;
    Literal(String value, int address) {
        this.value = value;
        this.address = address;
    }
}

// Class for OpCode Table Entry
class OpCode {
    String mnemonic;
    String code;
    int length;
    OpCode(String mnemonic, String code, int length) {
        this.mnemonic = mnemonic;
        this.code = code;
        this.length = length;
    }
}

public class PassOneAssembler {
    static List<Symbol> symbolTable = new ArrayList<>();
    static List<Literal> literalTable = new ArrayList<>();
    static List<OpCode> opCodeTable = Arrays.asList(
        new OpCode("ADD", "01", 1),
        new OpCode("SUB", "02", 1),
        new OpCode("MOVEM", "04", 1),
        new OpCode("MOVER", "05", 1),
        new OpCode("STOP", "00", 1)
    );
    static int locationCounter = 0;

    // Checks if symbol is already present
    static Symbol findSymbol(String name) {
        for (Symbol s : symbolTable) {
            if (s.name.equals(name)) return s;
        }
        return null;
    }

    // Parse single line and update tables/LC
    static void parseLine(String line) {
        String[] tokens = line.trim().split("\\s+");
        int idx = 0;

        // Handle label
        if (tokens.length > 2 && !Arrays.asList("START", "STOP", "END", "DS", "DC", "EQU", "LTORG", "ORIGIN").contains(tokens[1])) {
            if (findSymbol(tokens[0]) == null) {
                symbolTable.add(new Symbol(tokens[0], locationCounter));
            }
            idx = 1;
        }

        String opcode = tokens[idx];
        String operand = tokens.length > idx + 1 ? tokens[idx + 1] : "";

        switch (opcode) {
            case "START":
                locationCounter = Integer.parseInt(operand);
                break;
            case "DS":
            case "DC":
                // Declaration Directive: update symbol table and LC
                if (findSymbol(opcode) == null && tokens.length > idx - 1)
                    symbolTable.add(new Symbol(tokens[idx-1], locationCounter));
                locationCounter += Integer.parseInt(operand.replaceAll("'|\"", ""));
                break;
            case "EQU":
                // EQU Directive: assign value to symbol
                Symbol sym = findSymbol(tokens[idx-1]);
                if (sym != null) sym.address = Integer.parseInt(operand);
                break;
            case "LTORG":
            case "END":
                // Assign addresses to literals
                for (Literal lit : literalTable) {
                    if (lit.address == -1) {
                        lit.address = locationCounter++;
                    }
                }
                break;
            case "ORIGIN":
                locationCounter = Integer.parseInt(operand);
                break;
            default:
                // Any operation statement
                // Check for literal (e.g., ='12')
                if (operand.startsWith("='") && findLiteral(operand) == null) {
                    literalTable.add(new Literal(operand, -1));
                }
                locationCounter++;
                break;
        }
    }

    static Literal findLiteral(String value) {
        for (Literal l : literalTable) {
            if (l.value.equals(value)) return l;
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("input.asm"));
        String line;
        System.out.println("Pass-I: Intermediate Code Generation\n");
        while ((line = br.readLine()) != null && !line.trim().equals("")) {
            parseLine(line);
        }

        System.out.println("\nSymbol Table:");
        symbolTable.forEach(s -> System.out.println(s.name + "\t" + s.address));

        System.out.println("\nLiteral Table:");
        literalTable.forEach(l -> System.out.println(l.value + "\t" + l.address));
    }
}
