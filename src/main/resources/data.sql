--
--    Copyright 2015-2019 the original author or authors.
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--

INSERT INTO CITY (NAME, STATE, COUNTRY) VALUES ('San Francisco', 'CA', 'US');
INSERT INTO CITY (NAME, STATE, COUNTRY) VALUES ('Newyork', 'NY', 'US');
INSERT INTO CITY (NAME, STATE, COUNTRY) VALUES ('Washington', 'WD', 'US');
INSERT INTO CITY (NAME, STATE, COUNTRY) VALUES ('Dug', 'CA', 'US');

INSERT INTO STUDENT (STUDENT_NAME, STATE, STANDARD, DIVISION) VALUES ('Billy', 'CA', '5th', 'B');
INSERT INTO STUDENT (STUDENT_NAME, STATE, STANDARD, DIVISION) VALUES ('Xilly', 'AR', '5th', 'B');
INSERT INTO STUDENT (STUDENT_NAME, STATE, STANDARD, DIVISION) VALUES ('Ally', 'NY', '6th', 'A');
INSERT INTO STUDENT (STUDENT_NAME, STATE, STANDARD, DIVISION) VALUES ('Filly', 'CA', '7th', 'B');
INSERT INTO STUDENT (STUDENT_NAME, STATE, STANDARD, DIVISION) VALUES ('Yilly', 'DC', '7th', 'B');
INSERT INTO STUDENT (STUDENT_NAME, STATE, STANDARD, DIVISION) VALUES ('Zi,lly', 'CA', '7th', 'B');
INSERT INTO STUDENT (STUDENT_NAME, STATE, STANDARD, DIVISION) VALUES ('Hilly', 'WD', '8th', 'A');
INSERT INTO STUDENT (STUDENT_NAME, STATE, STANDARD, DIVISION) VALUES ('Tilly', 'NY', '9th', 'B');

INSERT INTO PRE_DEFINED_SQL (SQL_ID, SQL_TEXT, PARAM_DEFAULT_VALUES, IS_ACTIVE) VALUES ('get_b_div_students', 'SELECT * FROM STUDENT WHERE DIVISION=''B''', null, 'Y');
INSERT INTO PRE_DEFINED_SQL (SQL_ID, SQL_TEXT, PARAM_DEFAULT_VALUES, IS_ACTIVE) VALUES ('get_students_by_standard', 'SELECT * FROM STUDENT WHERE STANDARD = #{std}', 'std=7th', 'Y');
INSERT INTO PRE_DEFINED_SQL (SQL_ID, SQL_TEXT, PARAM_DEFAULT_VALUES, IS_ACTIVE) VALUES ('get_students_by_state_div', 'SELECT * FROM STUDENT WHERE STATE = #{state} AND DIVISION = #{div}', 'state=WD,div=A', 'Y');
