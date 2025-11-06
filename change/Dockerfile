FROM wildfly:latest

# Copier le WAR généré
COPY target/change.war /opt/jboss/wildfly/standalone/deployments/

# Exposer les ports
# 8180 pour HTTP (au lieu de 8080 pour éviter conflit avec WildFly local)
# 9991 pour Management (au lieu de 9990 pour éviter conflit avec WildFly local)
EXPOSE 8180 9991

# Démarrer WildFly avec les ports personnalisés et permettre l'accès externe
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0","-Djboss.http.port=8180", "-Djboss.management.http.port=9991"]