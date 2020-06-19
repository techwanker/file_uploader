set -x 
set -e 
git init
git config --global user.name "Jim Schmidt"
git config --global user.email james.joseph.schmidt@gmail.com
set +e
#git remote add origin //git@bitbucket.org/pacific-data-services/javautil-0.11
git remote add origin https://github.com/techwanker/javautil-0.11.git
set -e
#pushd javautil-pom
#mvn clean install
#popd
git add -A * 
git commit
git push -u origin master

