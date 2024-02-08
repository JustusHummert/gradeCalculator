USE gradecalculator;
INSERT INTO modules(moduleID, modulename) VALUES (0, 'Informatik 1');
INSERT INTO modules(moduleID, modulename) VALUES (1, 'Analysis 1');
INSERT INTO modules(moduleID, modulename) VALUES (2, 'Lineare Algebra 1');

INSERT INTO subjects(subjectID, subjectname) VALUES (0, 'Informatik');
INSERT INTO subjects(subjectID, subjectname) VALUES (1, 'Mathe');

INSERT INTO users(username, password) VALUES ('Justus', 'password');
INSERT INTO users(username, password) VALUES ('Jonas', 'password');

INSERT INTO modules_in_subjects(moduleID, subjectID, gradingFactor) VALUES (0, 0, 0.1);
INSERT INTO modules_in_subjects(moduleID, subjectID, gradingFactor) VALUES (1, 0, 0.08);
INSERT INTO modules_in_subjects(moduleID, subjectID, gradingFactor) VALUES (2, 0, 0.05);
INSERT INTO modules_in_subjects(moduleID, subjectID, gradingFactor) VALUES (1, 1, 0.1);
INSERT INTO modules_in_subjects(moduleID, subjectID, gradingFactor) VALUES (2, 1, 0.2);

INSERT INTO grades(username, moduleID, grade) VALUES ('Justus',  0, 1.0);
INSERT INTO grades(username, moduleID, grade) VALUES ('Justus',  1, 2.7);
INSERT INTO grades(username, moduleID, grade) VALUES ('Justus',  2, 4.0);
INSERT INTO grades(username, moduleID, grade) VALUES ('Jonas',  1, 2.3);
INSERT INTO grades(username, moduleID, grade) VALUES ('Jonas',  2, 3.7);

INSERT INTO enrolled(username, subjectID) VALUES ('Justus', 0);
INSERT INTO enrolled(username, subjectID) VALUES ('Jonas', 1);
