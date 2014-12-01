#!/usr/bin/env python

import sys

def main(argv):
    # define the current origin user and his retweet user list
    curr_user_id = None
    curr_total_score = 0
    curr_score = None
    for line in sys.stdin:
        if not line.strip():
            continue
        line = line.strip()
        # parse user and score from input
        (user_id, score) = line.split('\t')

        # if the current user is the parsed user
        if curr_user_id == user_id:
            curr_score += int(score)
        # set the previous user info and update the current
        else:
            if curr_user_id:
                for i in xrange(len(curr_user_id)):
                    if curr_user_id[i] != '0':
                        break
                curr_user_id = curr_user_id[i : ]
                
                info = curr_user_id + "\t" + str(curr_score) + '\t' + str(curr_score + curr_total_score)
                print info
                if int(curr_user_id) + 1 != int(user_id):
                    for i in xrange(1, int(user_id) - int(curr_user_id)):
                        print str(int(curr_user_id) + i) + "\t" + '0' + '\t' + str(curr_score + curr_total_score)
            if curr_score:
                curr_total_score += curr_score
            curr_user_id = user_id
            curr_score = int(score)
            
    if curr_user_id:
        for i in xrange(len(curr_user_id)):
            if curr_user_id[i] != '0':
                break
        curr_user_id = curr_user_id[i : ]
        info = curr_user_id + "\t" + str(curr_score) + '\t' + str(curr_score + curr_total_score)
        print info

if __name__ == "__main__":
    main(sys.argv)
