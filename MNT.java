import java.io.*;
import java.util.HashMap;
import java.util.Vector;

public class MacroPass2 {
    public static void main(String[] args) {
        HashMap<Integer, String> aptab = new HashMap<>();
        HashMap<String, Integer> aptabInverse = new HashMap<>();
        HashMap<String, Integer> mdtpHash = new HashMap<>();
        HashMap<String, Integer> kpdtpHash = new HashMap<>();
        HashMap<String, Integer> kpHash = new HashMap<>();
        HashMap<String, Integer> macroNameHash = new HashMap<>();
        Vector<String> mdt = new Vector<>();
        Vector<String> kpdt = new Vector<>();

        // Using try-with-resources for automatic resource management
        try (BufferedReader b1 = new BufferedReader(new FileReader("intermediate.txt"));
             BufferedReader b2 = new BufferedReader(new FileReader("mnt.txt"));
             BufferedReader b3 = new BufferedReader(new FileReader("mdt.txt"));
             BufferedReader b4 = new BufferedReader(new FileReader("kpdt.txt"));
             FileWriter f1 = new FileWriter("Pass2.txt")) {

            String s, s1;
            int pp, kp, kpdtp, mdtp, paramNo;

            // Load MDT (Macro Definition Table)
            while ((s = b3.readLine()) != null) {
                mdt.addElement(s);
            }

            // Load KPDT (Keyword Parameter Default Table)
            while ((s = b4.readLine()) != null) {
                kpdt.addElement(s);
            }

            // Load MNT (Macro Name Table)
            while ((s = b2.readLine()) != null) {
                String[] word = s.split("\t");
                if (word.length >= 5) {
                    s1 = word[0] + word[1];
                    macroNameHash.put(word[0], 1);
                    kpHash.put(s1, Integer.parseInt(word[2]));
                    mdtpHash.put(s1, Integer.parseInt(word[3]));
                    kpdtpHash.put(s1, Integer.parseInt(word[4]));
                }
            }

            // Process intermediate file
            while ((s = b1.readLine()) != null) {
                String[] b1Split = s.split("\\s+");
                
                if (b1Split.length > 0 && macroNameHash.containsKey(b1Split[0])) {
                    if (b1Split.length < 2) {
                        f1.write("+ " + s + "\n");
                        continue;
                    }

                    // Calculate positional and keyword parameters
                    String[] paramSplit = b1Split[1].split(",");
                    int equalCount = b1Split[1].split("=", -1).length - 1;
                    pp = paramSplit.length - equalCount;

                    String key = b1Split[0] + Integer.toString(pp);
                    if (!kpHash.containsKey(key)) {
                        f1.write("+ " + s + "\n");
                        continue;
                    }

                    kp = kpHash.get(key);
                    mdtp = mdtpHash.get(key);
                    kpdtp = kpdtpHash.get(key);

                    String[] actualParams = b1Split[1].split(",");
                    paramNo = 1;

                    // Process positional parameters
                    for (int j = 0; j < pp; j++) {
                        aptab.put(paramNo, actualParams[j]);
                        aptabInverse.put(actualParams[j], paramNo);
                        paramNo++;
                    }

                    // Process keyword parameters with default values
                    int i = kpdtp - 1;
                    for (int j = 0; j < kp; j++) {
                        if (i < kpdt.size()) {
                            String[] temp = kpdt.get(i).split("\t");
                            if (temp.length >= 2) {
                                aptab.put(paramNo, temp[1]);
                                aptabInverse.put(temp[0], paramNo);
                            }
                            i++;
                            paramNo++;
                        }
                    }

                    // Process actual keyword parameters
                    i = pp;
                    while (i < actualParams.length) {
                        if (actualParams[i].contains("=")) {
                            String[] initializedParams = actualParams[i].split("=");
                            if (initializedParams.length >= 2) {
                                String paramName = initializedParams[0];
                                // Remove leading '&' if present
                                if (paramName.startsWith("&")) {
                                    paramName = paramName.substring(1);
                                }
                                if (aptabInverse.containsKey(paramName)) {
                                    aptab.put(aptabInverse.get(paramName), initializedParams[1]);
                                }
                            }
                        }
                        i++;
                    }

                    // Expand macro definition
                    i = mdtp - 1;
                    while (i < mdt.size() && !mdt.get(i).equalsIgnoreCase("MEND")) {
                        StringBuilder line = new StringBuilder("+ ");
                        String mdtLine = mdt.get(i);
                        
                        for (int j = 0; j < mdtLine.length(); j++) {
                            if (mdtLine.charAt(j) == '#' && j + 1 < mdtLine.length()) {
                                j++;
                                if (Character.isDigit(mdtLine.charAt(j))) {
                                    int paramIndex = Character.getNumericValue(mdtLine.charAt(j));
                                    if (aptab.containsKey(paramIndex)) {
                                        line.append(aptab.get(paramIndex));
                                    }
                                }
                            } else {
                                line.append(mdtLine.charAt(j));
                            }
                        }
                        f1.write(line.toString() + "\n");
                        i++;
                    }

                    // Clear APTAB for next macro call
                    aptab.clear();
                    aptabInverse.clear();
                } else {
                    f1.write("+ " + s + "\n");
                }
            }

            System.out.println("Pass 2 completed successfully. Output written to Pass2.txt");

        } catch (FileNotFoundException e) {
            System.err.println("Error: Required input file not found - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error: I/O exception occurred - " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number format in input files - " + e.getMessage());
        }
    }
}
