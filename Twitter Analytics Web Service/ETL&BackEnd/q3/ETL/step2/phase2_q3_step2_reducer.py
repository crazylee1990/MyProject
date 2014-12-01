#!/usr/bin/env python

import sys

def main(argv):
    for line in sys.stdin:
        if not line.strip():
            continue
        line = line.strip()
        print line


if __name__ == "__main__":
    main(sys.argv)
