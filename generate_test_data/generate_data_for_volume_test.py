import os
import datetime
import subprocess
import argparse
from uuid_extensions import uuid7, uuid7str
import base64
import random
import csv
import gzip
import string
from enum import Enum

def quoted(value):
    return '"' + value + '"'

def randomBoolean():
    return quoted("true" if random.choice([True, False]) else "false")

def randomFMStatus():
    return "DONE" if random.choice([True, False]) else "CANCELED"

def randomString(length):
    return ''.join(random.choices(string.ascii_uppercase + string.digits, k=length))

def randomNumericString(length):
    return ''.join(random.choices(string.digits, k=length))

dirpath = os.path.join(os.path.dirname(os.path.realpath(__file__)), "csv", "ydb")
os.makedirs(dirpath, exist_ok = True)

fixed_date = datetime.datetime.now(datetime.timezone.utc)
formatted_date = fixed_date.strftime('%Y-%m-%dT%H:%M:%S.%f')[:-3] + 'Z'

# чанки для оптимизации записи блоками
chunk_size = 5000

for i in range(0, 100):
    with open(os.path.join(dirpath, f't_balance_{i}.csv'), "w", buffering=4*1024*1024) as balanceFile:
        with open(os.path.join(dirpath, f't_remainsapp_funds_movement_{i}.csv'), "w", buffering=4*1024*1024) as remainsFMFile:
            balance_buf = []
            remains_buf = []
            append_b = balance_buf.append
            append_r = remains_buf.append
            for j in range(0, 10000):
                accid = uuid7str()
                append_b(f'{quoted(accid)},"CUSCOR",{randomNumericString(20)},{formatted_date}\n')
                docid = uuid7str()
                FMStatus = randomFMStatus()
                amount = random.randint(1, 1000000)
                append_r(f'{quoted(docid)},1,{FMStatus},"FUNDS_DIRECTION_DEBIT",{quoted(accid)},{amount},{formatted_date},{formatted_date}\n')
                append_r(f'{quoted(docid)},2,{FMStatus},"FUNDS_DIRECTION_CREDIT",{quoted(accid)},{amount},{formatted_date},{formatted_date}\n')
                if (j + 1) % chunk_size == 0:
                    balanceFile.write(''.join(balance_buf))
                    remainsFMFile.write(''.join(remains_buf))
                    balance_buf.clear()
                    remains_buf.clear()
            # финальная догрузка
            if balance_buf: balanceFile.write(''.join(balance_buf))
            if remains_buf: remainsFMFile.write(''.join(remains_buf))