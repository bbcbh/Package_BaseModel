package infection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import person.AbstractIndividualInterface;
import random.RandomGenerator;

/**
 * <p>
 * An abstract class for all infectious diseases modelled using <a href="http://commons.apache.org/proper/commons-math/">Apache Commons Math</a>
 * library.</p>
 *
 * <p>
 * All infection modelled will be inherited from this class. They will each have an infection index, an array of String representing various stages of
 * infection, and a random number generator.</p>
 *
 *
 * @author Ben Hui
 */
public abstract class AbstractInfection implements java.io.Serializable {

    /**
     * The index of infection modelled.
     */
    private int infectionIndex;
    /**
     * String representation of stages involved for this disease.
     */
    private String[] infectionState;
    /**
     * Random number generator
     */
    private RandomGenerator RNG;
    /**
     * Distribution used in this infection object
     */
    private AbstractRealDistribution[] distributions;
    /**
     * State of distribution (e.g. [mean, sd] for Beta and Gamma, [min, max] for Uniform)
     */
    private double[][] distStates;
    /**
     * Pattern regex for setting n-th distribution
     */
    public static final String PARAM_DIST_PARAM_INDEX_REGEX = "Dist_Param_Index_999";
    protected final Pattern PATTERN_DIST_PARAM_INDEX = Pattern.compile(PARAM_DIST_PARAM_INDEX_REGEX.replaceAll("999", "(\\\\d+)"));
    public static final String PARAM_DIST_NEXT_VALUE_REGEX = "Dist_Next_Value_999";
    protected final Pattern PATTERN_DIST_NEXT_VALIE = Pattern.compile(PARAM_DIST_NEXT_VALUE_REGEX.replaceAll("999", "(\\\\d+)"));

    public static final int STATUS_EXP = 0;
    public static final int STATUS_ASY = STATUS_EXP + 1;
    public static final int STATUS_SYM = STATUS_ASY + 1;
    public static final int STATUS_IMM = STATUS_SYM + 1;

    /**
     * Constructor for AbstractInfection. All class that extends AbstractInfection need an constructor that overwrite this method.
     *
     * @param RNG random number generator
     */
    public AbstractInfection(RandomGenerator RNG) {
        this.RNG = RNG;
    }

    /**
     * Set distributions used in this object. Note that refresh is not called
     *
     * @param distributions
     * @param distStates
     */
    protected void storeDistributions(AbstractRealDistribution[] distributions, double[][] distStates) {
        this.distributions = distributions;
        this.distStates = distStates;
    }

    /**
     * Return Class of distribution
     *
     * @param index the index of distributions
     * @return the Class of distribution
     */
    public Class getDistributionClass(int index) {
        return distributions[index].getClass();
    }

    /**
     * Return distribution used in this object
     *
     * @param index the index of distributions
     * @return the distribution object used
     */
    protected AbstractRealDistribution getDistribution(int index) {
        return distributions[index];
    }

    /**
     * Get distribution state
     *
     * @param index distribution index
     * @return the double array storing distribution state
     */
    protected double[] getDistributionState(int index) {
        return distStates[index];
    }

    /**
     * Set distribution used in this object, as defined by index
     *
     * @param index the index of distribution
     * @param distribution
     */
    protected void setDistribution(int index, AbstractRealDistribution distribution) {
        this.distributions[index] = distribution;
        
    }

    /**
     * Set distribution state for distribution
     *
     * @param index the index of distribution
     * @param distState an array corresponds state distribution (e.g. [mean,SD],[min,max])
     */
    protected void setDistributionState(int index, double[] distState) {
        this.distStates[index] = distState;
        refreshDistribution(index);
    }

    /**
     * Refresh distribution based on state
     *
     * @param distId distribution index
     */
    private void refreshDistribution(int distId) {
        double[] var;
        AbstractRealDistribution distribution = getDistribution(distId);
        if (distStates[distId][1] != 0) {
            var = generateDistParam(distStates[distId], distribution);
            if (distribution instanceof GammaDistribution) {
                // var =  alpha/shape and beta/lambda/rate value                
                setDistribution(distId, new GammaDistribution(this.getRNG(), var[0], 1/var[1]));
            } else if (distribution instanceof BetaDistribution) {
                // var = alpha and beta value
                setDistribution(distId, new BetaDistribution(this.getRNG(), var[0], var[1]));
            } else if (distribution instanceof UniformRealDistribution) {
                // var = (min,max)                
                setDistribution(distId, new UniformRealDistribution(this.getRNG(), var[0], var[1]));
            } else {
                System.err.println(getClass().getName() + "refreshDistribution: Refreshing distribution " + distId + " not supported yet!");
            }
        }
    }

    /**
     * Return number of distribution used
     *
     * @return number of distribution used, or -1 if not defined
     */
    public int getDistSize() {
        return this.distributions == null ? -1 : this.distributions.length;
    }

    protected double getRandomDistValue(int id) {
        if (distributions[id] != null && distStates[id][1] != 0) {
            return distributions[id].sample();
        } else {
            return distStates[id][0]; // return mean value if sd =0 or distribution not defined
        }
    }

    /**
     * Return infection index of this disease.
     *
     * @return infection index
     */
    public int getInfectionIndex() {
        return infectionIndex;
    }

    /**
     * Set infection index of this disease. This method should only be called once, preferrably within the constructor of inherited object.
     *
     * @param index new infection index of this disease.
     */
    public void setInfectionIndex(int index) {
        infectionIndex = index;
    }

    /**
     * Set the infection state of this disease. This method should only be called once within the constructor of inherited object.
     *
     * @param infectionState String representation of infection state.
     */
    protected void setInfectionState(String[] infectionState) {
        this.infectionState = infectionState;
    }

    /**
     * Return number of state for this disease.
     *
     * @return number of state for this disease.
     */
    public int getNumState() {
        return this.infectionState.length;
    }

    /**
     * Return the String representation of infection state
     *
     * @param infectState Infection state index.
     * @return name of infection state as text.
     */
    public String getStateName(int infectState) {
        return infectState == AbstractIndividualInterface.INFECT_S ? "Susceptible" : this.infectionState[infectState];
    }

    /**
     * Set the random number generator for this disease.
     *
     * @param RNG random number generator.
     */
    public void setRNG(RandomGenerator RNG) {    
        this.RNG = RNG;

        for (int distId = 0; distId < distributions.length; distId++) {
            if (distributions[distId] != null) { // Only necessary if distribution is not null to begin with
                 
                // Generate new distribution
                boolean newDist = false;
                java.lang.reflect.Constructor[] constructors = distributions[distId].getClass().getConstructors();
                double[] state = generateDistParam(distStates[distId], distributions[distId]);
                
                newDist:
                for (Constructor constructor : constructors) {
                    Class[] paramClass = constructor.getParameterTypes();                                                            
                    if (paramClass.length == state.length + 1 && 
                            paramClass[0].isInstance(getRNG())){                                                               
                        // +1 for RNG
                        Object[] param = new Object[paramClass.length];
                        int statePt = 0;
                        for (int p = 0; p < paramClass.length; p++) {
                            if (paramClass[p].isInstance(getRNG())) {
                                param[p] = getRNG();
                            } else {
                                if (paramClass[p].isPrimitive()) {
                                    if (paramClass[p].equals(Integer.TYPE)) {
                                        param[p] = (int) state[statePt];
                                    } else if (paramClass[p].equals(Double.TYPE)) {                                       
                                        param[p] = state[statePt];                                        
                                    } else if (paramClass[p].equals(Float.TYPE)) {
                                        param[p] = (float) state[statePt];
                                    } else if (paramClass[p].equals(Long.TYPE)) {
                                        param[p] = (long) state[statePt];
                                    } else if (paramClass[p].equals(Short.TYPE)) {
                                        param[p] = (short) state[statePt];
                                    } else {
                                        //  Use double for now
                                        param[p] = state[statePt];                                    }
                                } else {
                                    //  Use double for now
                                    param[p] = state[statePt];
                                }
                                statePt++;
                            }
                        }
                        try {
                            distributions[distId] = (AbstractRealDistribution) constructor.newInstance(param);
                            newDist = true;
                            break;
                        }catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            ex.printStackTrace();  // Print error message, and try next one
                        }
                    }
                }
                if (!newDist) {
                    System.err.println(getClass().getName() + ".setRNG: Distribuiton #" + distId + " not renewed!");
                }

            }
        }
    }

    /**
     * Get the random generator for this disease.
     *
     * @return the random number generator for this disease.
     */
    public RandomGenerator getRNG() {
        return this.RNG;
    }

    /**
     * Get the current infection state of the an AbstractPerson.
     *
     * @param p input AbstractIndividualInterface
     * @return infection state of MovablePerson p
     */
    protected int getCurrentInfectionState(AbstractIndividualInterface p) {
        return p.getInfectionStatus(infectionIndex);
    }

    /**
     * Set infection state, and state duration of the an AbstractPerson.
     *
     * @param p input AbstractPerson
     * @param status infection state to be set
     * @param duration duration of infection state
     */
    protected void setInfection(AbstractIndividualInterface p, int status, double duration) {
        p.setInfectionStatus(infectionIndex, status);
        p.setTimeUntilNextStage(infectionIndex, duration);
    }

    /**
     * Set last infection at for target AbstractPerson
     *
     * @param tar target AbstractPerson
     * @param age age where infection occurs. Usually tar.getAge()
     */
    protected void setInfectedAt(AbstractIndividualInterface tar, double age) {
        tar.setLastInfectedAtAge(this.getInfectionIndex(), age);
    }

    /**
     * A convenience method of generating parameter required for Gamma distribution. For Gamma distribution,
     * <code> shape = mean*mean / variance, rate = 1 / (variance / mean);</code> *
     *
     * @param input mean and standard derivation of the distribution.
     * @return shape and rate value
     */
    protected double[] generatedGammaParam(double[] input) {
        // For Gamma distribution
        // shape = alpha = mean*mean / variance
        // rate = beta/lambda = 1 / (variance / mean);

        double[] res = new double[2];
        double var = input[1] * input[1];
        // beta/lambda
        res[1] = input[0] / var;
        //alpha
        res[0] = input[0] * res[1];
        return res;
    }

    /**
     * A convenience method of generating parameter required for Beta distribution. For Beta distribution,
     * <code>alpha = mean*(mean*(1-mean)/variance - 1), beta = (1-mean)*(mean*(1-mean)/variance - 1) </code>
     *
     * @param input mean and standard derivation of the distribution.
     * @return alpha and beta value
     */
    protected double[] generatedBetaParam(double[] input) {
        // For Beta distribution, 
        // alpha = mean*(mean*(1-mean)/variance - 1)
        // beta = (1-mean)*(mean*(1-mean)/variance - 1)
        double[] res = new double[2];
        double var = input[1] * input[1];
        double rP = input[0] * (1 - input[0]) / var - 1;
        //alpha
        res[0] = rP * input[0];
        //beta
        res[1] = rP * (1 - input[0]);
        return res;

    }

    protected double[] generateDistParam(double[] input, AbstractRealDistribution dist) {
        if (dist instanceof BetaDistribution) {
            return generatedBetaParam(input);
        } else if (dist instanceof GammaDistribution) {
            return generatedGammaParam(input);
        } else if (dist instanceof UniformRealDistribution) {
            return input;
        } else {
            System.err.println(getClass().getName() + ".generateDistParam: AbstractDistribution of type " + dist.getClass() + " not support yet. Using default input of " + java.util.Arrays.toString(input));
            return input;
        }
    }

    /**
     * Advance infection status of a SingleRelationshipTypePerson to next stage.
     *
     * This method will be will be called within SingleRelationshipTypePerson.incrementTime(double deltaT, AbstractInfection[] infectionList) method, and if an
     * infection is advancing from non-susceptible stage.
     *
     * @param p SingleRelationshipTypePerson involved
     * @return duration of advance state
     * @see population.AbstractPopulation#incrementTime(double deltaT, AbstractInfection[] infectionList)
     */
    public abstract double advancesState(AbstractIndividualInterface p);

    /**
     * Attempt to infecting a SingleRelationshipTypePerson with disease.
     *
     * This method will be called within SingleRelationshipTypePerson.incrementTime(double deltaT, AbstractInfection[] infectionList) method, and if infection
     * could be passed to target SingleRelationshipTypePerson from last act.
     *
     * @see population.AbstractPerson#incrementTime(double deltaT, AbstractInfection[] infectionList)
     * @param target target of infection
     * @return duration of infection, or Double.POSITIVE_INFINITY if not infected
     *
     */
    public abstract double infecting(AbstractIndividualInterface target);

    /**
     * Check if the person is infectious to the disease modeled.
     *
     * @param p SingleRelationshipTypePerson to be checked
     * @return true if the person is infectious
     */
    public abstract boolean isInfectious(AbstractIndividualInterface p);

    /**
     * Check if the infection could be transmitted from src to target. While other implementation/interpretation are possible, by default, the probability of
     * successful transmission will be based purely on the pathological behaviour of the infection. For example, the usage of condom will not effect this
     * function, but will be effected elsewhere
     *
     * @param src source of infection
     * @param target target of infection
     * @see population.relationshipMap.Relationship#performAct(population.person.SingleRelationshipTypePerson[], int, infection.AbstractInfection[], boolean[])
     * @return true if it is possible for transmission to occur
     */
    public abstract boolean couldTransmissInfection(AbstractIndividualInterface src, AbstractIndividualInterface target);

    /**
     * Check if the person is infected to the disease modelled. Depends on infection type it might or might not be the same to the result from isInfectious
     * method. e.g. a exposed person will be infected, but might not necessary be infectious.
     *
     * @param p AbstractPerson to be checked
     * @return true if the person is infected
     * @see AbstractInfection#isInfectious(population.MovablePerson)
     */
    public abstract boolean isInfected(AbstractIndividualInterface p);

    /**
     * Check if the person has symptoms.
     *
     * @param p person to be checked
     * @return true if the person has symptoms
     */
    public abstract boolean hasSymptoms(AbstractIndividualInterface p);

    /**
     * Set parameter of infection object.
     *
     * @param id parameter name
     * @param value parameter value
     * @return true if parameter is set correctly
     */
    public abstract boolean setParameter(String id, Object value);

    /**
     * Get parameter of infection object, using PATTERN by default      * <code>
      Usage:
     * paramStr = AbstractInfection.PARAM_DIST_PARAM_INDEX_REGEX.replaceFirst("\\d+",
     * Integer.toString(ChlamydiaInfection.DIST_INFECT_SYM_DUR_INDEX));
     * double[] inf_dur = (dobule[]) getParameter(paramStr);      *
     * </code>
     *
     * @param id parameter name
     * @return parameter to be return
     */
    public Object getParameter(String id) {
        Matcher m;
        int idNum;
        if ((m = PATTERN_DIST_PARAM_INDEX.matcher(id)).find()) {
            idNum = Integer.parseInt(m.group(1));
            return getDistributionState(idNum);
        } else if ((m = PATTERN_DIST_NEXT_VALIE.matcher(id)).find()) {
            idNum = Integer.parseInt(m.group(1));
            return getRandomDistValue(idNum);
        } else {
            return null; // Handle by subclass
        }

    }
}
