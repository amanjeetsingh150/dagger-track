function plot_dependencies() {
   package=$1
   activity_component=$2
   adb logcat -c
   adb shell am force-stop "$package"
   adb shell am start -W -n "$activity_component"
   adb logcat -d DaggerTrack > injection_time.txt
   python3 plot_dagger_track_logs.py --logs ./injection_time.txt
   rm injection_time.txt
}

function usage() {
    echo "Usage: $ [-p [package_name] -c [activity_component]]"; exit 1;
}

while getopts ":p:c:h:" opt; do
  case "$opt" in
    p) package_name=$OPTARG ;;
    c) component=$OPTARG ;;
    h) usage ;;
    *) usage;;
  esac
done

plot_dependencies "$package_name" "$component"

