cd /Users/mschijf/PriveSources/raspberry
mvn clean package 
ssh pi rm raspberry*.jar
sftp pi <<< $'mput target/raspberry*.jar'
ssh pi ./start.sh

