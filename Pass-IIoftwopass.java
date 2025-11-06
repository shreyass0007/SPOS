import java.io.*;
import java.util.*;

// Symbol table entry
class Symbol {
    String name;
    int address;
    Symbol(String name, int address) {
        this.name = name;
        this.address = address;
    }
}

// Literal table entry
class Literal {
    String value;
    int address;
    Literal(String value, int address) {
        this.value = value;
        this.address = address;
    }
}

public class PassTwoAssembler {
    static List<Symbol> symbolTable = new ArrayList<>();
    static List<Literal> literalTable = new ArrayList<>();

    // Read symbol table from file
    static void readSymbolTable(String filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\s+");
            if (parts.length >= 2)
                symbolTable.add(new Symbol(parts[0], Integer.parseInt(parts[1])));
        }
        br.close();
    }

    // Read literal table from file
    static void readLiteralTable(String filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\s+");
            if (parts.length >= 2)
                literalTable.add(new Literal(parts[0], Integer.parseInt(parts[1])));
        }
        br.close();
    }

    // Find address from symbol table
    static int getSymbolAddress(String name) {
        for (Symbol s : symbolTable) {
            if (s.name.equals(name)) return s.address;
        }
        return -1;
    }

    // Find address from literal table
    static int getLiteralAddress(String value) {
        for (Literal l : literalTable) {
            if (l.value.equals(value)) return l.address;
        }
        return -1;
    }

    public static void main(String[] args) throws Exception {
        // Input file names—update as needed
        String intermediateFile = "intermediate.txt";
        String symbolFile = "symtab.txt";
        String literalFile = "littab.txt";

        readSymbolTable(symbolFile);
        readLiteralTable(literalFile);

        BufferedReader br = new BufferedReader(new FileReader(intermediateFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter("machinecode.txt"));

        String line;
        System.out.println("LC\tMachine Code");
        while ((line = br.readLine()) != null && !line.trim().equals("")) {
            String[] tokens = line.trim().split("\\s+");
            if (tokens.length < 3) continue;

            String lc = tokens[0];
            String opcode = tokens[1];
            String operand = tokens[2];

            int operandAddr = -1;
            if (operand.startsWith("=")) {
                operandAddr = getLiteralAddress(operand);
            } else if (operand.matches("[A-Za-z]+")) {
                operandAddr = getSymbolAddress(operand);
            } else {
                operandAddr = Integer.parseInt(operand);
            }

            String machineLine = lc + "\t" + opcode + "\t" + operandAddr;
            bw.write(machineLine + "\n");
            System.out.println(machineLine);
        }
        br.close();
        bw.close();
    }
}
