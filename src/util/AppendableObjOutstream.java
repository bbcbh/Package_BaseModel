package util;

import java.io.*;

// From http://stackoverflow.com/questions/1194656/appending-to-an-objectoutputstream/1195078#1195078
public class AppendableObjOutstream extends ObjectOutputStream {

    public AppendableObjOutstream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        // do not write a header
    }

    public static ObjectOutputStream generateFromFile(File targetFile) throws IOException {
        if (!targetFile.exists()) {
            return new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(targetFile)));
        } else {
            return new AppendableObjOutstream(new BufferedOutputStream(new FileOutputStream(targetFile, true)));
        }
    }
           
    
}
