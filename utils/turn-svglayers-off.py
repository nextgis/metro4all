#!/usr/bin/env python -u
# -*- coding: utf-8 -*-
# ---------------------------------------------------------------------------
# turn-svglayers-off.py
# Author: Maxim Dubinin (sim@gis-lab.info)
# About: For each SVG file in a set folder, turn off visibility for specified layers.
# Created: 07.04.2014
# Usage example: python turn-svglayers-off.py /usr/local/work/svg/92.svg
# ---------------------------------------------------------------------------

import shutil
import os,sys

args = sys.argv[ 1: ]
fn = args[0]

turn_off_layers = {'photos','pointers','element-ids','exits'}

f = open(fn)
fn_out = fn.replace(".svg","_out.svg")
f_out = open(fn_out, "wb")
curlayer = ""
change = "no"

for ss in f.readlines():
    
    if 'inkscape:label=' in ss:
        curlayer = ss.replace('inkscape:label=','')
        curlayer = curlayer.replace('"','')
        curlayer = curlayer.replace('\n','').strip()

    #if layer is empty, it won't have style=display and won't be turned off
    if 'style="display:' in ss and len(ss) < 30:
        if curlayer in turn_off_layers:
            ss = ss.replace("inline","none")
            print "File: " + fn + ", Layer: " + curlayer+ " changed. String: " + ss.strip().replace("\n","")
            change = "yes"

    f_out.write(ss)

f.close()
f_out.close()

if change == "yes":
    shutil.move(fn_out,fn)