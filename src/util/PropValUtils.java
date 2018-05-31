package util;

import java.util.Arrays;

/**
 * A utility class contains methods to covert Object to String format used in properties files, and vice-versa.
 *
 * @author Ben Hui
 * @version 20180531
 */
public class PropValUtils {

    public static String objectToPropStr(Object ent, Class cls) {
        String res = "";
        if (ent != null) {
            if (boolean[].class.equals(ent)) {
                res = Arrays.toString((boolean[]) ent);
            } else if (int[].class.equals(cls)) {
                res = Arrays.toString((int[]) ent);
            } else if (float[].class.equals(cls)) {
                res = Arrays.toString((float[]) ent);
            } else if (double[].class.equals(cls)) {
                res = Arrays.toString((double[]) ent);
            } else if (cls.isArray()) {
                res = Arrays.deepToString((Object[]) ent);
            } else {
                res = ent.toString();
            }
        }
        return res;
    }

    public static Object propStrToObject(String ent, Class cls) {
        Object res = null;
        if (ent != null && !ent.isEmpty()) {
            if ("null".equalsIgnoreCase(ent)) {
                res = null;
            } else if (String.class.equals(cls)) {
                res = ent;
            } else if (Integer.class.equals(cls)) {
                res = Integer.valueOf(ent);
            } else if (Long.class.equals(cls)) {
                res = Long.valueOf(ent);
            } else if (Boolean.class.equals(cls)) {
                res = Boolean.valueOf(ent);
            } else if (Float.class.equals(cls)) {
                res = Float.valueOf(ent);
            } else if (cls.isArray()) {
                res = parsePrimitiveArray(ent, cls);
            } else {
                System.err.print("PropValUtils.propToObject: Parsing of '" + ent + "' to class " + cls.getName() + " not yet supported.");
            }
        }
        return res;
    }

    private static Object parsePrimitiveArray(String arrayStr, Class c) {
        Object res = null;
        try {
            if (arrayStr != null && !arrayStr.isEmpty() && !"null".equalsIgnoreCase(arrayStr)) {

                String numOnly = arrayStr.substring(1, arrayStr.length() - 1); // Exclude the ending brackets
                String[] splitNum = numOnly.split(",");

                if (splitNum.length == 1 && splitNum[0].isEmpty()) { // Special case for empty string
                    splitNum = new String[0];
                }
                if (boolean[].class.equals(c)) {
                    res = new boolean[splitNum.length];
                    for (int i = 0; i < ((boolean[]) res).length; i++) {
                        ((boolean[]) res)[i] = Boolean.getBoolean(splitNum[i].trim());
                    }
                } else if (int[].class.equals(c)) {
                    res = new int[splitNum.length];
                    for (int i = 0; i < ((int[]) res).length; i++) {
                        ((int[]) res)[i] = Integer.parseInt(splitNum[i].trim());
                    }
                } else if (float[].class.equals(c)) {
                    res = new float[splitNum.length];

                    for (int i = 0; i < ((float[]) res).length; i++) {
                        ((float[]) res)[i] = Float.parseFloat(splitNum[i].trim());
                    }

                } else if (double[].class.equals(c)) {
                    res = new double[splitNum.length];
                    for (int i = 0; i < ((double[]) res).length; i++) {
                        ((double[]) res)[i] = Double.parseDouble(splitNum[i].trim());
                    }
                } else if (c.isArray()) {

                    int numGenArr = numOnly.trim().length() == 0 ? 0 : 1;
                    int bBal = 0;

                    for (int b = 0; b < numOnly.length(); b++) {
                        if (numOnly.charAt(b) == '[') {
                            bBal++;
                        } else if (numOnly.charAt(b) == ']') {
                            bBal--;
                        } else if (numOnly.charAt(b) == ',' && bBal == 0) {
                            numGenArr++;
                        }
                    }

                    StringBuilder subString = new StringBuilder();
                    res = java.lang.reflect.Array.newInstance(c.getComponentType(), numGenArr);

                    int entNum = 0;
                    for (int b = 0; b < numOnly.length(); b++) {
                        if (numOnly.charAt(b) == '[') {
                            bBal++;
                        } else if (numOnly.charAt(b) == ']') {
                            bBal--;
                        }
                        if (numOnly.charAt(b) == ',' && bBal == 0) {
                            ((Object[]) res)[entNum] = parsePrimitiveArray(subString.toString().trim(), c.getComponentType());
                            subString = new StringBuilder();
                            entNum++;
                        } else {
                            subString.append(numOnly.charAt(b));
                        }
                    }

                    if (subString.length() != 0) { // Last entry
                        ((Object[]) res)[entNum] = parsePrimitiveArray(subString.toString().trim(), c.getComponentType());
                    }

                } else {
                    System.err.print("PropValUtils.parsePrimitiveArray: Parsing of string '"
                            + arrayStr + "' to class " + c.getName() + " not yet supported.");
                }
            }
        } catch (NumberFormatException ex) {
            throw ex;
        }
        return res;
    }

}
