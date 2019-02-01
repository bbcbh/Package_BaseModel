package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * A utility class to handle zipping and unzipping of file
 *
 * @author Ben Hui
 * @version 20190201 - Change to use Java 8+ notation for file zipping
 */
public class FileZipper {

    public static File unzipFile(File zipFile, File baseDir)
            throws FileNotFoundException, IOException {
        FileOutputStream fos;
        BufferedOutputStream dest;
        int count;
        File objFile = null;
        final int BUFFER = 2048;
        byte[] buf = new byte[BUFFER];
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)))) {
            while (zis.getNextEntry() != null) {
                objFile = File.createTempFile("pop", null, baseDir);
                fos = new FileOutputStream(objFile);
                dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = zis.read(buf, 0, BUFFER)) != -1) {
                    dest.write(buf, 0, count);
                }
                dest.flush();
                dest.close();
                fos.close();
            }
        }
        return objFile;
    }

    public static void zipFile(File srcFile, File tarFile)
            throws FileNotFoundException, IOException {

        if (!tarFile.getName().endsWith(".zip")) {
            tarFile.renameTo(new File(tarFile.getAbsolutePath() + ".zip"));
        }

        if (srcFile.isDirectory()) {
            Path p = Files.createFile(tarFile.toPath());

            try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
                Path pp = srcFile.toPath();
                Files.walk(pp)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                            try {
                                zs.putNextEntry(zipEntry);
                                Files.copy(path, zs);
                                zs.closeEntry();
                            } catch (IOException e) {
                                System.err.println(e);
                            }
                        });
            }
        } else {
            
            // Original method

            final int BUFFER = 2048;
            byte[] data = new byte[BUFFER];
            ZipOutputStream zout;
            ZipEntry ent;
            BufferedInputStream srcBufStr;

            int count;

            if (!tarFile.getName().endsWith(".zip")) {
                tarFile.renameTo(new File(tarFile.getAbsolutePath() + ".zip"));
            }

            zout = new ZipOutputStream(new FileOutputStream(tarFile));
            srcBufStr = new BufferedInputStream(new FileInputStream(srcFile), BUFFER);

            ent = new ZipEntry(srcFile.getName());
            zout.putNextEntry(ent);

            while ((count = srcBufStr.read(data, 0, BUFFER)) != -1) {
                zout.write(data, 0, count);
            }

            srcBufStr.close();
            zout.close();

        }
    }
}
