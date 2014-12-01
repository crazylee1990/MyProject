#!/usr/bin/python

import sys
import os
import json
import time


def main(argv):
    for line in sys.stdin:
        if not line.strip():
            continue
        line = line.strip()
        json_contont = json.loads(line)
        retweet_user_id = json_contont['user']['id_str']
        type1_info = retweet_user_id + "#1" + "\t" + "1"
        print type1_info
        # if this post is retweeted from other users
        if 'retweeted_status' in json_contont:
            origin_status = json_contont['retweeted_status']
            origin_user_id = origin_status['user']['id_str']
            type2_info = origin_user_id + "#2" + "\t" + "3"
            print type2_info

if __name__ == "__main__":
    main(sys.argv)
