

## Install jnetpcap local repo

```
mvn install:install-file -Dfile=jnetpcap.jar -DgroupId=org.jnetpcap -DartifactId=jnetpcap -Dversion=1.4.1 -Dpackaging=jar
```

## Run
### IntelliJ IDEA
at the project root directory
```
$ sudo bash
$ gradle execute
```
### Eclipse
VM parameter
```
-Djava.library.path="pathtoproject/jnetpcap/linux/jnetpcap-1.4.r1425"
```

## Make package
at the project root directory
```
$ gradle distZip
```
the zip file will be in the pathtoproject/CICFlowMeter/build/distributions
