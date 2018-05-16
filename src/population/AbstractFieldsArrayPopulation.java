package population;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import availability.AbstractAvailability;
import person.AbstractIndividualInterface;
import random.RandomGenerator;
import relationship.RelationshipMap;
import relationship.SingleRelationship;
import util.PersonClassifier;

/**
 * An extended version of AbstractPopulation, with fields store as arrays
 *
 * @author Ben Hui
 * @version 20140930
 */
public abstract class AbstractFieldsArrayPopulation extends AbstractPopulation {

    public static final int ONE_YEAR_INT = 360;
    public static final int FIELDS_SEED = 0;
    public static final int FIELDS_RNG = FIELDS_SEED + 1;
    public static final int FELIDS_POP = FIELDS_RNG + 1;
    public static final int FIELDS_REL_MAP = FELIDS_POP + 1;
    public static final int FIELDS_AVAIL = FIELDS_REL_MAP + 1;
    public static final int FIELDS_NEXT_ID = FIELDS_AVAIL + 1;
    public static final int LENGTH_FIELDS = FIELDS_NEXT_ID + 1;

    protected Object[] fields = new Object[LENGTH_FIELDS];
    private Map<Integer, AbstractIndividualInterface> localData;

    // <editor-fold defaultstate="collapsed" desc="protected Map<Integer, AbstractIndividualInterface> generateLocalDataMap()">
    protected Map<Integer, AbstractIndividualInterface> generateLocalDataMap() {
        Map<Integer, AbstractIndividualInterface> res; // Id -> Person            

        res = new Map<Integer, AbstractIndividualInterface>() {
            // Id -> Array index, with row = pid  / allPerson.length
            // and col = pid % allPerson.length;
            ArrayList<int[]> indexMap = new ArrayList<>();

            @Override
            public int size() {
                return getPop().length;
            }

            @Override
            public boolean isEmpty() {
                return indexMap.isEmpty();
            }

            @Override
            public boolean containsKey(Object key) { // Person Id
                try {
                    int row = ((Number) key).intValue() / getPop().length;
                    int col = ((Number) key).intValue() % getPop().length;
                    return row < indexMap.size() && indexMap.get(row)[col] >= 0;
                } catch (ClassCastException ex) {
                    return false;
                }
            }

            @Override
            public AbstractIndividualInterface get(Object key) { // Person Id
                try {
                    int row = ((Number) key).intValue() / getPop().length;
                    int col = ((Number) key).intValue() % getPop().length;

                    if (row < indexMap.size()) {
                        int allPersonIndex = indexMap.get(row)[col];
                        if (allPersonIndex < 0) {
                            return null;
                        } else {
                            if (getPop()[allPersonIndex].getId() != ((Number) key).intValue()) {
                                return null;   //Expired person
                            } else {
                                return getPop()[allPersonIndex];
                            }
                        }
                    } else {
                        return null;
                    }
                } catch (ClassCastException ex) {
                    return null;
                }
            }

            @Override
            public AbstractIndividualInterface put(Integer key, AbstractIndividualInterface value) { // Key = Array Index
                if (key > getPop().length) {
                    throw new IllegalArgumentException(getClass().getName() + ".put: Key should be array index < " + getPop().length);
                }
                int row = value.getId() / getPop().length;
                int col = value.getId() % getPop().length;

                while (indexMap.size() <= row) { // Add a new row
                    int[] newRow = new int[getPop().length];
                    Arrays.fill(new int[getPop().length], -1);
                    indexMap.add(newRow);
                }
                indexMap.get(row)[col] = key;
                int nKey = ((Number) key).intValue();
                getPop()[nKey] = value;
                return value;
            }

            @Override
            public boolean containsValue(Object value) {
                try {
                    AbstractIndividualInterface p = (AbstractIndividualInterface) value;
                    int row = p.getId() / getPop().length;
                    int col = p.getId() % getPop().length;
                    return row < indexMap.size() && indexMap.get(row)[col] >= 0;
                } catch (ClassCastException ex) {
                    return false;
                }
            }

            @Override
            public AbstractIndividualInterface remove(Object key) {
                try {
                    int row = ((Number) key).intValue() / getPop().length;
                    int col = ((Number) key).intValue() % getPop().length;
                    if (row < indexMap.size() && indexMap.get(row)[col] >= 0) {
                        int oldIndex = indexMap.get(row)[col];
                        indexMap.get(row)[col] = -1;
                        return getPop()[oldIndex];
                    } else {
                        return null;
                    }
                } catch (ClassCastException ex) {
                    return null;
                }
            }

            @Override
            public void putAll(Map<? extends Integer, ? extends AbstractIndividualInterface> m) {
                for (Integer arrKey : m.keySet()) {
                    put(arrKey, m.get(arrKey)); // Just stick with put function
                }
            }

            @Override
            public void clear() {
                indexMap.clear();
            }

            @Override
            public Set<Integer> keySet() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Collection<AbstractIndividualInterface> values() {
                return Arrays.asList(Arrays.copyOf(getPop(), getPop().length, AbstractIndividualInterface[].class));
            }

            @Override
            public Set<Map.Entry<Integer, AbstractIndividualInterface>> entrySet() {
                java.util.HashMap<Integer, AbstractIndividualInterface> res = new java.util.HashMap<>();
                int r = 0;
                for (int[] row : indexMap) {
                    for (int c = 0; c < row.length; c++) {
                        if (row[c] > 0) {
                            AbstractIndividualInterface ent;
                            if (getPop()[row[c]].getId() == (r * getPop().length) + c) {
                                ent = getPop()[row[c]];
                                res.put(ent.getId(), ent);
                            }
                        }
                    }
                    r++;
                }
                return res.entrySet();

            }
        };

        // Filling initial indexMap
        for (int i = 0; i < getPop().length; i++) {
            res.put(i, getPop()[i]);
        }

        return res;
    }

    protected Map<Integer, AbstractIndividualInterface> getLocalData() {
        if (localData == null) {
            localData = generateLocalDataMap();
        }
        return localData;
    }
// </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="encodePopToStream">   
    public void encodePopToStream(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(getGlobalTime());
        out.writeObject(getInfList());                
        out.writeObject(fields);
    }

    //</editor-fold>
    public AbstractFieldsArrayPopulation() {
        fields[FIELDS_NEXT_ID] = 0;
    }

    public Class getFieldClass(int fieldNum) {
        if (fieldNum < getFields().length) {
            return getFields()[fieldNum] != null ? getFields()[fieldNum].getClass() : null;
        } else {
            throw new UnsupportedOperationException(getClass().getName() + ".getFieldClass: Field #" + fieldNum + " not supported");
        }
    }

    public Object[] getFields() {
        return fields;
    }

    protected Object[] setFields(Object[] fields) {
        Object[] ori = this.fields;
        this.fields = fields;
        return ori;
    }

    // <editor-fold defaultstate="collapsed" desc="getValue and setParameter, by id only">
    @Override
    public Object getValue(String identifier, int id) {
        if (id > 0) {
            return getFields()[id];
        } else {
            throw new UnsupportedOperationException(getClass().getName() + ".getValue: Input for [" + identifier + "," + id + "] not supported");
        }
    }

    @Override
    public boolean setParameter(String identifier, int id, Object value) {
        if (id > 0 && id < getFields().length) {
            getFields()[id] = value;
            return true;
        } else {
            throw new UnsupportedOperationException(getClass().getName() + ".setParameter: Input [" + identifier + "," + id + "] not supported");
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="getPop() and setPop()">

    @Override
    public AbstractIndividualInterface[] getPop() {
        return (AbstractIndividualInterface[]) fields[FELIDS_POP];
    }

    @Override
    protected void setPop(AbstractIndividualInterface[] pop) {
        fields[FELIDS_POP] = pop;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="getRelMap and setRelMap">

    public RelationshipMap[] getRelMap() {
        return (RelationshipMap[]) fields[FIELDS_REL_MAP];
    }

    public void setRelMap(RelationshipMap[] relMap) {
        fields[FIELDS_REL_MAP] = relMap;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="getAvailability and setAvailiablity">
    public AbstractAvailability[] getAvailability() {
        return (AbstractAvailability[]) fields[FIELDS_AVAIL];
    }

    public void setAvailability(AbstractAvailability[] availability) {
        fields[FIELDS_AVAIL] = availability;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="getRNG, getSeed, getSeed and setRNG">
    public RandomGenerator getRNG() {       
        return (RandomGenerator) fields[FIELDS_RNG];
    }

    public void setRNG(RandomGenerator RNG) {
        fields[FIELDS_RNG] = RNG;
    }

    public long getSeed() {
        return ((Long) fields[FIELDS_SEED]);
    }

    public void setSeed(long seed) {
        fields[FIELDS_SEED] = seed;
    }

    // </editor-fold>
    protected abstract SingleRelationship formRelationship(AbstractIndividualInterface[] pair, RelationshipMap relMap, int d, int mapType);

    // <editor-fold defaultstate="collapsed" desc="overwritten version of formRelationship">
    @Override
    protected SingleRelationship formRelationship(AbstractIndividualInterface[] pair, RelationshipMap relMap, int d) {
        for (int i = 0; i < getRelMap().length; i++) {
            if (getRelMap()[i] == relMap) {
                return formRelationship(pair, relMap, d, i);
            }
        }
        return null;
    }
    // </editor-fold>

    /*
     * @return int[]{ number of newlyInfected by class..., number in class...}  
     */
    @Override
    public int[] setInstantInfection(int infId, PersonClassifier infClassifer, float[] prevalByClass, int preExposeMax) {

        float[] preval = Arrays.copyOf(prevalByClass, prevalByClass.length);
        boolean[] negPreval = new boolean[preval.length];
        boolean[] byCount = new boolean[preval.length];

        boolean preRun = false;

        int[] popCount = new int[preval.length];
        int[] popCountSus = new int[preval.length];
        int[] alreadyInfCount = new int[preval.length];
        int[] infCount = new int[preval.length * 2];

        boolean needRun = false;

        // If preval < 0, do a count first and calculate ratio based on popCount 
        for (int i = 0; i < byCount.length; i++) {
            negPreval[i] = preval[i] < 0;
            byCount[i] = preval[i] >= 1 || negPreval[i];
            preRun |= byCount[i];
        }

        AbstractIndividualInterface[] candidates = getPop();

        if (preRun) {
            ArrayList<AbstractIndividualInterface> candidatesArr
                    = new ArrayList<>();
            for (AbstractIndividualInterface person : getPop()) {
                int cI = infClassifer.classifyPerson(person);
                if (cI >= 0) {
                    popCount[cI]++;
                    candidatesArr.add(person);
                    // Already infected
                    if (person.getInfectionStatus()[infId] != AbstractIndividualInterface.INFECT_S) {
                        alreadyInfCount[cI]++;
                    }

                }
                candidates = candidatesArr.toArray(new AbstractIndividualInterface[candidatesArr.size()]);
            }
        }

        for (int i = 0; i < preval.length; i++) {
            if (negPreval[i]) {
                preval[i] = Math.round(-preval[i] * popCount[i]);
                preval[i] = preval[i] - alreadyInfCount[i];
                needRun |= preval[i] > 0;
            } else {
                needRun |= true;
            }
            popCountSus[i] = popCount[i] - alreadyInfCount[i];
        }

        // At this stage - preval[i] is number of newly infected need
        if (needRun) {
            for (AbstractIndividualInterface person : candidates) {
                int cI = infClassifer.classifyPerson(person);

                if (cI >= 0) {
                    AbstractIndividualInterface p = person;
                    infCount[cI + preval.length]++;
                    boolean newlyInfected;
                    int preExpose = 0;
                    double infectAt, stateStart, cumulStageTime;

                    if (byCount[cI]) {
                        if (p.getInfectionStatus(infId) != AbstractIndividualInterface.INFECT_S) {
                            newlyInfected = false;
                        } else {
                            if (popCountSus[cI] > 0 && preval[cI] > 0) {
                                newlyInfected = getInfList()[infId].getRNG().nextInt(popCountSus[cI]) < preval[cI];
                            } else {
                                newlyInfected = false;
                            }
                            popCountSus[cI]--;
                        }
                    } else {
                        newlyInfected = p.getInfectionStatus(infId) == AbstractIndividualInterface.INFECT_S
                                && getInfList()[infId].getRNG().nextFloat() < preval[cI];
                    }

                    if (newlyInfected) {
                        double res = getInfList()[infId].infecting(p);

                        if (res != Double.POSITIVE_INFINITY) {
                            if (preExposeMax > 0) {
                                preExpose = getInfList()[infId].getRNG().nextInt(preExposeMax);
                            }

                            infectAt = p.getAge() - preExpose;
                            p.setLastInfectedAtAge(infId, infectAt);

                            stateStart = -preExpose;

                            // Determine status immediately
                            cumulStageTime = p.getTimeUntilNextStage(infId);
                            p.setTimeUntilNextStage(infId, cumulStageTime + stateStart);

                            while ((p.getTimeUntilNextStage(infId)) < 0
                                    && p.getInfectionStatus(infId) != AbstractIndividualInterface.INFECT_S) {
                                cumulStageTime += Math.round(getInfList()[infId].advancesState(p));
                                p.setTimeUntilNextStage(infId, cumulStageTime + stateStart);
                            }

                        }

                        if (p.getInfectionStatus(infId) != AbstractIndividualInterface.INFECT_S) {
                            // Exclude those who recovered
                            if (byCount[cI]) {
                                preval[cI]--;
                            }
                            infCount[cI]++;
                        }
                    }

                }
            }
        }
        
        return infCount;
    }

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
        if (ent != null && !ent.isEmpty() && !"null".equalsIgnoreCase(ent)) {
            if (String.class.equals(cls)) {
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
                System.err.print("AbstractFieldsArrayPopulation.propToObject: Parsing of '" + ent + "' to class " + cls.getName() + " not yet supported.");
            }
        }
        return res;
    }

    private static Object parsePrimitiveArray(String arrayStr, Class c) {
        Object res = null;

        final java.util.regex.Pattern PRIMITIVE_ARRAY_PATTERN = java.util.regex.Pattern.compile("\\[.*?\\]");
        final java.util.regex.Pattern MULTI_ARRAY_PATTERN = java.util.regex.Pattern.compile("\\[\\[+.*?\\]+\\]");

        try {
            if (arrayStr != null && !arrayStr.isEmpty() && !"null".equalsIgnoreCase(arrayStr)) {
                String numOnly = arrayStr.substring(1, arrayStr.length() - 1); // Exclude the ending brackets
                String[] splitNum = numOnly.split(",");
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
                    java.util.regex.Matcher matcher;
                    int numGenArr = 0;

                    matcher = MULTI_ARRAY_PATTERN.matcher(numOnly);
                    while (matcher.find()) {
                        numGenArr++;
                    }
                    matcher.reset();
                    if (numGenArr == 0) { // Primitive level
                        matcher = PRIMITIVE_ARRAY_PATTERN.matcher(numOnly);
                        while (matcher.find()) {
                            numGenArr++;
                        }
                        matcher.reset();
                    }
                    res = java.lang.reflect.Array.newInstance(c.getComponentType(), numGenArr);
                    for (int i = 0; i < numGenArr; i++) {
                        matcher.find();
                        ((Object[]) res)[i] = parsePrimitiveArray(matcher.group(), c.getComponentType());
                    }
                } else {
                    System.err.print("AbstractFieldsArrayPopulation.parsePrimitiveArray: Parsing of string '"
                            + arrayStr + "' to class " + c.getName() + " not yet supported.");
                }
            }
        } catch (NumberFormatException ex) {
            throw ex;
        }
        return res;
    }
}
