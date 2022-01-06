# microserver
https://www.intelyclick.com/microserver/


I'ts a small server REST-API made in JAVA than call query or function in POSTGRESQL and show the JSON.


HOW INSTALL
- Create schema 'microrest' in postgresql database
- Create table  'restapi' see directory sql
- copy MicroServer.jar of dir 'build_jar'
- run java jar : java -jar MicroServer <path configuration file>

CONFIGURE
- you can configure the port by changing the row 'port' in table config
- Time Out
- URL of you database connection , see JDBC documentation 
- User database
- Password database  
  
THATS ALL!!!

Enjoy.
