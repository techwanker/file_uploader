build() {
	./build.sh
}

DIRNAME=`pwd`
PROJECT_NAME=javautil-0.11
echo $PROJECT_NAME
set -x
set -e 
#build
git init
git config --global user.name "Jim Schmidt"
git config --global user.email james.joseph.schmidt+pds@gmail.com
set +e
# This will return a non zero if remote exists
git remote add origin https://sea-ds@bitbucket.org/sea-ds/${PROJECT_NAME}
# This will return a non zero if there are ignored files
git add -A *
set +e
git commit
git push -u origin master

