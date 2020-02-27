package infection.vaccination;

import java.util.Arrays;
import java.util.HashMap;

import person.AbstractIndividualInterface;

/**
 * An abstract class serve as a base for the effect of vaccines in population.
 *
 * @author Ben Hui
 * @version 20190523
 */
public abstract class AbstractVaccination {

    // Key: person id, value: age of vaccination
    protected HashMap<Integer, int[]> vaccinationRecord = new HashMap<>();

    protected double[] parameters;

    protected int[] validTime;

    public AbstractVaccination(double[] parameters) {
        this.parameters = parameters;
        this.validTime = null;

    }

    public AbstractVaccination(double[] parameters, int[] validTime) {
        this.validTime = validTime;
        this.parameters = parameters;
    }

    public int[] getValidTime() {
        return validTime;
    }

    public boolean vaccinationApplicableAt(int time) {
        return (validTime == null)
                || (validTime[0] >= validTime[1])
                || (time >= validTime[0] && time < validTime[1]);
    }

    /**
     * Add vaccinated person to the vaccination record.
     *
     * @param person Person to be vaccinated
     * @return vaccination history for the added person.
     */
    public int[] vaccinatePerson(AbstractIndividualInterface person) {
        int[] rec = getVaccinationRecord(person.getId());
        if (rec == null) {
            rec = new int[1];
        } else {
            rec = Arrays.copyOf(rec, rec.length + 1);
        }

        rec[rec.length - 1] = (int) person.getAge();
        return vaccinationRecord.put(person.getId(), rec);
    }

    public HashMap<Integer, int[]> getVaccinationRecord() {
        return vaccinationRecord;
    }

    public int[] getVaccinationRecord(int personId) {
        return vaccinationRecord.get(personId);
    }

    /**
     * Describe the impact of vaccine on a selected person. Noted it is up to be implementation of individual vaccine to define the parameter input as well as outputs
     *
     * @param person Person to be examine.
     * @param params Additional parameters
     * @return a set of double array to describe the impact of vaccine
     */
    public abstract double[] vaccineImpact(AbstractIndividualInterface person, Object params);

    public double[] getParameters() {
        return parameters;
    }

    public void setParameters(double[] parameters) {
        this.parameters = parameters;
    }

}
