export basedir=`dirname "$(realpath $0)"`/..
export basedir=`realpath $basedir`


PROM_URL="http://monitor.dev.moscow.alfaintra.net:8428"
START_TIME="$1"
END_TIME="$2"
STEP="15s"
APP_JMETER="uc_4_1_linux.jmx"
TRANSACTION_JMETER="create_request"

# Выполнить запрос, показать ошибки curl при неудаче

# 1 # Throughput
QUERY="jmeter_count{application=\"${APP_JMETER}\",transaction=\"all\"}/5"
OUT_JSON="$basedir/json_$1/Throughput.json"
curl -sS -G "${PROM_URL}/api/v1/query_range" \
  --data-urlencode "query=${QUERY}" \
  --data-urlencode "start=${START_TIME}" \
  --data-urlencode "end=${END_TIME}" \
  --data-urlencode "step=${STEP}" \
  -o "${OUT_JSON}"

# 2 # Response Time - Pass (95th pct)
QUERY="jmeter_pct95.0{application=\"${APP_JMETER}\",transaction=\"${TRANSACTION_JMETER}\",statut=\"ok\"}"
OUT_JSON="$basedir/json_$1/Pct_95_response_time.json"
curl -sS -G "${PROM_URL}/api/v1/query_range" \
  --data-urlencode "query=${QUERY}" \
  --data-urlencode "start=${START_TIME}" \
  --data-urlencode "end=${END_TIME}" \
  --data-urlencode "step=${STEP}" \
  -o "${OUT_JSON}"

# 3 # Virtual Users
QUERY="jmeter_maxAT{application=\"${APP_JMETER}\"}"
OUT_JSON="$basedir/json_$1/Virtual_Users.json"
curl -sS -G "${PROM_URL}/api/v1/query_range" \
  --data-urlencode "query=${QUERY}" \
  --data-urlencode "start=${START_TIME}" \
  --data-urlencode "end=${END_TIME}" \
  --data-urlencode "step=${STEP}" \
  -o "${OUT_JSON}"

# 4 # Total Error
QUERY="jmeter_countError{application=\"${APP_JMETER}\",transaction=\"all\"}"
OUT_JSON="$basedir/json_$1/Total_Error.json"
curl -sS -G "${PROM_URL}/api/v1/query_range" \
  --data-urlencode "query=${QUERY}" \
  --data-urlencode "start=${START_TIME}" \
  --data-urlencode "end=${END_TIME}" \
  --data-urlencode "step=${STEP}" \
  -o "${OUT_JSON}"

# 5 # % Error
QUERY="sum(jmeter_count{application=\"${APP_JMETER}\",transaction=~\"${TRANSACTION_JMETER}\",statut=\"ko\"})*100/sum(jmeter_count{application=\"${APP_JMETER}\",transaction=~\"${TRANSACTION_JMETER}\",statut=\"all\"})"
OUT_JSON="$basedir/json_$1/Percent_Error.json"
curl -sS -G "${PROM_URL}/api/v1/query_range" \
  --data-urlencode "query=${QUERY}" \
  --data-urlencode "start=${START_TIME}" \
  --data-urlencode "end=${END_TIME}" \
  --data-urlencode "step=${STEP}" \
  -o "${OUT_JSON}"
