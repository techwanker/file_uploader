MAVEN=/common/tools/java/apache-maven-3.0/bin/mvn
maven() {
	$MAVEN  -DdownloadSources=true -DdownloadJavadocs=true $*
}

install-no-repo() {
	pushd norepo
	./mvn-install-artifacts
	popd
}

build-diff-match() {
	pushd diff-match
	maven install
	popd
}

build-jaudiotagger() {
	pushd jaudiotagger
	maven install
	popd
}

build-javautil() {
	pushd javautil-pom
	maven install
	popd
}
build-proxy() {
	pushd javautil-proxy
	maven install
	popd
}

build-web() {
	pushd javautil-web
	maven install
	popd
}

build-all() {
	install-no-repo
	build-diff-match
	build-jaudiotagger
	build-proxy
	build-web
	build-javautil
}

build-all
