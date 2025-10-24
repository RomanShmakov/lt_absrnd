import datetime
import os
fixed_date = datetime.datetime.now(datetime.timezone.utc)
formatted_date = fixed_date.strftime('%Y-%m-%dT%H:%M:%S.%f')[:-3] + 'Z'
date_string = f',"{formatted_date}"'
files_to_process = [
    't_branch.csv',
    't_ccy.csv',
    't_ccy_rate.csv',
    't_filial.csv',
    't_parameter.csv'
]
for filename in files_to_process:
    if not os.path.exists(filename):
        print(f"File {filename} not found, skip it")
        continue
    with open(filename, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    new_lines = []
    for line in lines:
        line = line.rstrip('\n\r') 
        if line:
            new_line = line + date_string + '\n'
        else:
            new_line = line + '\n'
        new_lines.append(new_line)
    with open(filename, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)