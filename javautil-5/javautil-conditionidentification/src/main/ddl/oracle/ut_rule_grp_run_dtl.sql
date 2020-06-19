  CREATE TABLE UT_RULE_GRP_RUN_DTL
   (	UT_RULE_GRP_RUN_NBR NUMBER(18,0) NOT NULL ENABLE,
        UT_TABLE_RULE_HIST_NBR NUMBER(18,0) NOT NULL ENABLE,
        EXCEPTION_CNT NUMBER(9),
	 CONSTRAINT UT_RULE_GRP_RUN_DTL_PK PRIMARY KEY (UT_RULE_GRP_RUN_NBR, UT_TABLE_RULE_HIST_NBR),
	 CONSTRAINT URGRD_URGR_FK FOREIGN KEY (UT_RULE_GRP_RUN_NBR)
	  REFERENCES UT_RULE_GRP_RUN (UT_RULE_GRP_RUN_NBR) ENABLE,
	 CONSTRAINT URGRD_UTRH_FK FOREIGN KEY (UT_TABLE_RULE_HIST_NBR)
	  REFERENCES UT_TABLE_RULE_HIST (UT_TABLE_RULE_HIST_NBR) ENABLE
   );

