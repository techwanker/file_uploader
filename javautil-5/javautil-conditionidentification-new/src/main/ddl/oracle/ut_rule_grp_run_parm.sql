
  CREATE TABLE UT_RULE_GRP_RUN_PARM
   (	UT_RULE_GRP_RUN_NBR NUMBER(18,0) NOT NULL ENABLE,
	PARM_NM VARCHAR2(30) NOT NULL ENABLE,
	PARM_VALUE_STRING VARCHAR2(255),
	PARM_VALUE_NUMBER NUMBER,
	PARM_VALUE_DATE DATE,
	 CONSTRAINT UT_RULE_GRP_RUN_PARM_PK PRIMARY KEY (UT_RULE_GRP_RUN_NBR, PARM_NM),
	 CONSTRAINT URGRP_URGR_FK FOREIGN KEY (UT_RULE_GRP_RUN_NBR)
	  REFERENCES UT_RULE_GRP_RUN (UT_RULE_GRP_RUN_NBR) ENABLE
   );

