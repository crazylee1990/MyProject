#!/usr/bin/python
# coding=utf-8


import os
import sys
import urllib

def main(argv):

    reload(sys)

    sys.setdefaultencoding('utf8')

    dictionary = dict()

    afinn_handle = urllib.urlopen("https://s3.amazonaws.com/15619.phase1/AFINN/AFINN.txt")

    for line in afinn_handle:

        if line.strip() != "":

            key_value = line.strip().split('\t')

            dictionary[key_value[0]] = int(key_value[1])

    afinn_handle.close()

    banner = set()

    ban_handle = urllib.urlopen("https://s3.amazonaws.com/15619.phase1/banned/plainBanned.txt")

    for line in ban_handle:

        if line.strip() != "":

            banner.add(line.strip())
            
    ban_handle.close()

    for line in sys.stdin:

        if line.strip() == "":

            continue

        line_list = line.strip().split('\t')

        score = 0

        (user_id, time, twitter_id, text) = (line_list[0], line_list[1], line_list[2], line_list[3])

        original_text = text

        new_text = ""

        for char in text:

            if not char.isalpha() and not char.isdigit():

                new_text += " "

            new_text += char

        new_text_list = new_text.split()

        for word in new_text_list:

            if word in dictionary:

                score += dictionary[word]

        thisWord = ""

        revised_text = ""

        original_text += " "

        for i in xrange(len(original_text)):

            if original_text[i].isalpha() or original_text[i].isdigit():

                thisWord += original_text[i]

            else:

                if thisWord != "" and thisWord.lower() in banner:

                    revised_text += thisWord[0] + "*" * (len(thisWord) - 2) + thisWord[-1]

                else:

                    revised_text += thisWord

                revised_text += original_text[i]

                thisWord = ""

        revised_text = revised_text.strip()

        information = user_id + "\t" + time + "\t" + twitter_id + "\t" + str(score) + "\t" + revised_text

        print information

if __name__ == "__main__":
    main(sys.argv)
