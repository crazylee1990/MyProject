#!/usr/bin/env python

import sys

def main(argv):
    # define the current origin user and his retweet user list
    curr_origin_user_id = None
    retweet_user_ids = []
    
    for line in sys.stdin:
        if not line.strip():
            continue
        line = line.strip()
        # parse origin user and retweet user from input
        (origin_user_id, retweet_user_id) = line.split('\t', 1)
        # if the current user is the parsed user
        if curr_origin_user_id == origin_user_id:
            if retweet_user_id not in retweet_user_ids:
                retweet_user_ids.append(retweet_user_id)
        # set the previous user list and update the current
        else:
            if curr_origin_user_id:
                info = curr_origin_user_id + '\t' + ','.join(id for id in retweet_user_ids)
                print info
                
            curr_origin_user_id = origin_user_id
            retweet_user_ids = [retweet_user_id]

    if curr_origin_user_id:
        info = curr_origin_user_id + '\t' + ','.join(id for id in retweet_user_ids)
        print info


if __name__ == "__main__":
    main(sys.argv)
