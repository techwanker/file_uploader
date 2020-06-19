for f in `find . -name "pom.xml"` 
do    
        sed -i "s/0.11.6/VER-0.11.7/" $f
done
