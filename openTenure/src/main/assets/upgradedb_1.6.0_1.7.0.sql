ALTER TABLE PERSON ADD BIRTH_PROVINCE_CODE VARCHAR(255);
ALTER TABLE PERSON ADD BIRTH_MUNICIPALITY_CODE VARCHAR(255);

UPDATE CONFIGURATION SET VALUE='1.7.0' WHERE NAME='DBVERSION' AND VALUE='1.6.0';