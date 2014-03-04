CREATE PROCEDURE clearDatabase()
BEGIN 
	DELETE FROM models; 
	DELETE FROM feature_types;
	DELETE FROM performance_types;
	DELETE FROM selector_types;
END;