import os
import datetime
import subprocess
import argparse
from uuid_extensions import uuid7, uuid7str
import base64
import random
import string
from enum import Enum

dbtype = None

class DbType(Enum):
    ydb = 'ydb'
    postgresql = 'postgresql'

    def __str__(self):
        return self.value

def quoted(value):
    if dbtype == DbType.ydb:
        return '"' + value + '"'
    else:
        return value

def randomBoolean():
    return quoted("true" if random.choice([True, False]) else "false")

def randomString(length):
    return ''.join(random.choices(string.ascii_uppercase + string.digits, k=length))

def randomNumericString(length):
    return ''.join(random.choices(string.digits, k=length))

parser = argparse.ArgumentParser(description='Generate data', add_help=True, usage=True)
parser.add_argument(
    '--dbtype',
    type=DbType,
    help='Db type',
    choices=list(DbType)
)
parser.add_argument(
    '--accounts',
    type=int,
    help='Accounts rows number',
    default=0
)
parser.add_argument(
    '--docs',
    type=int,
    help='Docs rows number per account',
    default=0
)
parsed_args = parser.parse_args()
dbtype = parsed_args.dbtype

dirpath = os.path.join(os.path.dirname(os.path.realpath(__file__)), "csv", parsed_args.dbtype.value)
os.makedirs(dirpath, exist_ok = True)

fixed_date = datetime.datetime.now(datetime.timezone.utc)
formatted_date = fixed_date.strftime('%Y-%m-%dT%H:%M:%S.%f')[:-3] + 'Z'
date_string = f',"{formatted_date}"'

with open(os.path.join(dirpath, "t_account.csv"), "w") as accountsFile:
    with open(os.path.join(dirpath, "t_balance.csv"), "w") as balanceFile:
        for i in range(0, parsed_args.accounts):
            customerid = randomString(20)
            accid = uuid7str()
            accountsFile.write(f'{quoted(accid)},"{customerid}","{randomNumericString(20)}","RUR","filial1",2016-01-01,true,false,false,false,"",{formatted_date}\n')
            balanceFile.write(f'{quoted(accid)},"CUSCOR",{randomNumericString(20)},{formatted_date}\n')

                # with  open(os.path.join(dirpath, "t_posting.csv"), "w") as pstFile:

                    # # запись данных для ФТ

                    # accid1 = uuid7str()
                    # accountsFile.write(
                    #     f'{quoted(accid1)},"cus1","01234567890123456789","RUR","filial1",{formatted_date},true,false,false,false,"",{formatted_date}\n'
                    # )
                    # balanceFile.write(
                    #     f'{quoted(accid1)},"CURBAL",10,{formatted_date}\n'
                    # )
                    # accid2 = uuid7str()
                    # accountsFile.write(
                    #     f'{quoted(accid2)},"cus1","11234567890123456789","RUR","filial1",{formatted_date},true,false,false,false,"",{formatted_date}\n'
                    # )
                    # balanceFile.write(
                    #     f'{quoted(accid2)},"CURBAL",10,{formatted_date}\n'
                    # )

                    # запись данных для НТ


                        # for j in range(0, parsed_args.docs):
                        #     docid = uuid7str()
                        #     pstFile.write(
                        #         f'{docid},1,2016-01-01,2016-01-02,{quoted(accid)},"{randomString(10)}",{100*random.randrange(100)},{100*random.randrange(100)},{formatted_date}\n'
                        # )
                        #     pstFile.write(
                        #         f'{docid},2,2016-01-01,2016-01-02,{quoted(accid)},"{randomString(10)}",{100*random.randrange(100)},{100*random.randrange(100)},{formatted_date}\n' 
                        # )
