# dclab-grants
Code for grant information extraction
Ths projoect is part of a requirement for publishers to link grant information from within journals to identified grant agencies.  The main piece of code is a tokenizer using the JFLEX library (java).  This code takes a stream of text and given text cues that signifiy that a funding information is present locates grant-organizations and  grant-id .  The stream of tokens is used to verify grant-agency against an RDF derived sql table taken from Fundref.org.

Calling the code:    The GrantInformation object is called with four parameters
Writer   -     sink for writing the resultant xml
content (String) Text of the grant section where funding information is being searched
FundRefSearch - obj   FundRefSearch is a brigde between the database (mysql) and the GrantInformation
displayNonregistered (boolean) yes to show grant-agencies that are not within the fundref database
debug (boolean)    -   show match scores within the resultant xml
