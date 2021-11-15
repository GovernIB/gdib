#! /bin/bash
mvn -Dmaven.test.skip=true clean install
find . -name *.amp -exec scp {} alfresco@sdesalflin9.caib.es:/opt/alfresco-one-62/a_desplegar \;
#ssh alfresco@sdesalflin9.caib.es /opt/alfresco-one-62/desplega.sh
