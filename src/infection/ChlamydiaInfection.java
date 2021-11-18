package infection;

import java.util.regex.Matcher;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import person.AbstractIndividualInterface;
import person.TreatablePersonInterface;

import random.RandomGenerator;

/**
 * <p>
 * An infection objects for chlamydia, based on the specification listed in Johnson et al. 2002</p>
 *
 * <p>
 * The pathology of chlamydia is modelled using 5 stages. An <u>susceptible</u> could be infected and become <u>exposed</u> to infection. An exposed can becomes
 * infectious <u>with symptoms</u> or
 * <u>asymptomatically</u>.All infections will recover naturally, and there will be a period of <u>immunity</u> before the the individual becomes susceptible
 * again.</p>
 *
 * <p>
 * Reference:</p>
 *
 * <pre>Johnson, L.F., Alkema, L., Dorrington, R.E.: "A Bayesian approach to uncertainty
 * analysis of sexually transmitted infection model", Sexually Transmitted Infection,
 * 2009</pre>
 *
 * @author Ben Hui
 */
public class ChlamydiaInfection extends AbstractInfection implements TreatableInfectionInterface {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3192605672124834831L;
	public static final int DEFAULT_CHLAM_INDEX = 1;
    public static final String[] CHLAM_STATUS = {"Exposed", "Asymptomatic", "Symptomatic", "Immune"};

    // Gamma dist, with mean, SD in days
    private final double[] DEFAULT_INFLECT_ASY_DURATION = {296, 188}; //From: Kretzschmar, Korenromp, Regan, Althaus Johnson, Turner;
    private final double[] DEFAULT_INFECT_SYM_DURATION = {296, 188}; //{16 * 7, 5 * 7};
    private final double[] DEFAULT_EXPOSED_DURATION = {12, 28};
    private final double[] DEFAULT_IMMUNE_DURATION = {45, 15};
    private final double DEFAULT_IMMUNE_AFTER_CURE = 0.5;
    // Beta dist, with mean, SD as probability
    private final double[] DEFAULT_SYM_M = {0.30, 0.15};
    private final double[] DEFAULT_SYM_F = {0.15, 0.08};
    private final double[] DEFAULT_TRANS_MF = {0.16, 0.10};
    private final double[] DEFAULT_TRANS_FM = {0.12, 0.06};
    private final double[][] DEF_DIST_VAR = {
        DEFAULT_INFLECT_ASY_DURATION, DEFAULT_INFECT_SYM_DURATION, DEFAULT_SYM_M, DEFAULT_SYM_F,
        DEFAULT_TRANS_MF, DEFAULT_TRANS_FM, DEFAULT_EXPOSED_DURATION, DEFAULT_IMMUNE_DURATION
    };
    public static final int DIST_INFECT_ASY_DUR_INDEX = 0;
    public static final int DIST_INFECT_SYM_DUR_INDEX = DIST_INFECT_ASY_DUR_INDEX + 1;
    public static final int DIST_SYM_MALE_INDEX = DIST_INFECT_SYM_DUR_INDEX + 1;
    public static final int DIST_SYM_FEMALE_INDEX = DIST_SYM_MALE_INDEX + 1;
    public static final int DIST_TRANS_MF_INDEX = DIST_SYM_FEMALE_INDEX + 1;
    public static final int DIST_TRANS_FM_INDEX = DIST_TRANS_MF_INDEX + 1;
    public static final int DIST_EXPOSED_DUR_INDEX = DIST_TRANS_FM_INDEX + 1;
    public static final int DIST_IMMUNE_DUR_INDEX = DIST_EXPOSED_DUR_INDEX + 1;
    public static final int DIST_TOTAL = DIST_IMMUNE_DUR_INDEX + 1;

    public ChlamydiaInfection(RandomGenerator RNG) {
        super(RNG);
        this.setInfectionIndex(DEFAULT_CHLAM_INDEX);
        this.setInfectionState(CHLAM_STATUS);
        AbstractRealDistribution[] distributions = new AbstractRealDistribution[DIST_TOTAL];

        double[] var;
        if (DEF_DIST_VAR[DIST_INFECT_ASY_DUR_INDEX][1] != 0) {
            var = this.generatedGammaParam(DEFAULT_INFLECT_ASY_DURATION);
            distributions[DIST_INFECT_ASY_DUR_INDEX] = new GammaDistribution(this.getRNG(), var[0], 1 / var[1]);
        }
        if (DEF_DIST_VAR[DIST_INFECT_SYM_DUR_INDEX][1] != 0) {
            var = this.generatedGammaParam(DEFAULT_INFECT_SYM_DURATION);
            distributions[DIST_INFECT_SYM_DUR_INDEX] = new GammaDistribution(this.getRNG(), var[0], 1 / var[1]);
        }
        if (DEF_DIST_VAR[DIST_SYM_MALE_INDEX][1] != 0) {
            var = this.generatedBetaParam(DEFAULT_SYM_M);
            distributions[DIST_SYM_MALE_INDEX] = new BetaDistribution(this.getRNG(), var[0], var[1]);
        }
        if (DEF_DIST_VAR[DIST_SYM_FEMALE_INDEX][1] != 0) {
            var = this.generatedBetaParam(DEFAULT_SYM_F);
            distributions[DIST_SYM_FEMALE_INDEX] = new BetaDistribution(this.getRNG(), var[0], var[1]);
        }
        if (DEF_DIST_VAR[DIST_TRANS_MF_INDEX][1] != 0) {
            var = this.generatedBetaParam(DEFAULT_TRANS_MF);
            distributions[DIST_TRANS_MF_INDEX] = new BetaDistribution(this.getRNG(), var[0], var[1]);
        }
        if (DEF_DIST_VAR[DIST_TRANS_FM_INDEX][1] != 0) {
            var = this.generatedBetaParam(DEFAULT_TRANS_FM);
            distributions[DIST_TRANS_FM_INDEX] = new BetaDistribution(this.getRNG(), var[0], var[1]);
        }
        if (DEF_DIST_VAR[DIST_EXPOSED_DUR_INDEX][1] != 0) {
            //var = generatedGammaParam(DEFAULT_EXPOSED_DURATION);
            //distributions[DIST_EXPOSED_DUR_INDEX] = new cern.jet.random.Gamma(var[0], var[1], getRNG());            
            var = DEF_DIST_VAR[DIST_EXPOSED_DUR_INDEX];
            distributions[DIST_EXPOSED_DUR_INDEX] = new UniformRealDistribution(this.getRNG(), var[0], var[1]);
        }
        if (DEF_DIST_VAR[DIST_IMMUNE_DUR_INDEX][1] != 0) {
            var = generatedGammaParam(DEFAULT_IMMUNE_DURATION);
            distributions[DIST_IMMUNE_DUR_INDEX] = new GammaDistribution(this.getRNG(), var[0], 1 / var[1]);
        }
        storeDistributions(distributions, DEF_DIST_VAR);

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
                    sym = getRandomDistValue(DIST_SYM_MALE_INDEX);
                } else {
                    sym = getRandomDistValue(DIST_SYM_FEMALE_INDEX);
                }
                // Infection occurs
                int infectState = this.getRNG().nextDouble() < sym ? STATUS_SYM : STATUS_ASY;
                if (infectState == STATUS_ASY) {
                    res = getRandomDistValue(DIST_INFECT_ASY_DUR_INDEX);
                } else {
                    res = getRandomDistValue(DIST_INFECT_SYM_DUR_INDEX);
                }
                setInfection(p, infectState, res);
                break;
            case STATUS_ASY: // A -> I
                res = getRandomDistValue(DIST_IMMUNE_DUR_INDEX); //dist_immunity.nextDouble();
                this.setInfection(p, STATUS_IMM, res);
                break;
            case STATUS_SYM:
                // Y -> I
                //*
                res = getRandomDistValue(DIST_IMMUNE_DUR_INDEX); //dist_immunity.nextDouble();
                this.setInfection(p, STATUS_IMM, res);
                //*/
                // Y -> A or I
                /*
                 * double totalInfectDur = getRandomDistValue(DIST_INFECT_ASY_DUR_INDEX); if (totalInfectDur > infectedSince) { // Additional time as asymptomatic res = totalInfectDur - infectedSince;
                 * setInfection(p, STATUS_ASY, res); } else { // Become immune immediately // Unlikely if getRandomDistValue(DIST_INFECT_DUR_INDEX) >> getRandomDistValue(DIST_SYM_DUR_INDEX); res =
                 * getRandomDistValue(DIST_IMMUNE_DUR_INDEX); setInfection(p, STATUS_IMM, res); } //
                 */
                break;
            case STATUS_IMM: // Immumne -> S
                this.setInfection(p, AbstractIndividualInterface.INFECT_S, Double.POSITIVE_INFINITY);
                res = Double.POSITIVE_INFINITY;
                break;
        }
        return res;

    }

    @Override
    public double infecting(AbstractIndividualInterface tar) {
        double res = Double.POSITIVE_INFINITY;
        if (tar.getInfectionStatus(this.getInfectionIndex()) == AbstractIndividualInterface.INFECT_S) {
            res = getRandomDistValue(DIST_EXPOSED_DUR_INDEX);
            setInfection(tar, STATUS_EXP, res);           
            setInfectedAt( tar, tar.getAge());            
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
                trans = getRandomDistValue(DIST_TRANS_FM_INDEX); //dist_trans_fm;
            } else {
                trans = getRandomDistValue(DIST_TRANS_MF_INDEX); //dist_trans_mf;
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
    public boolean applyTreatmentAt(TreatablePersonInterface treated, int time) {
        // Only possible if the SingleRelationshipTypePerson is treatable and infected
        boolean suc = isInfected(treated);

        //treated.setLastActInfectious(getInfectionIndex(), false);
        if (suc) {
            // 50% chance immune
            boolean isImmune = getRNG().nextDouble() < DEFAULT_IMMUNE_AFTER_CURE;
            if (isImmune) {
                double res = getRandomDistValue(DIST_IMMUNE_DUR_INDEX);
                setInfection(treated, STATUS_IMM, res);
            } else {
                setInfection(treated, AbstractIndividualInterface.INFECT_S, Double.POSITIVE_INFINITY);
            }
            treated.setLastTreatedAt(time);

        }
        return suc;

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

}
