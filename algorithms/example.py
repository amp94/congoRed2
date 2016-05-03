#!/usr/bin/python

# Hello world python program

import sys

x = str(sys.argv[1]).split(",")
print 'Number of images:', len(x)

for i in range(len(x)):
    print i,":",x[i]

sys.stdout.flush()