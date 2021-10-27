java -version
cd common-utils
echo COMMON-UTILS
mvn -Dmaven.test.skip -Dhttps.protocols=TLSv1.2 -Djavax.net.ssl.trustStore=/home/u91856/app/jdk1.7.0_75/jre/lib/security/cacerts clean install -U
cd ..
cd ws-base
echo WS-BASE
mvn -Dmaven.test.skip -Dhttps.protocols=TLSv1.2 -Djavax.net.ssl.trustStore=/home/u91856/app/jdk1.7.0_75/jre/lib/security/cacerts clean install -U
cd ..
cd gdib-amp
echo GDIB-AMP
mvn -Dmaven.test.skip -Dhttps.protocols=TLSv1.2 -Djavax.net.ssl.trustStore=/home/u91856/app/jdk1.7.0_75/jre/lib/security/cacerts clean install -U
cd ..
cd gdib-share
echo GDIB-SHARE
mvn -Dmaven.test.skip -Dhttps.protocols=TLSv1.2 -Djavax.net.ssl.trustStore=/home/u91856/app/jdk1.7.0_75/jre/lib/security/cacerts clean install -U
cd ..
cd invoices
echo INVOICES
mvn -Dmaven.test.skip -Dhttps.protocols=TLSv1.2 -Djavax.net.ssl.trustStore=/home/u91856/app/jdk1.7.0_75/jre/lib/security/cacerts clean install -U
cd ..
rm gdib.zip
find . -name *.amp -exec zip -j gdib.zip {} \;
