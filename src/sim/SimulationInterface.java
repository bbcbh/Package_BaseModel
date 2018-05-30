package sim;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import util.PersonClassifier;

/**
 * An interface describing some common properties and method for simulation.
 * @version 20180528
 * @author Ben Hui
 *
 *History: 
 *<pre>
 *20180528:
 *  - Change definition of PROP_POP_EXPORT_AT to PROP_POP_EXPORT_AT. 
 *  - Remove the calling of PROP_POP_IMPORT. Use PROP_POP_IMPORT_PATH instead.
 </pre>
 * 
 */
public interface SimulationInterface {

    public static final String PROGRESS_MSG = "PROGRESS_MSG";
    public static final String PROGESS_CURRENT_STEP = "PROGESS_CURRENT_STEP";
    public static final String PROGRESS_SIM_STORED = "PROGRESS_SIM_STORED";
    public static final String PROGRESS_ALL_DONE = "PROGRESS_ALL_DONE";
    public static final String FILENAME_PROP = "simSpecificSim.prop";
    
    // Properties - shared for all sim
    public static final String[] PROP_NAME = {
        "PROP_POP_TYPE", "PROP_NUM_SIM_PER_SET", "PROP_USE_PARALLEL",
        "PROP_BASESEED", "PROP_NUM_SNAP", "PROP_SNAP_FREQ",
        "PROP_BURNIN",           
        "PROP_POP_EXPORT_AT", "PROP_POP_IMPORT_PATH", 
        "PROP_INFECTION_INTRO"
    };
    public static final Class[] PROP_CLASS = {
        String.class, Integer.class, Integer.class,
        Long.class, Integer.class, Integer.class,
        Integer.class,              
        int[].class, String.class, 
        float[][].class // PROP_INFECTION_INTRO Format: float[infId][global_time, prevalence or number of infected,...]
    };
    
    public static final int PROP_POP_TYPE = 0;
    public static final int PROP_NUM_SIM_PER_SET = PROP_POP_TYPE + 1;
    public static final int PROP_USE_PARALLEL = PROP_NUM_SIM_PER_SET + 1;
    public static final int PROP_BASESEED = PROP_USE_PARALLEL + 1;
    public static final int PROP_NUM_SNAP = PROP_BASESEED + 1;
    public static final int PROP_SNAP_FREQ = PROP_NUM_SNAP + 1;
    public static final int PROP_BURNIN = PROP_SNAP_FREQ + 1;     
    public static final int PROP_POP_EXPORT_AT = PROP_BURNIN + 1;
    public static final int PROP_POP_IMPORT_PATH = PROP_POP_EXPORT_AT+1;
    public static final int PROP_INFECTION_INTRO = PROP_POP_IMPORT_PATH + 1; 
    
    // Export / import options
    public static final String POP_FILE_PREFIX = "pop_";    
    public static final String EXCL_SEED_FILE_PREFIX = "exclBool_";
    

    void loadProperties(Properties prop);

    void setStopNextTurn(boolean stopNextTurn);

    void setBaseDir(File baseDir);

    Properties generateProperties();

    void setSnapshotSetting(PersonClassifier[] snapshotCountClassifier, boolean[] snapshotCountAccum);

    void generateOneResultSet() throws IOException, InterruptedException;
    
  
    
}
