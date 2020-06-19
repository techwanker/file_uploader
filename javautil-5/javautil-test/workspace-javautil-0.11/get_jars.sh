rm javautil-jars/*
ls -l javautil-jars
for JAR in javautil-*/target/*.jar 
do
   ln -s ../$JAR javautil-jars
done
