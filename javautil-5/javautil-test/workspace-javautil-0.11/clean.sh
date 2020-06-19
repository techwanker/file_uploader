for DIR in *
do
   pushd $DIR
   echo $DIR
   maven clean
   popd
done 
