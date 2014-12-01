#!/usr/bin/env python

import sys

def main(argv):
    # define the current origin user and his retweet user list
    curr_user_id = None
    curr_score = None
    
    for line in sys.stdin:
        if not line.strip():
            continue
        line = line.strip()
        # parse user and score from input
        (user_id, score) = line.split('\t', 1)
        # if the current user is the parsed user
        if curr_user_id == user_id:
            curr_score += int(score)
        # set the previous user info and update the current
        else:
            if curr_user_id:
                info = curr_user_id + "\t" + str(curr_score)
                print info
                
            curr_user_id = user_id
            curr_score = int(score)

    if curr_user_id:
        info = curr_user_id + "\t" + str(curr_score)
        print info


if __name__ == "__main__":
    main(sys.argv)
