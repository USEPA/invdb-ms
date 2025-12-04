# invdb-ms

## Local setup
- Open 'Git Bash' terminal
- Run command './gradlew bootrun'

## SAIC certificate issue resolve
- We need to import saic cert to Java's security folder to resolve this SSLHandshake issue. 
- CMD to Java bin location and run below command. Make sure your JAVA_HOME is set.
- keytool -import -trustcacerts -alias SAIC_Trusted_Root -file C:/dev/certificates/SAIC_Private_Root_CA_2016_01.cer -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -v

