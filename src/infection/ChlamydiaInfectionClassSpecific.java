package infection;

import java.io.Serializable;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import person.AbstractIndividualInterface;
import random.RandomGenerator;
import util.PersonClassifier;

/**
 *
 * @author Ben Hui
 */
public class ChlamydiaInfectionClassSpecific extends ChlamydiaInfection implements Serializable {

    private final PersonClassifier personClassifier;
    private final AbstractRealDistribution[][] classSpecificDist; // classId, distIndex
    private final double[][][] classSpecificDistVar;

    public ChlamydiaInfectionClassSpecific(RandomGenerator RNG,
            PersonClassifier personClassifier,
            AbstractRealDistribution[][] classSpecificDist,
            double[][][] classSpecificDistVar) {
        super(RNG);
        this.personClassifier = personClassifier;
        this.classSpecificDist = classSpecificDist;
        this.classSpecificDistVar = classSpecificDistVar;
    }

    public PersonClassifier getPersonClassifier() {
        return personClassifier;
    }

    public AbstractRealDistribution[][] getClassSpecificDist() {
        return classSpecificDist;
    }

    public double[][][] getClassSpecificDistVar() {
        return classSpecificDistVar;
    }

    protected double getClassSpecificRandomDistValue(int classId, int distId) {

        if (classId >= 0 && classSpecificDist[classId] != null
                && classSpecificDistVar[classId][distId] != null) {
            if (classSpecificDist[classId][distId] != null && classSpecificDistVar[classId][distId][1] != 0) {
                return classSpecificDist[classId][distId].sample();
            } else {
                return classSpecificDistVar[classId][distId][0]; // return mean value if sd =0 or distribution not defined
            }
        } else {
            // Return default            
            return getRandomDistValue(distId);
        }
    }

    @Override
    public boolean couldTransmissInfection(AbstractIndividualInterface src, AbstractIndividualInterface tar) {
        boolean transmission = isInfectious(src) && (tar.getInfectionStatus(this.getInfectionIndex()) == AbstractIndividualInterface.INFECT_S);

        if (transmission) {
            if (getPersonClassifier() != null) {
                int classId = getPersonClassifier().classifyPerson(tar);
                if (classId >= 0) {
                    double trans = getClassSpecificRandomDistValue(classId, tar.isMale() ? DIST_TRANS_FM_INDEX : DIST_TRANS_MF_INDEX);
                    return this.getRNG().nextDouble() < trans;
                }
            }
            return super.couldTransmissInfection(src, tar);
        } else {
            return transmission;
        }

    }

    @Override
    public double advancesState(AbstractIndividualInterface p) {
        if (getPersonClassifier() != null) {

            int classId = getPersonClassifier().classifyPerson(p);

            if (classId >= 0) {

                if (getCurrentInfectionState(p) == STATUS_EXP) {
                    double res = Double.POSITIVE_INFINITY;
                    // E -> A or Y , overwriting the non-gender specific code
                    double sym;
                    if (p.isMale()) {
                        sym = getClassSpecificRandomDistValue(classId, DIST_SYM_MALE_INDEX);
                    } else {
                        sym = getClassSpecificRandomDistValue(classId, DIST_SYM_FEMALE_INDEX);
                    }
                    // Infection occurs
                    int infectState = this.getRNG().nextDouble() < sym ? STATUS_SYM : STATUS_ASY;
                    if (infectState == STATUS_ASY) {
                        res = getClassSpecificRandomDistValue(classId, DIST_INFECT_ASY_DUR_INDEX);
                    } else {
                        res = getClassSpecificRandomDistValue(classId, DIST_INFECT_SYM_DUR_INDEX);
                    }
                    setInfection(p, infectState, res);
                    return res;
                }  //  if (getCurrentInfectionState(p) == STATUS_EXP) {

            }// classId < 0
        } // if (getPersonClassifier() != null) {

        // If reach here that means use default
        return super.advancesState(p);
    }

}
