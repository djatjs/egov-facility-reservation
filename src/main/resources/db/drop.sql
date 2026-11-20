-- 모든 테이블·시퀀스 삭제 (초기화용). 이후 ddl.sql → data.sql 순서로 다시 실행한다.
BEGIN
    FOR t IN (SELECT table_name FROM user_tables WHERE table_name LIKE 'TB\_%' ESCAPE '\') LOOP
        EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS PURGE';
    END LOOP;
    FOR s IN (SELECT sequence_name FROM user_sequences WHERE sequence_name LIKE 'SEQ\_%' ESCAPE '\') LOOP
        EXECUTE IMMEDIATE 'DROP SEQUENCE ' || s.sequence_name;
    END LOOP;
END;
/
