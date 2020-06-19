
  CREATE TABLE UT_METRIC
   (	METRIC_NBR NUMBER(9,0),
	METRIC_ID VARCHAR2(16),
	METRIC_DESCR VARCHAR2(80),
	METRIC_QRY CLOB,
	 CONSTRAINT UT_METRIC_PK PRIMARY KEY (METRIC_NBR),
	 CONSTRAINT UT_METRIC_UK UNIQUE (METRIC_ID)
   );


