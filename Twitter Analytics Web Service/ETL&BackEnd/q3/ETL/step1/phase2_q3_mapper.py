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
        # if this post is retweeted from other users
        if 'retweeted_status' in json_contont:
            origin_status = json_contont['retweeted_status']
            origin_user_id = origin_status['user']['id_str']
            retweet_user_id = json_contont['user']['id_str']
            # make info with origin and retweet users
            info = origin_user_id +'\t' + retweet_user_id
            print info

if __name__ == "__main__":
    main(sys.argv)
