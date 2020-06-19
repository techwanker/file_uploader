init() {
	git init
}

remote_add() {
	 git remote add origin git@github.com:javautil/javautil.org.git
}

add_workspace() {
	
  git add javautil-addressvalidation
  git add javautil-apex
  git add javautil-commandline
  git add javautil-conditionidentification
  git add javautil-core
  git add javautil-dblogging
  git add javautil-dbunit
  git add javautil-diff-match
  git add javautil-hibernate
  git add javautil-hibernatesalesdata
  git add javautil-jdbc
  git add javautil-jfm
  git add javautil-mp3
  git add javautil-oracle
  git add javautil-poi
  git add javautil-proxy
  git add javautil-test
  git add javautil-web
  git add norepo
}

#So running git commit will do nothing unless you explicitly add files to the commit with git add. 
#If you're looking for the commit command to automatically commit local modifications we use the -a flag.
#git commit -a -m "Changed some files"
local_commit() {
	git commit -a
}

push() {
	 git push -u origin master
}

#init
add_workspace
local_commit
push

