#!/usr/bin/python
# coding=utf-8

import sys
import json
import time


def main(argv):

    reload(sys)

    sys.setdefaultencoding('utf8')

    for line in sys.stdin:

        if line.strip() == "":

            continue

        json_content = json.loads(line.strip())

        text = ''.join(item + " " for item in json_content["text"].split()).strip()

        time_content = json_content['created_at'][0 : -11] + json_content['created_at'][-5 :]

        time_origin = time.strptime(time_content, "%a %b %d %X %Y")

        time_standard = time.strftime("%Y-%m-%d+%X", time_origin)

        user_id = json_content['user']['id_str']

        twitter_id = json_content['id_str']

        result = user_id + '\t' + time_standard + '\t' + twitter_id + '\t' + text

        print result

if __name__ == "__main__":
    main(sys.argv)

