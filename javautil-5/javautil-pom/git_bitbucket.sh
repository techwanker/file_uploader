git init
git remote add origin git@bitbucket.org:BocasThunder/javautil-pom
git remote set-url origin git@bitbucket.org:BocasThunder/javautil-pom
echo "Jim Schmidt" >> contributors.txt
git add .
git commit
git push -u origin master
