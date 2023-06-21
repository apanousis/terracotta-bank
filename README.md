# terracotta-bank
A darned-vulnerable Java web application - For educating on and practicing secure Java coding techniques

### First Run
1. Use https://sdkman.io to install Java 8 `sdk install java 8.0.372-amzn`
2. Edit `/etc/hosts`, add the following: 
```
127.0.0.1 terracotta-bank
127.0.0.1 owasp-zap
127.0.0.1 selenium-hub
```
3. Run `./mvnw package`
4. You're done!

### SAST @ SonarQube
1. Start Sonarqube 8.9 since latest versions do not support Java 8.

```
docker run --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:8.9-community
```

2. Access SonarQube at http://localhost:9000/ with `admin:admin` 
3. Go to `Projects > Manually` and add the following entries:
```
Project key: terracotta-bank
```
4. Press `Set Up`
```
Generate a token: terracotta-bank-token
```
4. Press `Continue`
5. Select `Maven`
6. Copy the command and add it to your CLI (replace `mvn` with `./mvnw`)
7. Navigate to the project and review the vulnerabilities!

### Run Integration Tests
Just type:
```
./mvnw integration-test -Pintegration-test
```
This command will run the following as docker containers:
1. Selenium Grid at http://localhost:4444
2. Owasp Zap at http://localhost:8090
3. Chrome and Firefox nodes attached to Selenium Grid
4. Terracotta Bank at http://localhost:8080

Go to http://localhost:4444 `> Sessions` and attach to the session to see all the action! Use password `secret`.

### Run Performance Tests

```
./mvnw io.gatling:gatling-maven-plugin:test -Pperf-test
```
Open [dependency-check-report.html](target%2Fdependency-check-report.html) to review the vulnerabilities.

### Run Mutation Tests
```
./mvnw test-compile org.pitest:pitest-maven:mutationCoverage
```
Open [index.html](target%2Fpit-reports%2Findex.html) to view the report.

### Hack the bank

Navigate to http://localhost:8080 and try exploring any vulnerabilities! Enjoy!
