#!/usr/bin/python

import sys
import os
import json
import time

def myCmp(string1, string2):
    return int(string1) - int(string2)

def main(argv):
    for line in sys.stdin:
        if not line.strip():
            continue
        line = line.strip()
        (origin_id, retweet_ids) = line.split('\t')
        retweet_ids_list = retweet_ids.split(',')
        retweet_ids_list.sort(myCmp)
        info = origin_id + '\t' + ','.join(id for id in retweet_ids_list)
        print info

if __name__ == "__main__":
    main(sys.argv)
