workspace=$1
project=$2
url=$3

echo "---------参数workspace:    $workspace"
echo "---------参数project:    $project"
echo "---------参数url:    $url"
cd $workspace

#!/usr/bin/env bash
scriptDir=$(cd $(dirname $0); pwd)
echo "-----当前目录： ${scriptDir}";

if [ -d "$project" ]; then
	echo "---------执行:   cd $project"
	cd $project
	git fetch --all
  git pull --all
else
	echo "---------执行:   git clone $url"
	git clone $url
fi




