DROP DATABASE gradecalculator;
CREATE DATABASE gradecalculator;
USE gradecalculator;
CREATE TABLE modules(moduleID INT NOT NULL PRIMARY KEY, modulename TEXT);
CREATE TABLE subjects(subjectID INT NOT NULL PRIMARY KEY, subjectname TEXT);
CREATE TABLE users(username VARCHAR(100) NOT NULL PRIMARY KEY, password TEXT);
CREATE TABLE modules_in_subjects(
	moduleID INT NOT NULL,
	subjectID INT NOT NULL,
	gradingFactor DOUBLE(10,9),
	FOREIGN KEY (moduleID) REFERENCES modules (moduleID),
	FOREIGN KEY (subjectID) REFERENCES subjects (subjectID)
);
CREATE TABLE grades(
	username VARCHAR(100) NOT NULL,
	moduleID INT NOT NULL,
	grade DOUBLE(2,1),
	FOREIGN KEY (username) REFERENCES users (username),
	FOREIGN KEY (moduleID) REFERENCES modules (moduleID)
);
CREATE TABLE enrolled(
	username VARCHAR(100) NOT NULL,
	subjectID INT NOT NULL,
	FOREIGN KEY (username) REFERENCES users (username),
	FOREIGN KEY (subjectID) REFERENCES subjects (subjectID)
);
