
set -e   # script should terminate on an error
set -o nounset # blowup if referencing variable not initialized
set -o verbose # same as -v echo commands
. ./build-javautil.properties
#
# Run the ant tasks which compile the Hibernate Reverse Engineering Strategy
# classes generates Hibernate pojos and mapping files
#
# The maven plugin at the time of this writing was too quirky to use
#
run_ant() {
	for DIR in javautil-* 
	do
		pushd $DIR
		echo building $DIR
		if [ -f build.xml ] ; then
			ant
			if [ $? -ne 0 ] ; then
			   echo "Error in build " $DIR
			   exit $?
                        fi
		fi
		popd
	done
}

install_artifacts_without_repositories() {
	echo need to write $0
} 

download_and_install_apex() {
	echo need to write $0
}

unlock_user_hr() {
	echo need to write $0
}

run_maven3() {
	pushd javautil-pom
	$MVN -Dlog4j.configuration=$LOG4J_FILE \
		-DdownloadSources=true  \
		-DdownloadJavadocs=true $* 2>&1 | tee maven.log
} 	

# Experimental not even alpha yet
render_books() {
	maven doxia:render-books
}


run_ant

run_maven3 clean site





