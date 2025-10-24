export basedir=`dirname "$(realpath $0)"`

mkdir -p "$basedir/json_$1"
mkdir -p "$basedir/csv_$1"

$basedir/vm_scripts/vm_get_json.sh $1 $2

python3 $basedir/vm_scripts/vm_get_csv.py --start_time $1