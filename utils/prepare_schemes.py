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

def process_svg(fn,git_folder,png_folder_main,png_folder_layers,png_folder_numbers):

    fn_main = fn + "_main.svg"
    fn_layers = fn + "_layers.svg"
    fn_numbers = fn + "_numbers.svg"
    fn = fn + ".svg"
    fn_png = fn.replace(".svg",".png")

    #get file from GDrive
    cmd = "python get_from_gdrive.py " + fn
    print(cmd)
    os.system(cmd)
    #TODO optionally get file from GitHub

    #make copies
    shutil.copy(fn,fn_main)
    shutil.copy(fn,fn_layers)
    shutil.copy(fn,fn_numbers)

    #turn off layers
    cmd = "python turn-svglayers-off.py " + fn_main + " photos,pointers,element-ids,exits"
    print(cmd)
    os.system(cmd)
    cmd = "python turn-svglayers-off.py " + fn_layers + " photos,pointers,element-ids,exits,numbers"
    print(cmd)
    os.system(cmd)
    cmd = "python turn-svglayers-off.py " + fn_numbers + " photos,pointers,element-ids,exits,name_ru,name_en,scheme,meetcode,copyright"
    print(cmd)
    os.system(cmd)

    #convert to png - main
    cmd = "inkscape -d 150 -b white -f " + fn_main + " -e " + git_folder + png_folder_main + fn_png
    print(cmd)
    os.system(cmd)

    #convert to png - layers
    cmd = "inkscape -d 150 -b white -f " + fn_layers + " -e " + git_folder + png_folder_layers + fn_png
    print(cmd)
    os.system(cmd)

    #convert to png - numbers
    cmd = "inkscape -d 150 -f " + fn_numbers + " -e " + git_folder + png_folder_numbers + fn_png
    print(cmd)
    os.system(cmd)

    #convert to png
    cmd = "Remove *.svg"
    print(cmd)
    os.remove(fn)
    os.remove(fn_main)
    os.remove(fn_layers)
    os.remove(fn_numbers)

    #chdir to git folder
    os.chdir(git_folder)

    #add new scheme
    cmd = "git add " + png_folder_main
    print(cmd)
    os.system(cmd)

if __name__ == '__main__':
    args = sys.argv[ 1: ]
    fns = args[0]
    city = args[1]
    git_folder = "/home/sim/work/metro4all/repo/"
    utils_folder = git_folder + "utils"
    png_folder_main = "data/" + city + "/schemes/"
    png_folder_layers = "data/" + city + "/schemes/layers/"
    png_folder_numbers = "data/" + city + "/schemes/numbers/"

    commit_str = []
    if fns == "ALL":
        print "ALL was used"
    else:
        for fn in fns.split(","):
            os.chdir(utils_folder)
            process_svg(fn,git_folder,png_folder_main,png_folder_layers,png_folder_numbers)
            commit_str.append(fn)
    
    #commit
    cmd = "git commit -m 'new/updated scheme(s) " + ",".join(commit_str) + "'"
    print(cmd)
    os.system(cmd)