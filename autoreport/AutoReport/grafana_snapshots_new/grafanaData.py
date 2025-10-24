# -*- coding: utf-8 -*-
# ***********************************************************************************************
import copy, json, re
import dateutil.parser, dateutil.relativedelta, time
from datetime import datetime, timezone
from urllib import request
from jinja2 import Template

from grafana_client.knowledge import query_factory

from grafana_snapshots.dataresults.dataresults import dataresults


class GrafanaData(object):
    # prometheus query change in v 8
    # version_8 = LooseVersion('8')
    varfinder = re.compile(r'(?:\$([a-zA-Z0-9_]+))|(?:\${([a-zA-Z0-9_]+)})')

    # ***********************************************
    def __init__(*args, **kwargs):
        self = args[0]

        self.api = kwargs.get('api')
        if self.api is None:
            raise Exception('api not set')
        self.dashboard = kwargs.get('dashboard')
        datasources = kwargs.get('datasources', None)
        if datasources is None:
            self.datasources = self.api.get_datasources()
        else:
            self.datasources = datasources

        self.time_to = self.get_time(kwargs.get('time_to'))
        self.time_from = self.get_time(kwargs.get('time_from'))
        self.step = self.get_step_ms(self.time_from, self.time_to)

        self.context = kwargs.get('context')

    # ***********************************************
    def get_dashboard_data(self, panel_name):

        content = None

        panel_url = None
        if self.context is not None and 'url' in self.context:
            panel_url = self.context['url']
        if panel_url is None:
            panel_url = 'xXx'

        self.context['time_from'] = self.time_from
        self.context['time_to'] = self.time_to

        snapshotData = None
        res_status = False

        if 'panels' in self.dashboard:
            for panel in self.dashboard['panels']:
                # ** row panel...
                if 'type' in panel and panel['type'] in ('row', 'text'):
                    print("target-type is '{0}': skipped".format(panel['type']))
                    continue

                print("panel: {}".format(panel))

                dtsrc = 'default'
                target = None
                if 'datasource' in panel and panel['datasource'] is not None:
                    dtsrc = panel['datasource']

                    print("dtsrc: %s" % (dtsrc))
                    if isinstance(dtsrc, dict) and 'uid' in dtsrc and dtsrc['uid'] == '-- Mixed --':
                        dtsrc = '-- Mixed --'

                if 'targets' in panel and panel['targets'] is not None:
                    targets = panel['targets']

                    print("target: %s" % (targets))

                datasource = self.get_datasource(dtsrc)
                if (datasource is not None or dtsrc == '-- Mixed --') and targets is not None:

                    # print('dt: {0}'.format(datasources[dtsrc]))
                    request = None
                    for target in targets:
                        if dtsrc == '-- Mixed --' and 'datasource' in target:
                            datasource = self.get_datasource(target['datasource'])
                            if datasource is not None:
                                datasource_name = datasource['name']
                            else:
                                datasource_name = target['datasource']
                        else:
                            datasource_name = dtsrc

                        if not datasource:
                            print("datasource '{0}' was not found".format(datasource_name))
                            continue
                        request = self.get_query_from_datasource(datasource, target, request=request)
                        if request is None:
                            print("query type '{0}' not supported".format(datasource['type']))
                            continue

                        print("query datasource proxy uri: {0}".format(request))
                    if request is not None and panel['title'] == panel_name:
                        try:
                            content = self.api.smartquery(datasource, request)
                            break
                        except Exception as e:
                            raise Exception('invalid results...: {}'.format(e))

        if content is None:
            print(f"vars content not found by {panel_name}")

        return content

    # **********************************************************************************
    def get_time(self, time_str: str) -> int:

        now = datetime.now()
        ts = None

        if time_str is None:
            time_str = now

        if type(time_str) is datetime:
            ts = int(time_str.timestamp())
        elif type(time_str) is int:
            # it must be an unix timestamp
            ts = time_str
        elif type(time_str) is str:
            p = re.compile(r'^now(?:-(\d+)([dhmMyw]))?$')
            m = p.search(time_str)
            if m:
                factor = 0
                ts = now
                if m.group(1) and m.group(2):
                    val = int(m.group(1))
                    if m.group(2) == 'm':
                        dt = dateutil.relativedelta.relativedelta(minutes=val)
                    elif m.group(2) == 'h':
                        dt = dateutil.relativedelta.relativedelta(hours=val)
                    elif m.group(2) == 'd':
                        dt = dateutil.relativedelta.relativedelta(days=val)
                    elif m.group(2) == 'w':
                        dt = dateutil.relativedelta.relativedelta(weeks=val)
                    elif m.group(2) == 'M':
                        dt = dateutil.relativedelta.relativedelta(months=val)
                    elif m.group(2) == 'y':
                        dt = dateutil.relativedelta.relativedelta(years=val)
                    ts = ts - dt
                ts = int(ts.timestamp())
            else:
                time_str = dateutil.parser.parse(time_str)
                ts = int(time_str.timestamp())
        else:
            time_str = now
            ts = int(time_str.timestamp())

        print("get_time('{0}') = {1}".format(time_str, ts))

        return ts

    # **********************************************************************************
    def get_step_ms(self, ts_from: int, ts_to: int) -> int:
        """
        build a step in milli seconds based on from/to interval and grafana's steps (collected manually)
        """
        # compute step value
        # last 5 min: step 15 (300 : 15: 20 )
        # last 15 min: step 15 (900 : 15: 60 )
        # last 1 hour: step 15 (3600 : 15: 240 )
        # last 3 hours: step 15 (3*3600 : 15: 720 )
        # last 6 hours: step 15 (6*3600 : 15: 1440 )
        # last 12 hours: step 30 (12*3600 : 30: 1440 )
        # last 1 days => step = 120 : 720
        # last 2 days => step = 300 : 576
        # last 7 days => step = 900 : 672
        # last 30 days => step = 1800 :
        # last 45 days => step = 3600 :
        # last 90 days => step = 7200 :
        # last 6 months => step = 10800 :
        # last 1 year => step = 21600 :
        # last 2 years => step = 43200 :
        # last 5 years => step = 86400 :
        steps = [15, 20, 30, 120, 300, 900, 1800, 3600, 7200, 10800, 21600, 43200, 864000]
        MAX_RESOLUTION = 2500
        #    STEP_INTERVAL = 15
        # MAX_RESOLUTION_POINT = 11000
        # * maximum resolution of 11,000 points
        # actual screen size are max 3000 pixel, so it is useless to have more than this value of data,
        # meaning one value for each pixel
        delta = int(ts_to - ts_from)
        #    print( 'delta={0}'.format(delta) )
        # notsimilar to grafana behavior
        #    step = STEP_INTERVAL * (int(int(ts_to - ts_from) / ( MAX_RESOLUTION_POINT * STEP_INTERVAL)) + 1)
        #    step = STEP_INTERVAL * (int(delta) / ( MAX_RESOLUTION_POINT * STEP_INTERVAL)) + 1)
        for step in steps:
            if int(delta / step) < MAX_RESOLUTION:
                break

        #    print('step={0}'.format(step))
        return step * 1000

    # ***********************************************
    def get_datasource(self, datasource) -> dict:
        res_datasource = None

        if (isinstance(datasource, str) and datasource != '-- Mixed --') \
                or isinstance(datasource, dict):

            # datasource set in panel is in new format { 'uid': ..., 'type':... }
            if isinstance(datasource, dict):
                if 'uid' in datasource and datasource['uid'] in self.datasources:
                    res_datasource = self.datasources[datasource['uid']]
            # datasource is in old format: str; so have to find name in datasource list
            else:
                if datasource == "default" and "default" in self.datasources:
                    res_datasource = self.datasources["default"]
                else:
                    for _, source in self.datasources.items():
                        if source['name'] == datasource:
                            res_datasource = source
                            break

        return res_datasource

    # ***********************************************
    def get_query_from_datasource(self, datasource, target, request=None, panel=None):

        if 'type' not in datasource:
            return None

        expr = None
        var_type = None

        if datasource['type'] in ('loki', 'prometheus'):
            # check query method: timeseries or table
            # query_type = 'query_range'
            # format = 'time_series'
            # if 'format' in target and target['format'] == 'table':
            #    format = target['format']
            #    query_type = 'query'

            # check if expr is defined or and unconfigurated query
            if 'expr' in target:
                expr = target['expr']

        elif datasource['type'] in ('mssql', 'mysql', 'oracle', 'postgres'):
            if 'rawSql' in target:
                expr = target['rawSql']
        elif datasource['type'] == 'graphite':
            expr = target['target']
            var_type = 'graphite'

        # influxdb,
        elif datasource['type'] == 'influxdb':
            if 'query' in target:
                expr = target['query']
            # add variables for grafana internal substitution
            # $__interval, $timeFilter
            interval = '10m'
            if panel is not None and 'interval' in panel:
                m = re.match(r'^[<>=]+(.+)', panel['interval'])
                if m is not None:
                    interval = m.group(1)

            self.context['vars'].update({
                # *** WARNING
                '__interval': interval,
                'timeFilter': "time >= '{}' AND time <= '{}'".format(
                    datetime.fromtimestamp(self.time_from, timezone.utc).isoformat(),
                    datetime.fromtimestamp(self.time_to, timezone.utc).isoformat(),
                )
            })
        else:
            if 'query' in target:
                expr = target['query']

        new_req = None
        if expr is not None:
            # check if target expr contains variable ($var)
            m = GrafanaData.varfinder.search(expr)
            if m:
                expr = self.extract_vars(expr, type=var_type)

            params = copy.deepcopy(target)
            params['utcOffsetSec'] = self.get_offsetFromUTC()
            params['query'] = expr
            params['time_to'] = self.time_to
            params['time_from'] = self.time_from
            params['intervalMS'] = self.step

            new_req = query_factory(datasource, params)
        elif "datasource" in target and target["datasource"]['type'] == '__expr__':
            params = copy.deepcopy(target)
            params["window"] = ""
            if request is not None:
                request["data"]["queries"].append(params)

        if request is None:
            request = new_req
        elif new_req is not None:
            request["data"]["queries"].append(*new_req["data"]["queries"])
        return request

    # ***********************************************
    def get_offsetFromUTC(self) -> int:
        # get localtime with timezone
        # timezone contains deltatime from utc: convert to second.micro then to int
        return int(datetime.now().astimezone().utcoffset().total_seconds())

    # **********************************************************************************
    def extract_vars(self, expr: str, type: str = 'regexpr') -> str:

        vl = list()
        # ** collect all var_name from expression
        for m in re.finditer(GrafanaData.varfinder, expr):
            var = m.group(1)
            # variable is in normal format : $var
            if var is not None:
                format = 'raw'
            else:
                format = 'encapsulated'

                var = m.group(2)

            # ** check if the context (user args) provides a value for thee variable
            # ** else use the  current value from dashboard templating list
            if var not in self.context['vars']:
                self.context['vars'][var] = self.get_var_value_from_dashboard(var)

            print("found variable ${0} => \"{1}\"".format(var, self.context['vars'][var]))
            vl.append({'name': var, 'format': format, })

        # ** replace all variables name with values in expr
        for var in vl:
            val = self.context['vars'][var['name']]
            if isinstance(val, str):
                if val == '$__all':
                    val = '.*'
            elif isinstance(val, list):
                if type == 'regexpr':
                    val = '(' + '|'.join(val) + ')'
                elif type == 'graphite':
                    val = '{' + ','.join(val) + '}'

            if var['format'] == 'raw':
                expr = expr.replace('$' + var['name'], val)
            elif var['format'] == 'encapsulated':
                expr = re.sub('\${\s*' + var['name'] + '\s*\}', val, expr, flags=re.MULTILINE)

        print("extract_vars::result expr=\"{0}\"".format(expr))
        return expr

    # **********************************************************************************
    def get_var_value_from_dashboard(self, var_name):
        value = '$' + var_name
        for tpl_list in self.dashboard['templating']['list']:
            if 'name' in tpl_list:
                print("get_var_value_from_dashboard::check name {0}".format(tpl_list['name']))
            if 'name' not in tpl_list or tpl_list['name'] != var_name:
                continue
            if 'value' in tpl_list['current']:
                cur_val = tpl_list['current']['value']
                if isinstance(cur_val, list) and len(cur_val) > 0:
                    value = cur_val[0]
                else:
                    value = cur_val
                break
        print("get_var_value_from_dashboard::value {0}".format(value))
        return value
