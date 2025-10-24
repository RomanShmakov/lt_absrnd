import os
import traceback

import urllib3
import math

import yaml

import grafana_snapshots_new.grafana as Grafana
from grafana_snapshots_new.grafanaData import GrafanaData

# Отключает предупреждения о небезопасном SSL-соединении
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

# config
from constants import (CONFIG_NAME)

# config
base_path = '.'
config_file = os.path.join(base_path, CONFIG_NAME)
config = yaml.safe_load(open(config_file, 'r'))


def get_content_from_panel(panel_name, time_from, time_to):
    # dashboard
    dashboard_name = config["dashboard"]
    application = config["application"]
    transaction = config["transaction"]

    # grafana
    host = config["host"]
    protocol = config["protocol"]
    port = config["port"]
    token = config["token"]
    verify_ssl = config["verify_ssl"]
    search_api_limit = config["search_api_limit"]
    folder = config["folder_dashboard"]

    datasources = {}
    context = {'vars': {}}

    params = {
        'host': host,
        'protocol': protocol,
        'port': port,
        'token': token,
        'verify_ssl': verify_ssl,
        'search_api_limit': search_api_limit,
        'folder': folder,
    }

    try:
        grafana_api = Grafana.Grafana(**params)
    except Exception as e:
        print("can't init grafana api: message: {}".format(e))

    context['vars'].update({
        'data_source': "VictoriaMetrics",
        'application': application,
        'transaction': transaction
    })

    try:
        dashboard = grafana_api.export_dashboard(dashboard_name)
    except Grafana.GrafanaDashboardNotFoundError:
        print("dashboard name not found '{0}'".format(dashboard_name))
    except Exception as exp:
        print("dashboard '{0}' export exception '{1}'".format(dashboard_name, traceback.format_exc()))

    try:
        datasources = grafana_api.get_datasources()
    except Exception as e:
        print("excepton during grafana datasource retrive error: {} - message: {}".format(e.status_code, e.message))

    print("datasources OK.")

    context['url'] = dashboard['meta']['url']

    params = {
        'api': grafana_api,
        'dashboard': dashboard['dashboard'],
        'datasources': datasources,
        'time_to': time_to,
        'time_from': time_from,
        'context': context,
    }

    # **********************************************************************************
    # *** collect the data from datasources to populate the snapshot
    data_api = GrafanaData(**params)
    try:
        res = data_api.get_dashboard_data(panel_name)
    except Exception as exp:
        print("dashboard '{0}' export exception '{1}'".format(dashboard_name, traceback.format_exc()))
        res = None
    if res is None or not res:
        print("can't obtain dashboard data... snapshot canceled!")

    return res
