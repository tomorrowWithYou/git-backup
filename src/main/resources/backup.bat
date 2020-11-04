set workspace=%1%
set project=%2%
set url=%3%

cd %workspace%

if exist %project% (
    echo "into----"
	cd %project%
) else (
    echo "start clone---"
    git clone %url%
    cd %project%
)
echo "update code----"
git fetch --all
git pull --all

echo bat exec end

