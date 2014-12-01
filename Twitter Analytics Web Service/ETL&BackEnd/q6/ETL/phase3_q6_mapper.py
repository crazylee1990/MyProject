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
        (user_id, photo_num) = line.split('\t')
        user_id = (10 - len(user_id)) * '0' + user_id
        info = user_id + '\t' + photo_num
        print info

if __name__ == "__main__":
    main(sys.argv)
