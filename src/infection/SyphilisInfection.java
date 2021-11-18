/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infection;

import java.util.regex.Matcher;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import person.AbstractIndividualInterface;
import person.TreatablePersonInterface;
import random.RandomGenerator;

/**
 *
 * @author Bhui
 */
public class SyphilisInfection extends infection.AbstractInfection implements infection.TreatableInfectionInterface {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7410186998176484107L;

	public static final String[] SYPHILIS_STATUS = {"Incubating",
        "Primary", "Secondary", "Early Latent", "Latent", "Tertiary",
        "Reission", "Recurrent", "Immunity"};

    public static final int STATUS_INCUB = 0;
    public static final int STATUS_PRIMARY = STATUS_INCUB + 1;
    public static final int STATUS_SECONDARY = STATUS_PRIMARY + 1;
    public static final int STATUS_EARLY_LATENT = STATUS_SECONDARY + 1;
    public static final int STATUS_LATENT = STATUS_EARLY_LATENT + 1;
    public static final int STATUS_TERTIARY = STATUS_LATENT + 1;
    public static final int STATUS_REMISS = STATUS_TERTIARY + 1;
    public static final int STATUS_RECURRENT = STATUS_REMISS + 1;
    public static final int STATUS_IMMUN = STATUS_RECURRENT + 1;

    // Uniform dist version
    // 3 - 4 weeks,
    private final double[] DEFAULT_DURATION_INCUBATION = {21, 28};
    // 45 - 60 days
    private final double[] DEFAULT_DURATION_PRIMARY = {45, 60};
    // 100-140 days
    private final double[] DEFAULT_DURATION_SECONDARY = {100, 140};
    // Total infectious 1-2 years
    private final double[] DEFAULT_DURATION_EARLY_LATENT = {360 - (28 + 60 + 140), 720 - (21 + 45 + 100)};
    // Remission = 6 months   
    private final double[] DEFAULT_DURATION_REMISSION = {6 * 30, 0};
    // Relapse = 90 days
    private final double[] DEFAULT_DURATION_RECURRENT = {90, 0};

    // 15 years from Latency to Tertiary
    private final double[] DEFAULT_DURATION_LATENT = {15 * 360, 0};
    // Protective immunity from treamtent = 5 years
    private final double[] DEFAULT_DURATION_IMMUN = {5 * 360, 0};

    private final double[] DEFAULT_TRANS_INCUBATION_MF = {0.18, 0};
    private final double[] DEFAULT_TRANS_PRI_MF = {0.18, 0};
    private final double[] DEFAULT_TRANS_SEC_MF = {0.18, 0};
    private final double[] DEFAULT_TRANS_EARLY_LATENT_MF = {0.09, 0};
    private final double[] DEFAULT_TRANS_RECURENT_MF = {0.18, 0};

    private final double[] DEFAULT_TRANS_INCUBATION_FM = {0.15, 0};
    private final double[] DEFAULT_TRANS_PRI_FM = {0.15, 0};
    private final double[] DEFAULT_TRANS_SEC_FM = {0.15, 0};
    private final double[] DEFAULT_TRANS_EARLY_LATENT_FM = {0.075, 0};
    private final double[] DEFAULT_TRANS_RECURENT_FM = {0.15, 0};

    private final double[] DEFAULT_PERCENT_RECUR = {0.25, 0};

    private final double[][] DEF_DIST_VAR = {
        DEFAULT_DURATION_INCUBATION,
        DEFAULT_DURATION_PRIMARY,
        DEFAULT_DURATION_SECONDARY,
        DEFAULT_DURATION_EARLY_LATENT,
        DEFAULT_DURATION_LATENT,
        DEFAULT_DURATION_REMISSION,
        DEFAULT_DURATION_RECURRENT,
        DEFAULT_DURATION_IMMUN,
        DEFAULT_TRANS_INCUBATION_MF,
        DEFAULT_TRANS_PRI_MF,
        DEFAULT_TRANS_SEC_MF,
        DEFAULT_TRANS_EARLY_LATENT_MF,
        DEFAULT_TRANS_RECURENT_MF,
        DEFAULT_TRANS_INCUBATION_FM,
        DEFAULT_TRANS_PRI_FM,
        DEFAULT_TRANS_SEC_FM,
        DEFAULT_TRANS_EARLY_LATENT_FM,
        DEFAULT_TRANS_RECURENT_FM,
        DEFAULT_PERCENT_RECUR,};

    public static final int DIST_INDEX_DURATION_INCUBATION = 0;
    public static final int DIST_INDEX_DURATION_PRIMARY = DIST_INDEX_DURATION_INCUBATION + 1;
    public static final int DIST_INDEX_DURATION_SECONDARY = DIST_INDEX_DURATION_PRIMARY + 1;
    public static final int DIST_INDEX_DURATION_EARLY_LATENT = DIST_INDEX_DURATION_SECONDARY + 1;
    public static final int DIST_INDEX_DURATION_LATENT = DIST_INDEX_DURATION_EARLY_LATENT + 1;
    public static final int DIST_INDEX_DURATION_REMISSION = DIST_INDEX_DURATION_LATENT + 1;
    public static final int DIST_INDEX_DURATION_RECURRENT = DIST_INDEX_DURATION_REMISSION + 1;
    public static final int DIST_INDEX_DURATION_IMMUN = DIST_INDEX_DURATION_RECURRENT + 1;
    public static final int DIST_INDEX_TRANS_INCUBATION_MF = DIST_INDEX_DURATION_IMMUN + 1;
    public static final int DIST_INDEX_TRANS_PRI_MF = DIST_INDEX_TRANS_INCUBATION_MF + 1;
    public static final int DIST_INDEX_TRANS_SEC_MF = DIST_INDEX_TRANS_PRI_MF + 1;
    public static final int DIST_INDEX_TRANS_EARLY_LATENT_MF = DIST_INDEX_TRANS_SEC_MF + 1;
    public static final int DIST_INDEX_TRANS_RECURRENT_MF = DIST_INDEX_TRANS_EARLY_LATENT_MF + 1;
    public static final int DIST_INDEX_TRANS_INCUBATION_FM = DIST_INDEX_TRANS_RECURRENT_MF + 1;
    public static final int DIST_INDEX_TRANS_PRI_FM = DIST_INDEX_TRANS_INCUBATION_FM + 1;
    public static final int DIST_INDEX_TRANS_SEC_FM = DIST_INDEX_TRANS_PRI_FM + 1;
    public static final int DIST_INDEX_TRANS_EARLY_LATENT_FM = DIST_INDEX_TRANS_SEC_FM + 1;
    public static final int DIST_INDEX_TRANS_RECURRENT_FM = DIST_INDEX_TRANS_EARLY_LATENT_FM + 1;
    public static final int DIST_INDEX_PERCENT_RECUR = DIST_INDEX_TRANS_RECURRENT_FM + 1;
    public static final int DIST_TOTAL = DIST_INDEX_PERCENT_RECUR + 1;

    public SyphilisInfection(RandomGenerator RNG) {
        super(RNG);
        this.setInfectionState(SYPHILIS_STATUS);
        AbstractRealDistribution[] distributions = new AbstractRealDistribution[DEF_DIST_VAR.length];
        double[] var;

        for (int i = 0; i < distributions.length; i++) {
            var = DEF_DIST_VAR[i];
            distributions[i] = createDistribution(i, var);

        }

        storeDistributions(distributions, DEF_DIST_VAR);

    }

    public AbstractRealDistribution createDistribution(int dist_Id,
            double[] var) throws NumberIsTooLargeException {

        AbstractRealDistribution res = null;
        if (var[1] != 0) {
            switch (dist_Id) {
                case DIST_INDEX_DURATION_INCUBATION:
                case DIST_INDEX_DURATION_PRIMARY:
                case DIST_INDEX_DURATION_SECONDARY:
                case DIST_INDEX_DURATION_EARLY_LATENT:
                case DIST_INDEX_DURATION_LATENT:
                case DIST_INDEX_DURATION_REMISSION:
                case DIST_INDEX_DURATION_RECURRENT:
                case DIST_INDEX_DURATION_IMMUN:
                    res = new UniformRealDistribution(this.getRNG(), var[0], var[1]);
                    break;
                case DIST_INDEX_TRANS_INCUBATION_MF:
                case DIST_INDEX_TRANS_PRI_MF:
                case DIST_INDEX_TRANS_SEC_MF:
                case DIST_INDEX_TRANS_EARLY_LATENT_MF:
                case DIST_INDEX_TRANS_RECURRENT_MF:
                case DIST_INDEX_TRANS_INCUBATION_FM:
                case DIST_INDEX_TRANS_PRI_FM:
                case DIST_INDEX_TRANS_SEC_FM:
                case DIST_INDEX_TRANS_EARLY_LATENT_FM:
                case DIST_INDEX_TRANS_RECURRENT_FM:
                case DIST_INDEX_PERCENT_RECUR:
                    double[] varBeta = generatedBetaParam(var);
                    res = new BetaDistribution(this.getRNG(), varBeta[0], varBeta[1]);
                    break;
                default:
                    System.err.println(this.getClass().getName() + ".allocateDistribution : Distribution index of " + dist_Id + " not defined");
            }
        }

        return res;
    }

    @Override
    public double advancesState(AbstractIndividualInterface p) {
        double res = Double.POSITIVE_INFINITY;
        switch (getCurrentInfectionState(p)) {
            case STATUS_INCUB:
                res = Math.round(getRandomDistValue(DIST_INDEX_DURATION_PRIMARY));
                setInfection(p, STATUS_PRIMARY, res);
                break;
            case STATUS_PRIMARY:
                res = Math.round(getRandomDistValue(DIST_INDEX_DURATION_SECONDARY));
                setInfection(p, STATUS_SECONDARY, res);
                break;
            case STATUS_SECONDARY:
                res = Math.round(getRandomDistValue(DIST_INDEX_DURATION_EARLY_LATENT));
                setInfection(p, STATUS_EARLY_LATENT, res);
                break;
            case STATUS_EARLY_LATENT:
                res = Math.round(getRandomDistValue(DIST_INDEX_PERCENT_RECUR));
                if (getRNG().nextDouble() < res) {
                    // Remission
                    res = Math.round(getRandomDistValue(DIST_INDEX_DURATION_REMISSION));
                    setInfection(p, STATUS_REMISS, res);
                } else {
                    // Latent 
                    res = Math.round(getRandomDistValue(DIST_INDEX_DURATION_LATENT));
                    setInfection(p, STATUS_LATENT, res);
                }
                break;
            case STATUS_LATENT:
                res = Double.POSITIVE_INFINITY;
                setInfection(p, STATUS_TERTIARY, res);
                break;
            case STATUS_REMISS:
                res = Math.round(getRandomDistValue(DIST_INDEX_DURATION_RECURRENT));
                setInfection(p, STATUS_RECURRENT, res);
                break;
            case STATUS_RECURRENT:
                res = Math.round(getRandomDistValue(DIST_INDEX_DURATION_LATENT));
                setInfection(p, STATUS_LATENT, res);
                break;
            case STATUS_IMMUN:
                setInfection(p, AbstractIndividualInterface.INFECT_S, Double.POSITIVE_INFINITY);
                break;

        }

        return res;

    }

    @Override
    public double infecting(AbstractIndividualInterface target) {
        double res = Double.POSITIVE_INFINITY;
        if (target.getInfectionStatus(this.getInfectionIndex()) == AbstractIndividualInterface.INFECT_S) {
            res = Math.round(getRandomDistValue(DIST_INDEX_DURATION_INCUBATION));
            setInfection(target, STATUS_INCUB, res);
            setInfectedAt(target, target.getAge());

        }
        return res;
    }

    @Override
    public boolean isInfectious(AbstractIndividualInterface p) {
        int s = getCurrentInfectionState(p);

        return s == STATUS_INCUB
                || s == STATUS_PRIMARY
                || s == STATUS_SECONDARY
                || s == STATUS_EARLY_LATENT
                || s == STATUS_RECURRENT;
    }

    @Override
    public boolean couldTransmissInfection(AbstractIndividualInterface src, AbstractIndividualInterface target) {
        boolean transmission = isInfectious(src) && (target.getInfectionStatus(this.getInfectionIndex()) == AbstractIndividualInterface.INFECT_S);
        if (transmission) {
            double trans;
            switch (getCurrentInfectionState(src)) {
                case STATUS_INCUB:
                    trans = target.isMale() ? getRandomDistValue(DIST_INDEX_TRANS_INCUBATION_FM) : getRandomDistValue(DIST_INDEX_TRANS_INCUBATION_MF);
                    break;
                case STATUS_PRIMARY:
                    trans = target.isMale() ? getRandomDistValue(DIST_INDEX_TRANS_PRI_FM) : getRandomDistValue(DIST_INDEX_TRANS_PRI_MF);
                    break;
                case STATUS_SECONDARY:
                    trans = target.isMale() ? getRandomDistValue(DIST_INDEX_TRANS_SEC_FM) : getRandomDistValue(DIST_INDEX_TRANS_SEC_MF);
                    break;
                case STATUS_EARLY_LATENT:
                    trans = target.isMale() ? getRandomDistValue(DIST_INDEX_TRANS_EARLY_LATENT_FM) : getRandomDistValue(DIST_INDEX_TRANS_EARLY_LATENT_MF);
                    break;
                case STATUS_RECURRENT:
                    trans = target.isMale() ? getRandomDistValue(DIST_INDEX_TRANS_RECURRENT_FM) : getRandomDistValue(DIST_INDEX_TRANS_RECURRENT_MF);
                    break;
                default:
                    trans = 0;
            }

            transmission = this.getRNG().nextDouble() < trans;
        }
        return transmission;

    }

    @Override
    public boolean isInfected(AbstractIndividualInterface p) {
        return getCurrentInfectionState(p) != AbstractIndividualInterface.INFECT_S;
    }

    @Override
    public boolean hasSymptoms(AbstractIndividualInterface p) {
        // Dummy
        return false;
    }

    @Override
    public boolean setParameter(String id, Object value) {
        Matcher m;
        m = PATTERN_DIST_PARAM_INDEX.matcher(id);
        if (m.find()) {
            int idNum = Integer.parseInt(m.group(1));
            double[] distState = (double[]) value;

            AbstractRealDistribution dist = getDistribution(idNum);

            // Create a new dist
            if (dist == null) {
                dist = createDistribution(idNum, distState);
                setDistribution(idNum, dist);
            }

            setDistributionState(idNum, (double[]) distState);
            return true;
        } else {
            System.err.println(this.getClass().getName() + ".setParameter : Value not a valid object for parameter id \"" + id + "\"");
            return false;
        }
    }

    @Override
    public boolean applyTreatmentAt(TreatablePersonInterface treated, int time) {
        // Only possible if the SingleRelationshipTypePerson is treatable and infected
        boolean suc = isInfected(treated);
        if (suc) {
            int inf_stat = getCurrentInfectionState(treated);

            switch (inf_stat) {
                case STATUS_INCUB:
                case STATUS_PRIMARY:
                case STATUS_SECONDARY:
                case STATUS_EARLY_LATENT:
                    setInfection(treated, AbstractIndividualInterface.INFECT_S, Double.POSITIVE_INFINITY);
                    break;
                case STATUS_LATENT:
                case STATUS_TERTIARY:
                case STATUS_REMISS:
                case STATUS_RECURRENT:
                    setInfection(treated, STATUS_IMMUN, getRandomDistValue(DIST_INDEX_DURATION_IMMUN));
                    break;
            }
            treated.setLastTreatedAt(time);
        }

        return suc;
    }

}
