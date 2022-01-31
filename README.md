# microserver
https://www.intelyclick.com/microserver/


I'ts a small server REST-API made in JAVA that call a query or function in POSTGRESQL or MYSQL and show the JSON.







HOW INSTALL
- Create schema 'microrest' in postgresql database
- or table microrest_restapi in Mysql
- Create table  'restapi' see directory sql
- copy MicroServer.jar of dir 'build_jar'
- run java jar : java -jar MicroServer <path configuration file>

CONFIGURE (Config File)
- you can configure the port by changing the 'port' in config file
- Time Out
- URL of you database connection , see JDBC documentation 
- User database
- Password database  
  
- SSL Support
To view examples ,please visit
https://www.intelyclick.com/como-anadir-soporte-ssl-para-microrest
  
  
RUN MicroServer and by params put the location of config file.
  
  
THATS ALL!!!

Enjoy.
