import os
from datetime import datetime
import shutil

import yaml

from ReplacersScrips.ReplacerInWord import repace_table_result, insert_table_error_info, replace_images, \
    replace_description_for_graf
from SaverScripts.GrafanaRendererImageSaver import save_all_graf_from_grafana

# config #
from constants import (CONFIG_NAME)

# config #
base_path = '.'
config_file = os.path.join(base_path, CONFIG_NAME)
config = yaml.safe_load(open(config_file, 'r'))


def main():
    # # Создаем документ
    # doc_name = create_doc()
    # print(f"doc was created: {doc_name}")

    doc_name = "Report_absrnd10_10_2025_LoadTest_.docx"

    # # скачиваем графики
    # is_successful = save_all_graf_from_grafana()
    # #
    # if is_successful:
    #     print("All graf was download successful")
    # else:
    #     print("!!! ATTENTION !!! ERROR !!! Some graf was not download successful !!!")
    #
    # # заменяем картинки в документе новыми скачанными
    # replace_images(doc_name)
    #
    # # заменяем текст в таблице с результатами
    # repace_table_result(doc_name)
    #
    # # заменяем текст в таблице с ошибками
    # insert_table_error_info(doc_name)

    # доверяем ИИ описывать все графики
    replace_description_for_graf(doc_name)


def create_doc():
    # Копируем исходный файл DOCX под новым именем
    docx_path = 'ReportTemplate.docx'

    new_docx_path = "Report_" + config["file_name"] + datetime.now().strftime("%d_%m_%Y") + "_" \
                    + config["typeTest"] + "_" + ".docx"

    shutil.copyfile(docx_path, new_docx_path)
    name_new_doc = new_docx_path

    return name_new_doc


if __name__ == '__main__':
    main()
