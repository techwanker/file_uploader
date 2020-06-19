#git init
git remote set-url origin git@bitbucket.org:BocasThunder/workspace-javautil
pushd javautil-pom
mvn clean
popd
#echo "Jim Schmidt" >> contributors.txt
echo about to add
git add .
git commit -m 'As of 2016-11-08 '
git config --global core.excludesfile ~/.gitignore
#git push -u origin master
