#!/usr/bin/env python
# coding=utf-8

import sys


def main(argv):

    reload(sys)

    sys.setdefaultencoding('utf8')

    for line in sys.stdin:

        print line.strip()

if __name__ == "__main__":
    main(sys.argv)

