package org.javautil.dblogging.dto;
// Generated Sep 10, 2011 1:41:31 PM by Hibernate Tools 3.2.2.GA



/**
 * UtProcessStatId generated by hbm2java
 */
public class UtProcessStatId  implements java.io.Serializable {


     private int utProcessStatusNbr;
     private int logSeqNbr;
     private int statisticNbr;

    public UtProcessStatId() {
    }

    public UtProcessStatId(int utProcessStatusNbr, int logSeqNbr, int statisticNbr) {
       this.utProcessStatusNbr = utProcessStatusNbr;
       this.logSeqNbr = logSeqNbr;
       this.statisticNbr = statisticNbr;
    }
   
    public int getUtProcessStatusNbr() {
        return this.utProcessStatusNbr;
    }
    
    public void setUtProcessStatusNbr(int utProcessStatusNbr) {
        this.utProcessStatusNbr = utProcessStatusNbr;
    }
    public int getLogSeqNbr() {
        return this.logSeqNbr;
    }
    
    public void setLogSeqNbr(int logSeqNbr) {
        this.logSeqNbr = logSeqNbr;
    }
    public int getStatisticNbr() {
        return this.statisticNbr;
    }
    
    public void setStatisticNbr(int statisticNbr) {
        this.statisticNbr = statisticNbr;
    }




}


