CREATE TABLE BOOK(
	BOOK_ID 	INT PRIMARY KEY AUTO_INCREMENT,
	TITLE		VARCHAR(80) UNIQUE,
	AUTHOR		VARCHAR(80)
);