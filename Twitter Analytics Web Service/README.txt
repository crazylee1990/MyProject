The folder contains all codes and report for 15619 Project Phase 3
1. ETL Folder:
	this folder contains all codes during the process and contains three subfolder
		(1) Extraction: contains two mappers and two reducers which run in EMR, just to parse the JSON file and generate the original file to load into databses
		(2) MySQL: contains code which will load data from step (1) into MySQL
		(3) HBase: contains codes which will load data into HBase
2. Front End:
	contains three java files which will handle 4 kind of queries in Phase 3;

3. Report:
	regarding how we made it during this phase