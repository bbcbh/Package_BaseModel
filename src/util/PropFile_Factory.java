package util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * A factory class to create .prop files based on an existing template.
 *
 * To create new .prop files based on template, a propMod.xml file is needed in
 * the output directory, with the following format:
 *
 * <pre>
 * <replacement_set>
 *    <template_dir_regex>Regex to directory to included as template</template_dir_regex>
 *    <gen_dir_regex grp_num=
'groupNumber from template_dir_regex'>New directory name</gen_dir_regex>
 *    <replace_entry key=
"attrbute to the entry to be replace with">Replacement entry</replace_entry> *
 * </replacement_set>
 *
 *
 * </pre>
 *
 *
 *
 * @author Ben Hui
 * @version 20210308
 */
public class PropFile_Factory {

	public static final String PROP_FILE_NAME = "simSpecificSim.prop";
	public static final String MODIFICATION_FILE_NAME = "propMod.xml";
	public static final File DEFAULT_FILE_BASE_PROP_DIR = new File(
			"C:\\Users\\Bhui\\Documents\\Java_Test\\Prop_Template");
	public static final File DEFAULT_FILE_TARGET_DIR = new File("C:\\Users\\Bhui\\Documents\\Java_Test\\Prop_Gen");

	private static final String NODE_REPLACEMENT_SET = "replacement_set";
	private static final String NODE_TEMPLATE_DIR = "template_dir_regex";
	private static final String NODE_GEN_DIR = "gen_dir_regex";
	private static final String NODE_REPLACE_ENT = "replace_entry";
	private static final String NODE_REPLACE_PATTERN = "replace_pattern";
	private static final String NODE_COPY_EXTRA_FILES = "copy_extra_files";
	private static final String NODE_SUFFIX_ARR = "suffix_arr";
	private static final String ATTR_REPLACE_ENT_KEY = "key";
	private static final String ATTR_REPLACE_PATTERN_KEY = ATTR_REPLACE_ENT_KEY;
	private static final String ATTR_GEN_DIR_GRP_NUM = "grp_num";
	private static final String ATTR_GEN_DIR_USE_SUFFIX_ARR = "use_suffix_arr";
	private static final String ATTR_REPLACE_ENT_USE_SUFFIX_ARR = ATTR_GEN_DIR_USE_SUFFIX_ARR;
	private static final String ATTR_SUFFIX_ARR_ARR_ID = "arr_id";

	public static void main(String[] arg) throws FileNotFoundException, IOException, ParserConfigurationException,
			SAXException, TransformerException {

		File base_prop_dir = DEFAULT_FILE_BASE_PROP_DIR;
		File target_prop_dir = DEFAULT_FILE_TARGET_DIR;

		if (!base_prop_dir.isDirectory()) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setDialogTitle("Choose directory that contains directory of prop files");
			int resp = fc.showOpenDialog(null);

			if (resp == JFileChooser.APPROVE_OPTION) {
				base_prop_dir = fc.getSelectedFile();
			}
		}

		if (!target_prop_dir.isDirectory()) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setDialogTitle("Choose directory that directory of prop files save to");
			int resp = fc.showOpenDialog(null);

			if (resp == JFileChooser.APPROVE_OPTION) {
				target_prop_dir = fc.getSelectedFile();
			}
		}

		if (base_prop_dir.isDirectory() && target_prop_dir.isDirectory()) {
			File modFile = new File(target_prop_dir, MODIFICATION_FILE_NAME);

			if (modFile.isFile()) {
				Document xml_mod_src = PropValUtils.parseXMLFile(modFile);
				NodeList nList_replacement_set = xml_mod_src.getElementsByTagName(NODE_REPLACEMENT_SET);

				for (int rId = 0; rId < nList_replacement_set.getLength(); rId++) {
					Element elem_set = (Element) nList_replacement_set.item(rId);
					NodeList node_set = elem_set.getChildNodes();

					String template_dir_pattern_str = null;
					HashMap<Integer, String> dir_replacement_text = new HashMap<Integer, String>();
					HashMap<String, String> entry_replacement_text = new HashMap<String, String>();
					HashMap<String, String> entry_replacement_pattern = new HashMap<String, String>();
					HashMap<String, String> entry_replacement_suffix_array_key = new HashMap<String, String>();
					boolean copyExtra = false;
					HashMap<String, String[]> suffix_array_map = new HashMap<String, String[]>();

					String gen_dir_suffix_array_id = "";

					for (int sId = 0; sId < node_set.getLength(); sId++) {
						if (node_set.item(sId) instanceof Element) {
							Element child = (Element) node_set.item(sId);
							if (child.getNodeName().equals(NODE_TEMPLATE_DIR)) {
								template_dir_pattern_str = child.getTextContent();
							}
							if (child.getNodeName().equals(NODE_GEN_DIR)) {
								int grpNum = Integer.parseInt(child.getAttribute(ATTR_GEN_DIR_GRP_NUM));
								dir_replacement_text.put(grpNum, child.getTextContent());
								gen_dir_suffix_array_id = child.getAttribute(ATTR_GEN_DIR_USE_SUFFIX_ARR);

							}
							if (child.getNodeName().equals(NODE_REPLACE_ENT)) {
								String key = child.getAttribute(ATTR_REPLACE_ENT_KEY);
								entry_replacement_text.put(key, child.getTextContent());

								if (!child.getAttribute(ATTR_REPLACE_ENT_USE_SUFFIX_ARR).isEmpty()) {
									entry_replacement_suffix_array_key.put(key,
											child.getAttribute(ATTR_REPLACE_ENT_USE_SUFFIX_ARR));
								}

							}
							if (child.getNodeName().equals(NODE_REPLACE_PATTERN)) {
								String key = child.getAttribute(ATTR_REPLACE_PATTERN_KEY);
								entry_replacement_pattern.put(key, child.getTextContent());
							}
							if (child.getNodeName().equals(NODE_COPY_EXTRA_FILES)) {
								copyExtra = true;
							}
							if (child.getNodeName().equals(NODE_SUFFIX_ARR)) {
								String key = child.getAttribute(ATTR_SUFFIX_ARR_ARR_ID);
								suffix_array_map.put(key, child.getTextContent().split(","));
							}

						}

					}

					if (template_dir_pattern_str != null && dir_replacement_text.size() > 0) {
						Pattern template_dir_pattern = Pattern.compile(template_dir_pattern_str);

						File[] template_dirs = base_prop_dir.listFiles(new FileFilter() {
							@Override
							public boolean accept(File pathname) {
								return pathname.isDirectory() && (new File(pathname, PROP_FILE_NAME)).exists()
										&& template_dir_pattern.matcher(pathname.getName()).find();
							}
						});

						for (File template_dir : template_dirs) {

							String[] gen_dir_suffix_array = new String[] { "" };

							if (!gen_dir_suffix_array_id.isEmpty()) {
								gen_dir_suffix_array = suffix_array_map.getOrDefault(gen_dir_suffix_array_id,
										gen_dir_suffix_array);
							}

							for (int s = 0; s < gen_dir_suffix_array.length; s++) {
								String gen_dir_suffix = gen_dir_suffix_array[s];

								// Generating new directory name
								System.out.println("Template dir name = " + template_dir.getName());
								Matcher m = template_dir_pattern.matcher(template_dir.getName());
								StringBuilder newDirName = new StringBuilder(template_dir.getName());
								if (m.find()) {
									for (Integer k : dir_replacement_text.keySet()) {
										String dir_str_replace = dir_replacement_text.get(k);
										newDirName.replace(m.start(k), m.end(k), dir_str_replace);
									}
								}

								if (!gen_dir_suffix.isEmpty()) {
									newDirName = newDirName.append(gen_dir_suffix);
								}

								System.out.println("New dir name = " + newDirName.toString());

								// Make new directory
								File newDir = new File(target_prop_dir, newDirName.toString());
								newDir.mkdirs();

								// Modify elements
								Document xml_src = PropValUtils.parseXMLFile(new File(template_dir, PROP_FILE_NAME));
								NodeList nList_entry = xml_src.getElementsByTagName("entry");

								// Replace element
								ArrayList<Element> replaceElement = new ArrayList<>();

								for (String aK : entry_replacement_text.keySet()) {
									boolean addNewElem = true;
									String replaceText = entry_replacement_text.get(aK);
									Pattern replacePattern = null;
									if (entry_replacement_pattern.containsKey(aK)) {
										replacePattern = Pattern.compile(entry_replacement_pattern.get(aK));
									}

									for (int entId = 0; entId < nList_entry.getLength() && addNewElem; entId++) {
										Element entryElement = (Element) nList_entry.item(entId);
										if (aK.equals(entryElement.getAttribute(ATTR_REPLACE_ENT_KEY))) {
											if (replacePattern != null) {
												String orig_ent = entryElement.getTextContent();
												StringBuilder new_ent = new StringBuilder(orig_ent);
												Matcher matcherEnt = replacePattern.matcher(orig_ent);

												if (matcherEnt.find()) {

													new_ent.replace(matcherEnt.start(1), matcherEnt.end(1),
															replaceText);
												}

												replaceText = new_ent.toString();
											}

											if (entry_replacement_suffix_array_key.get(aK) != null) {
												String[] suffixList = suffix_array_map
														.get(entry_replacement_suffix_array_key.get(aK));
												if (suffixList == null) {
													System.err.printf(
															"Warning: Suffix array of id '%s' not found as described in <%s ... %s = '%s'>...</%s>.\n",
															entry_replacement_suffix_array_key.get(aK),
															NODE_REPLACE_ENT, ATTR_REPLACE_ENT_KEY, aK,
															NODE_REPLACE_ENT);
												} else {
													replaceText = replaceText + suffixList[s];
												}

											}

											entryElement.setTextContent(replaceText);

											replaceElement.add(entryElement);
											addNewElem = false;
										}
									}
									if (addNewElem) {
										Element newEntry = xml_src.createElement("entry");
										newEntry.setAttribute(ATTR_REPLACE_ENT_KEY, aK);

										newEntry.setTextContent(replaceText);
										replaceElement.add(newEntry);
									}
								}

								// Store as new .prop file
								PropValUtils.replacePropEntryByDOM(xml_src, new File(newDir, PROP_FILE_NAME),
										replaceElement.toArray(new Element[replaceElement.size()]), null);

								// Check if copy extra
								if (copyExtra) {
									File[] template_dir_extra = template_dir.listFiles(new FileFilter() {
										@Override
										public boolean accept(File pathname) {
											return !pathname.getName().equals(PROP_FILE_NAME);
										}
									});

									for (File extra : template_dir_extra) {
										File target = new File(newDir, extra.getName());
										Files.copy(extra.toPath(), target.toPath(),
												StandardCopyOption.REPLACE_EXISTING);
									}

								}

							}
						}

					}

				}

			} else {
				System.err.println("Mod file " + modFile.getAbsolutePath() + " not found!");
			}

		} else {
			System.err.println("Error: base prop directory and/or target prop direct not defined. Exiting.");
			System.exit(1);
		}

	}

}
