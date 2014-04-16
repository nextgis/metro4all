#!/usr/bin/env python -u
# -*- coding: utf-8 -*-
# ---------------------------------------------------------------------------
# prepare_schemes.py
# Author: Maxim Dubinin (sim@gis-lab.info)
# About: For an SVG download it from GDrive, turn off layers, convert to png, put png in a repo folder, add and push to repo.
# Created: 12.04.2014
# Usage example for one scheme:   python prepare_schemes.py 92 msk
# Usage example for many schemes: python prepare_schemes.py 92,80,132 msk
#                             or: python prepare_schemes.py ALL msk
# ---------------------------------------------------------------------------

import shutil
import os,sys

def process_svg(fn,git_folder,png_folder):

    fn = fn + ".svg"
    fn_png = fn.replace(".svg",".png")

    #get file from GDrive
    cmd = "python get_from_gdrive.py " + fn
    print(cmd)
    os.system(cmd)
    #TODO optionally get file from GitHub

    #turn off layers
    cmd = "python turn-svglayers-off.py " + fn
    print(cmd)
    os.system(cmd)

    #convert to png
    cmd = "inkscape -d 150 -b white -f " + fn + " -e " + git_folder + png_folder + fn_png
    print(cmd)
    os.system(cmd)

    #convert to png
    cmd = "Remove " + fn
    print(cmd)
    os.remove(fn)

    #chdir to git folder
    os.chdir(git_folder)

    #add new scheme
    cmd = "git add " + png_folder + fn_png
    print(cmd)
    os.system(cmd)

if __name__ == '__main__':
    args = sys.argv[ 1: ]
    fns = args[0]
    city = args[1]
    git_folder = "/home/sim/work/metro4all/metroaccess/"
    utils_folder = git_folder + "utils"
    png_folder = "data/" + city + "/schemes/"

    commit_str = []
    if fns == "ALL":
        print "ALL was used"
    else:
        for fn in fns.split(","):
            os.chdir(utils_folder)
            process_svg(fn,git_folder,png_folder)
            commit_str.append(fn)
    
    #commit
    cmd = "git commit -m 'new/updated scheme(s) " + ",".join(commit_str) + "'"
    print(cmd)
    os.system(cmd)