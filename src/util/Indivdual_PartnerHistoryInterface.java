package util;

import person.AbstractIndividualInterface;

/**
 * @author Ben Hui
 *
 * History:
 */
public interface Indivdual_PartnerHistoryInterface extends AbstractIndividualInterface, LongFieldsInterface {   

    public int[] getPartnerHistoryLifetimePID();

    public int[] getPartnerHistoryLifetimeAtAge();

    public int[] getPartnerHistoryRelLength();

    public int getPartnerHistoryLifetimePt();

    public void setPartnerHistoryLifetimePt(int partnerHistoryLifetimePt);

    public void addPartnerAtAge(int age, int partnerId, int relLength);

    public void ensureHistoryLength(int ensuredHistoryLength);

    public int numPartnerFromAge(double ageToCheck);  

    public int getNumPartnerInPastYear();
    
    public void copyPartnerHistory(Indivdual_PartnerHistoryInterface clone);

}
