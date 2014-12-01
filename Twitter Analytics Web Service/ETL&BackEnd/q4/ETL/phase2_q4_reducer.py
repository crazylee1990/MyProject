#!/usr/bin/env python
# coding=utf-8

import sys


def myCmp(string1, string2):
    return int(string1) - int(string2)


def myCmp1(dic1, dic2):
    if dic1['number'] != dic2['number']:
        return dic2['number'] - dic1['number']
    else:
        if dic1['id'][0] != dic2['id'][0]:
            return int(dic1['id'][0]) - int(dic2['id'][0])
    return int(dic1['index']) - int(dic2['index'])

def main(argv):
    reload(sys)
    sys.setdefaultencoding('utf8')
    # initial
    curr_sign = None
    curr_map = dict()
    for line in sys.stdin:
        if not line.strip():
            continue
        line = line.strip()
        # parse input to fields
        (sign, tag, tweet_id, index) = line.split('\t')
        if curr_sign == sign:
            if tag in curr_map:
                if tweet_id not in curr_map[tag]['id']:
                    if len(curr_map[tag]['id']) > 0 and int(tweet_id) < int(curr_map[tag]['id'][0]):
                        curr_map[tag]['index'] = index
                    curr_map[tag]['id'].append(tweet_id)
                    curr_map[tag]['id'].sort(myCmp)
                    curr_map[tag]['number'] += 1
            else:
                curr_map[tag] = dict()
                curr_map[tag]['name'] = tag
                curr_map[tag]['id'] = [tweet_id]
                curr_map[tag]['number'] = 1
                curr_map[tag]['index'] = index
                
        # print previous one and update the current
        else:
            if curr_sign:
                tag_list = []
                for key in curr_map:
                    tag_list.append(curr_map[key])
                tag_list.sort(myCmp1)
                new_index = 1
                for dic in tag_list:
                    dic['id'].sort(myCmp)
                    info = curr_sign + '\t' + dic['name'] + '\t' + ','.join(id for id in dic['id']) + '\t' + str(new_index)
                    print info
                    new_index += 1
                
            curr_sign = sign
            curr_map = dict()
            curr_map[tag] = dict()
            curr_map[tag]['name'] = tag
            curr_map[tag]['id'] = [tweet_id]
            curr_map[tag]['number'] = 1
            curr_map[tag]['index'] = index

    if curr_sign:
        tag_list = []
        for key in curr_map:
            tag_list.append(curr_map[key])
            tag_list.sort(myCmp1)
            new_index = 1
            for dic in tag_list:
                dic['id'].sort(myCmp)
                info = curr_sign + '\t' + dic['name'] + '\t' + ','.join(id for id in dic['id']) + '\t' + str(new_index)
                print info
                new_index += 1


if __name__ == "__main__":
    main(sys.argv)
