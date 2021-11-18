package person;

import infection.AbstractInfection;

/**
 * An interface define function required for every individual in the model
 *
 * @author Ben Hui
 */
public interface AbstractIndividualInterface {
    /**
     * Infection status for those who are susceptible to infections
     */
    public static final int INFECT_S = -1;
    /**
     * ONE_YEAR has 365.25 days
     */
    public static final double ONE_YEAR = 365.25;
    
    public static final int ONE_YEAR_INT = 365;

    /**
     * Get age of this person in days
     *
     * @return the value of age in days
     */
    double getAge();

    /**
     * Get this person's id
     *
     * @return the value of id
     */
    int getId();

    /**
     * Get the value of infectionStatus
     *
     * @return the value of infectionStatus
     */
    int[] getInfectionStatus();

    /**
     * Get the value of infectionStatus at specified infection index.
     *
     * @param index infection index
     * @return the value of infectionStatus at specified index
     */
    int getInfectionStatus(int index);

    /**
     * Get age of last infection, with input infection index
     *
     * @param infectionIndex infection index
     * @return the age of last infection ,or -1 if never infected
     */
    double getLastInfectedAtAge(int infectionIndex);

    public Comparable<?> getParameter(String id);

    /**
     * Get the value of timeUntilNextStage at specified infection index.
     *
     * @param index infection index
     * @return time until next stage
     */
    double getTimeUntilNextStage(int index);

    int incrementTime(int deltaT, AbstractInfection[] infectionList);

    /**
     * Return gender of this person
     *
     * @return true if the person is male
     */
    boolean isMale();

    /**
     * Set age of selected AbstractPerson. It should only be used for debugging/sample generation
     *
     * @param age New age of selected MovablePerson
     */
    void setAge(double age);

    /**
     * Set the value of infectionStatus at specified infection index.
     *
     * @param index infection index
     * @param newInfectionStatus new value of infectionStatus at specified index
     */
    void setInfectionStatus(int index, int newInfectionStatus);

    /**
     * Set to true if infections was transmitted to this person from the last act, false otherwise.
     *
     * @param infectionIndex Infection index
     * @param lastActInf If last act is infectious
     */
    void setLastActInfectious(int infectionIndex, boolean lastActInf);

    public Comparable<?> setParameter(String id, Comparable<?> value);

    /**
     * Set the value of timeUntilNextStage at specified infection index. Should only be used at initialisation stage
     *
     * @param index infection index
     * @param newTimeUntilNextStage
     */
    void setTimeUntilNextStage(int index, double newTimeUntilNextStage);

    /**
     * Get time of which the individual enter population
     *
     * @return the time step where the individual enter the model
     */
    int getEnterPopulationAt();

    /**
     * Get starting age
     *
     * @return start age
     */
    double getStartingAge();

    /**
     * Set the time step of which the individual enter the population
     *
     * @param enterPopulationAt
     */
    void setEnterPopulationAt(int enterPopulationAt);

    /**
     * Set the age of last infection.
     * Usually, this should be called during AbstractInfection.infecting method in AbstractInfection
     *
     * @param infectionIndex infection index
     * @param age age of infection
     * @see infection.AbstractInfection#infecting(population.person.RegularCasualRelationshipTypePerson)
     */
    void setLastInfectedAtAge(int infectionIndex, double age);
}
