#!/usr/bin/python
# coding=utf-8

import sys
import os
import json
import time

def findPlace(json_content):
    if "place" in json_content:
        if json_content['place']:
            if json_content['place']['name']:
                return json_content['place']['name']
    if 'time_zone' in json_content['user']:
        if json_content['user']['time_zone']:
            if 'time' not in json_content['user']['time_zone'].lower():
                return json_content['user']['time_zone']
    return None

def main(argv):
    reload(sys)
    sys.setdefaultencoding('utf8')
    for line in sys.stdin:
        if not line.strip():
            continue
        line = line.strip()
        json_content = json.loads(line)
        if not findPlace(json_content):
            continue
        location = findPlace(json_content)
        # check hashtags
        if not 'hashtags' in json_content['entities'] or not json_content['entities']['hashtags']:
            continue
        tag_list = json_content['entities']['hashtags']
        
        time_content = json_content['created_at'][0 : -11] + json_content['created_at'][-5 :]
        time_origin = time.strptime(time_content, "%a %b %d %X %Y")
        time_standard = time.strftime("%Y-%m-%d", time_origin)
        twitter_id = json_content['id_str']
        index = 0
        for dic in tag_list:
            print time_standard + location + '\t' + dic['text'] + '\t' + twitter_id + '\t' + str(index)
            index += 1
    
if __name__ == "__main__":
    main(sys.argv)
