package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A utility class contains methods to covert Object to String format used in properties files, and vice-versa.
 *
 * @author Ben Hui
 * @version 20181011
 *
 * <pre>
 * History
 * 20181011: Add support for parsing of Double
 *
 * </pre>
 *
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
            } else if (Double.class.equals(cls)) {
                res = Double.valueOf(ent);
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

    public static Document parseXMLFile(File xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(xml);
    }

    public static void replacePropEntryByDOM(Document src_doc, File tarProp,
            Element[] replacement_element, Element replacement_comment)
            throws IOException, ParserConfigurationException, SAXException,
            TransformerConfigurationException, TransformerException {

        final Date CURRENT_DATE = Calendar.getInstance().getTime();
        final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String DATE_STAMP_STR = DEFAULT_DATE_FORMAT.format(CURRENT_DATE);
        NodeList nList_comment = src_doc.getElementsByTagName("comment");
        for (int c = 0; c < 1; c++) {
            Node commentNode = nList_comment.item(c);
            if (replacement_comment != null) {
                commentNode.setTextContent(replacement_comment.getTextContent());
            }
            String ent = commentNode.getTextContent();
            StringWriter wri = new StringWriter();
            PrintWriter pWri;
            try (final BufferedReader lines = new BufferedReader(new StringReader(ent))) {
                pWri = new PrintWriter(wri);
                String srcLine;
                while ((srcLine = lines.readLine()) != null) {
                    if (srcLine.startsWith("Last modification")) {
                        String dateStr = "Last modification: " + DATE_STAMP_STR;
                        pWri.println(dateStr);
                    } else {
                        pWri.println(srcLine);
                    }
                }
            }
            pWri.close();
            commentNode.setTextContent(wri.toString().replaceAll("\r", ""));
        }
        if (replacement_element.length > 0) {
            boolean[] replaceEntry = new boolean[replacement_element.length];
            NodeList nList_entry = src_doc.getElementsByTagName("entry");
            for (int entId = 0; entId < nList_entry.getLength(); entId++) {
                Element entryElement = (Element) nList_entry.item(entId);
                for (int k = 0; k < replacement_element.length; k++) {
                    String keyAttr = replacement_element[k].getAttribute("key");
                    if (keyAttr != null && keyAttr.equals(entryElement.getAttribute("key"))) {
                        entryElement.setTextContent(replacement_element[k].getTextContent());
                        replaceEntry[k] = true;
                    }
                }
            }
            // Insert entry
            NodeList nList_prop = src_doc.getElementsByTagName("properties");
            Element propElem = (Element) nList_prop.item(0);
            for (int k = 0; k < replacement_element.length; k++) {
                if (!replaceEntry[k]) {
                    Element newEntry = src_doc.createElement(replacement_element[k].getTagName());
                    newEntry.setAttribute("key", replacement_element[k].getAttribute("key"));
                    newEntry.setTextContent(replacement_element[k].getTextContent());
                    // Add a new entry
                    propElem.appendChild(newEntry);
                }
            }
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        transformer = tf.newTransformer();
        //Uncomment if you do not require XML declaration
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, src_doc.getDoctype().getSystemId());
        //Write XML to file
        FileOutputStream outStream = new FileOutputStream(tarProp);
        transformer.transform(new DOMSource(src_doc), new StreamResult(outStream));
    }

    public static void genPropFile(File tarDir, File scrDir, String PROP_FILE_NAME,
            Element[] replacement_element, Element replacement_comment)
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        File propFile = new File(scrDir, PROP_FILE_NAME);
        if (propFile.exists()) {
            tarDir.mkdirs();
            File tarProp = new File(tarDir, PROP_FILE_NAME);
            PropValUtils.replacePropEntryByDOM(PropValUtils.parseXMLFile(propFile), tarProp, replacement_element, replacement_comment);
        }
        File[] dirList = scrDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        //Run recursively until there is no directory
        for (File d : dirList) {
            File newTar = new File(tarDir, d.getName());
            genPropFile(newTar, d, PROP_FILE_NAME, replacement_element, replacement_comment);
        }
    }

    public static void genPropFile(File tarDir, File scrDir, String PROP_FILE_NAME)
            throws IOException, ParserConfigurationException, SAXException, TransformerException {

        Element[] replacement_element;
        Element replacement_comment;
        
        File propFile = new File(scrDir, PROP_FILE_NAME);
        Document xml_doc = PropValUtils.parseXMLFile(propFile);

        NodeList nList_entry = xml_doc.getElementsByTagName("entry");
        replacement_element = new Element[nList_entry.getLength()];

        for (int entId = 0; entId < nList_entry.getLength(); entId++) {
            Element entryElement = (Element) nList_entry.item(entId);
            replacement_element[entId] = entryElement;
        }

        replacement_comment = (Element) xml_doc.getElementsByTagName("comment").item(0);

        PropValUtils.genPropFile(tarDir, scrDir, PROP_FILE_NAME,
                replacement_element, replacement_comment);

    }

}
