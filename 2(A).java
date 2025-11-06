import java.io.*;
import java.util.*;

class SymTab {
    public static void main(String args[]) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java SymTab <input_file>");
            return;
        }
        
        FileReader FP = new FileReader(args[0]);
        BufferedReader bufferedReader = new BufferedReader(FP);
        
        String line = null;
        int line_count = 0, LC = 0, symTabLine = 0, opTabLine = 0, litTabLine = 0, poolTabLine = 0;
        
        // Data Structures
        final int MAX = 100;
        String SymbolTab[][] = new String[MAX][3];
        String OpTab[][] = new String[MAX][3];
        String LitTab[][] = new String[MAX][2];
        int PoolTab[] = new int[MAX];
        HashSet<String> opCodeSet = new HashSet<>();  // To avoid duplicate opcodes
        
        System.out.println("___________________________________________________");
        
        while ((line = bufferedReader.readLine()) != null) {
            String[] tokens = line.split("\t");
            
            // Handle first line (START directive)
            if (line_count == 0) {
                if (tokens.length > 2) {
                    LC = Integer.parseInt(tokens[2]);  // Set LC to operand of START
                }
                for (int i = 0; i < tokens.length; i++) {
                    System.out.print(tokens[i] + "\t");
                }
                System.out.println("");
            } else {
                // Print the input program line
                for (int i = 0; i < tokens.length; i++) {
                    System.out.print(tokens[i] + "\t");
                }
                System.out.println("");
                
                // Check if label exists (tokens[0] is not empty)
                if (tokens.length > 0 && !tokens[0].trim().equals("")) {
                    // Inserting into Symbol Table
                    SymbolTab[symTabLine][0] = tokens[0];
                    SymbolTab[symTabLine][1] = Integer.toString(LC);
                    SymbolTab[symTabLine][2] = Integer.toString(1);
                    symTabLine++;
                }
                
                // Check for mnemonic and add to opcode table
                if (tokens.length > 1 && tokens[1] != null && !tokens[1].trim().equals("")) {
                    String mnemonic = tokens[1];
                    
                    // Only add if not already in opcode table
                    if (!opCodeSet.contains(mnemonic)) {
                        OpTab[opTabLine][0] = mnemonic;
                        
                        // Classify the mnemonic
                        if (mnemonic.equalsIgnoreCase("START") || 
                            mnemonic.equalsIgnoreCase("END") || 
                            mnemonic.equalsIgnoreCase("ORIGIN") || 
                            mnemonic.equalsIgnoreCase("EQU") || 
                            mnemonic.equalsIgnoreCase("LTORG")) {
                            OpTab[opTabLine][1] = "AD";
                            OpTab[opTabLine][2] = "R11";
                        } else if (mnemonic.equalsIgnoreCase("DS") || 
                                   mnemonic.equalsIgnoreCase("DC")) {
                            OpTab[opTabLine][1] = "DL";
                            OpTab[opTabLine][2] = "R7";
                        } else {
                            OpTab[opTabLine][1] = "IS";
                            OpTab[opTabLine][2] = "(04,1)";
                        }
                        
                        opCodeSet.add(mnemonic);
                        opTabLine++;
                    }
                    
                    // Handle DS/DC statements - increment LC by operand size
                    if (tokens.length > 2 && 
                        (mnemonic.equalsIgnoreCase("DS") || mnemonic.equalsIgnoreCase("DC"))) {
                        try {
                            int size = Integer.parseInt(tokens[2]);
                            LC += size - 1;  // -1 because LC++ happens at end
                        } catch (NumberFormatException e) {
                            // If operand is not a number, treat as size 1
                        }
                    }
                    
                    // Handle LTORG directive - assign addresses to pending literals
                    if (mnemonic.equalsIgnoreCase("LTORG") || mnemonic.equalsIgnoreCase("END")) {
                        // Mark start of new literal pool
                        if (litTabLine > poolTabLine) {
                            PoolTab[poolTabLine] = poolTabLine + 1;
                            poolTabLine++;
                        }
                    }
                }
                
                // Check for literals (operand starting with '=')
                if (tokens.length > 2 && tokens[2] != null && 
                    tokens[2].length() > 0 && tokens[2].charAt(0) == '=') {
                    // Entry of literals into literal table (address assigned later)
                    LitTab[litTabLine][0] = tokens[2];
                    LitTab[litTabLine][1] = "";  // Address assigned at LTORG/END
                    litTabLine++;
                }
            }
            
            line_count++;
            LC++;
        }
        
        System.out.println("___________________________________________________");
        
        // Print symbol table
        System.out.println("\n\n               SYMBOL TABLE                 ");
        System.out.println("--------------------------");
        System.out.println("SYMBOL\tADDRESS\tLENGTH");
        System.out.println("--------------------------");
        for (int i = 0; i < symTabLine; i++) {
            System.out.println(SymbolTab[i][0] + "\t" + SymbolTab[i][1] + "\t" + SymbolTab[i][2]);
        }
        System.out.println("--------------------------");
        
        // Print opcode table
        System.out.println("\n\n               OPCODE TABLE                 ");
        System.out.println("----------------------------");
        System.out.println("MNEMONIC\tCLASS\tINFO");
        System.out.println("----------------------------");
        for (int i = 0; i < opTabLine; i++) {
            System.out.println(OpTab[i][0] + "\t\t" + OpTab[i][1] + "\t" + OpTab[i][2]);
        }
        System.out.println("----------------------------");
        
        // Print literal table
        System.out.println("\n\n   LITERAL TABLE                             ");
        System.out.println("-----------------");
        System.out.println("LITERAL\tADDRESS");
        System.out.println("-----------------");
        for (int i = 0; i < litTabLine; i++) {
            System.out.println(LitTab[i][0] + "\t" + LitTab[i][1]);
        }
        System.out.println("------------------");
        
        // Initialization of POOLTAB - Fixed loop boundary
        for (int i = 0; i < litTabLine - 1; i++) {  // Fixed: i < litTabLine - 1
            if (LitTab[i][0] != null && LitTab[i + 1][0] != null) {
                if (i == 0) {
                    PoolTab[poolTabLine] = i + 1;
                    poolTabLine++;
                } else if (!LitTab[i][1].isEmpty() && !LitTab[i + 1][1].isEmpty()) {
                    // Check if there's a gap in addresses
                    try {
                        int currentAddr = Integer.parseInt(LitTab[i][1]);
                        int nextAddr = Integer.parseInt(LitTab[i + 1][1]);
                        if (currentAddr < nextAddr - 1) {
                            PoolTab[poolTabLine] = i + 2;
                            poolTabLine++;
                        }
                    } catch (NumberFormatException e) {
                        // Skip if addresses are not valid
                    }
                }
            }
        }
        
        // Print pool table
        System.out.println("\n\n   POOL TABLE                  ");
        System.out.println("-----------------");
        System.out.println("LITERAL NUMBER");
        System.out.println("-----------------");
        for (int i = 0; i < poolTabLine; i++) {
            System.out.println(PoolTab[i]);
        }
        System.out.println("------------------");
        
        // Always close files
        bufferedReader.close();
    }
}
