export basedir=`dirname "$(realpath $0)"`/..
export basedir=`realpath $basedir`


PROM_URL="http://kube-node1.moscow.alfaintra.net:30090"
START_TIME="$1"
END_TIME="$2"
STEP="15s"

# Выполнить запрос, показать ошибки curl при неудаче

# 1 # RPS by label
QUERY='sum(rate(jmeter_success_ratio_total[15s])) by (label)'
OUT_JSON="$basedir/json_$1/RPS_by_label.json"
curl -sS -G "${PROM_URL}/api/v1/query_range" \
  --data-urlencode "query=${QUERY}" \
  --data-urlencode "start=${START_TIME}" \
  --data-urlencode "end=${END_TIME}" \
  --data-urlencode "step=${STEP}" \
  -o "${OUT_JSON}"

# 2 # Pct response times
QUERY='avg(jmeter_rt_summary{code="200",quantile="0.95"}) by (quantile)'
OUT_JSON="$basedir/json_$1/Pct_response_times.json"
curl -sS -G "${PROM_URL}/api/v1/query_range" \
  --data-urlencode "query=${QUERY}" \
  --data-urlencode "start=${START_TIME}" \
  --data-urlencode "end=${END_TIME}" \
  --data-urlencode "step=${STEP}" \
  -o "${OUT_JSON}"

# 3 # Virtual Users
QUERY='sum(jmeter_threads{state="active"})'
OUT_JSON="$basedir/json_$1/Virtual_Users.json"
curl -sS -G "${PROM_URL}/api/v1/query_range" \
  --data-urlencode "query=${QUERY}" \
  --data-urlencode "start=${START_TIME}" \
  --data-urlencode "end=${END_TIME}" \
  --data-urlencode "step=${STEP}" \
  -o "${OUT_JSON}"

# 4 # RPS error
QUERY='sum(rate(jmeter_success_ratio_failure[15s])) by (label)'
OUT_JSON="$basedir/json_$1/RPS_error.json"
curl -sS -G "${PROM_URL}/api/v1/query_range" \
  --data-urlencode "query=${QUERY}" \
  --data-urlencode "start=${START_TIME}" \
  --data-urlencode "end=${END_TIME}" \
  --data-urlencode "step=${STEP}" \
  -o "${OUT_JSON}"

# 5 # RPS Error by message
QUERY='sum by (failed_message) (rate(jmeter_response_message_error{failed_message!="null"}[15s]))'
OUT_JSON="$basedir/json_$1/RPS_Error_by_message.json"
curl -sS -G "${PROM_URL}/api/v1/query_range" \
  --data-urlencode "query=${QUERY}" \
  --data-urlencode "start=${START_TIME}" \
  --data-urlencode "end=${END_TIME}" \
  --data-urlencode "step=${STEP}" \
  -o "${OUT_JSON}"
