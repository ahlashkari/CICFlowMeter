

## Install jnetpcap local repo


for linux, sudo is a prerequisite
```
//linux :at the pathtoproject/jnetpcap/linux/jnetpcap-1.4.r1425
//windows: at the pathtoproject/jnetpcap/win/jnetpcap-1.4.r1425
mvn install:install-file -Dfile=jnetpcap.jar -DgroupId=org.jnetpcap -DartifactId=jnetpcap -Dversion=1.4.1 -Dpackaging=jar
```

## Run
### IntelliJ IDEA
open a Terminal in the IDE
```
//linux:
$ sudo bash
$ gradle execute

//windows:
$ gradlew execute
```
### Eclipse

Run eclipse with sudo
```
1.first time
right click App.java -> Run As -> Run Configurations -> Arguments -> VM arguments:
-Djava.library.path="pathtoproject/jnetpcap/linux/jnetpcap-1.4.r1425"  -> Run

2.right click App.java -> Run As -> Java Application

```

## Make package

### IntelliJ IDEA
open a Terminal in the IDE
```
//linux:
$ gradle distZip

//window
$ gradlew distZip
```
the zip file will be in the pathtoproject/CICFlowMeter/build/distributions

### Eclipse
at the project root
```
mvn package
```
the jar file will be in the pathtoproject/CICFlowMeter/target
