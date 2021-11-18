package infection;

import java.util.regex.Matcher;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import person.AbstractIndividualInterface;
import person.TreatablePersonInterface;
import random.RandomGenerator;

/**
 *
 * @author Ben Hui
 */
public class GeneralSEIRSInfection extends AbstractInfection implements TreatableInfectionInterface {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9143931227762965254L;

	public static final String[] INF_STATUS = {"Exposed", "Asymptomatic", "Symptomatic", "Immune"};

    public static final int PARAM_INDEX_TRAN_FEMALE_MALE = 0;
    public static final int PARAM_INDEX_TRAN_MALE_FEMALE = PARAM_INDEX_TRAN_FEMALE_MALE + 1;
    public static final int PARAM_INDEX_SYM_FEMALE = PARAM_INDEX_TRAN_MALE_FEMALE + 1;
    public static final int PARAM_INDEX_SYM_MALE = PARAM_INDEX_SYM_FEMALE + 1;
    public static final int PARAM_INDEX_ASY_INF_DUR_FEMALE = PARAM_INDEX_SYM_MALE + 1;
    public static final int PARAM_INDEX_ASY_INF_DUR_MALE = PARAM_INDEX_ASY_INF_DUR_FEMALE + 1;
    public static final int PARAM_INDEX_SYM_INF_DUR_FEMALE = PARAM_INDEX_ASY_INF_DUR_MALE + 1;
    public static final int PARAM_INDEX_SYM_INF_DUR_MALE = PARAM_INDEX_SYM_INF_DUR_FEMALE + 1;
    public static final int PARAM_INDEX_EXPOSE_DUR_INDEX = PARAM_INDEX_SYM_INF_DUR_MALE + 1;
    public static final int PARAM_INDEX_IMMUNE_DUR_INDEX = PARAM_INDEX_EXPOSE_DUR_INDEX + 1;
    public static final int NUM_DIST_TOTAL = PARAM_INDEX_SYM_INF_DUR_MALE + 1;

    public GeneralSEIRSInfection(RandomGenerator RNG) {
        super(RNG);
        super.setInfectionState(INF_STATUS);                
    }
    
    public static double[][] getDefaultDistributionState(){
        return new double[NUM_DIST_TOTAL][2];
    }

    public static AbstractRealDistribution[] getDefaultDistribution(RandomGenerator infectionRNG) {
        AbstractRealDistribution[] distributions = new AbstractRealDistribution[GeneralSEIRSInfection.NUM_DIST_TOTAL];
        // Use dummy value for now
        distributions[GeneralSEIRSInfection.PARAM_INDEX_TRAN_FEMALE_MALE] = new BetaDistribution(infectionRNG, 1, 1);
        distributions[GeneralSEIRSInfection.PARAM_INDEX_TRAN_MALE_FEMALE] = new BetaDistribution(infectionRNG, 1, 1);
        distributions[GeneralSEIRSInfection.PARAM_INDEX_SYM_FEMALE] = new BetaDistribution(infectionRNG, 1, 1);
        distributions[GeneralSEIRSInfection.PARAM_INDEX_SYM_MALE] = new BetaDistribution(infectionRNG, 1, 1);
        distributions[GeneralSEIRSInfection.PARAM_INDEX_ASY_INF_DUR_FEMALE] = new GammaDistribution(infectionRNG, 1, 1);
        distributions[GeneralSEIRSInfection.PARAM_INDEX_ASY_INF_DUR_MALE] = new GammaDistribution(infectionRNG, 1, 1);
        distributions[GeneralSEIRSInfection.PARAM_INDEX_SYM_INF_DUR_FEMALE] = new GammaDistribution(infectionRNG, 1, 1);
        distributions[GeneralSEIRSInfection.PARAM_INDEX_SYM_INF_DUR_MALE] = new GammaDistribution(infectionRNG, 1, 1);
        distributions[GeneralSEIRSInfection.PARAM_INDEX_EXPOSE_DUR_INDEX] = new GammaDistribution(infectionRNG, 1, 1);
        distributions[GeneralSEIRSInfection.PARAM_INDEX_IMMUNE_DUR_INDEX] = new GammaDistribution(infectionRNG, 1, 1);
        return distributions;
    }

    @Override
    public void storeDistributions(AbstractRealDistribution[] distributions, double[][] distStates) {
        super.storeDistributions(distributions, distStates);
    }

    @Override
    public double advancesState(AbstractIndividualInterface p) {

        double res = Double.POSITIVE_INFINITY;
        // Could be non-zero at pre-sim age        
        switch (this.getCurrentInfectionState(p)) {
            case STATUS_EXP: // E -> A or Y
                // Determine if the person has symptoms when they become infectious
                double sym;
                if (p.isMale()) {
                    sym = getRandomDistValue(PARAM_INDEX_SYM_MALE);
                } else {
                    sym = getRandomDistValue(PARAM_INDEX_SYM_FEMALE);
                }
                // Infection occurs
                int infectState = this.getRNG().nextDouble() < sym ? STATUS_SYM : STATUS_ASY;
                if (infectState == STATUS_ASY) {
                    res = p.isMale() ? getRandomDistValue(PARAM_INDEX_ASY_INF_DUR_MALE) : getRandomDistValue(PARAM_INDEX_ASY_INF_DUR_FEMALE);
                } else {
                    res = p.isMale() ? getRandomDistValue(PARAM_INDEX_SYM_INF_DUR_MALE) : getRandomDistValue(PARAM_INDEX_SYM_INF_DUR_FEMALE);
                }
                setInfection(p, infectState, res);
                break;
            case STATUS_ASY: // A -> I
                res = getRandomDistValue(PARAM_INDEX_IMMUNE_DUR_INDEX); 
                this.setInfection(p, STATUS_IMM, res);
                break;
            case STATUS_SYM:
                // Y -> I
                //*
                res = getRandomDistValue(PARAM_INDEX_IMMUNE_DUR_INDEX); 
                this.setInfection(p, STATUS_IMM, res);
                //                
                break;
            case STATUS_IMM: // Immumne -> S
                this.setInfection(p, AbstractIndividualInterface.INFECT_S, Double.POSITIVE_INFINITY);
                res = Double.POSITIVE_INFINITY;
                break;
        }
        return res;

    }

    @Override
    public double infecting(AbstractIndividualInterface target) {
        double res = Double.POSITIVE_INFINITY;
        if (target.getInfectionStatus(this.getInfectionIndex()) == AbstractIndividualInterface.INFECT_S) {
            res = getRandomDistValue(PARAM_INDEX_EXPOSE_DUR_INDEX);
            setInfection(target, STATUS_EXP, res);
            setInfectedAt(target, target.getAge());
        }
        return res;
    }

    @Override
    public boolean isInfectious(AbstractIndividualInterface p) {
        int s = getCurrentInfectionState(p);
        return s == STATUS_ASY || s == STATUS_SYM; // Immune?
    }

    @Override
    public boolean couldTransmissInfection(AbstractIndividualInterface src, AbstractIndividualInterface tar) {
        boolean transmission = isInfectious(src) && (tar.getInfectionStatus(this.getInfectionIndex()) == AbstractIndividualInterface.INFECT_S);
        if (transmission) {
            double trans;
            if (tar.isMale()) {
                trans = getRandomDistValue(PARAM_INDEX_TRAN_FEMALE_MALE); //dist_trans_fm;
            } else {
                trans = getRandomDistValue(PARAM_INDEX_TRAN_MALE_FEMALE); //dist_trans_mf;
            }
            transmission = this.getRNG().nextDouble() < trans;
        }
        return transmission;

    }

    @Override
    public boolean isInfected(AbstractIndividualInterface p) {
        int s = getCurrentInfectionState(p);
        return s == STATUS_ASY || s == STATUS_SYM || s == STATUS_EXP; // Immune?
    }

    @Override
    public boolean hasSymptoms(AbstractIndividualInterface p) {
        int s = getCurrentInfectionState(p);
        return s == STATUS_SYM; // Immune?
    }

    @Override
    public boolean setParameter(String id, Object value) {
        Matcher m;
        m = PATTERN_DIST_PARAM_INDEX.matcher(id);
        if (m.find()) {
            int idNum = Integer.parseInt(m.group(1));
            double[] distState = (double[]) value;
            setDistributionState(idNum, distState);
            return true;
        } else {
            throw new UnsupportedOperationException(getClass().getName() + ".setParameter: Param id = " + id + " not supported.");
        }
    }

    @Override
    public boolean applyTreatmentAt(TreatablePersonInterface treated, int time) {
        // Only possible if the SingleRelationshipTypePerson is treatable and infected
        boolean suc = isInfected(treated);
        if (suc) {
            setInfection(treated, AbstractIndividualInterface.INFECT_S, Double.POSITIVE_INFINITY);
        }
        treated.setLastTreatedAt(time);
        return suc;
    }

}
