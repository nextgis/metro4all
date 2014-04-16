#!/usr/bin/env python -u
# -*- coding: utf-8 -*-
# ---------------------------------------------------------------------------
# get_from_gdrive.py
# Author: Maxim Dubinin (sim@gis-lab.info)
# About: Download a file from Google Drive
# Created: 11.04.2014
# Usage example: python get_from_gdrive.py 92.svg
# ---------------------------------------------------------------------------
# ---------------------------------------------------------------------------
# Getting started, before running the script:
# 1. Install Pydrive
# 2. Go to https://code.google.com/apis/console and create project
# 3. Go to Credentials, Web applications, Edit, enter http://localhost:8080/ for both ‘Redirect URIs’ and ‘JavaScript origins’.
# 4. Download JSON to working folder
# 5. Add settings.yaml to working folder
# More: http://pythonhosted.org/PyDrive/quickstart.html
# ---------------------------------------------------------------------------

import sys

from pydrive.auth import GoogleAuth
from pydrive.drive import GoogleDrive

file_name = sys.argv[1]

gauth = GoogleAuth()
gauth.LocalWebserverAuth()

drive = GoogleDrive(gauth)
file_list = drive.ListFile({'q': "title contains '" + file_name + "' and trashed = False"}).GetList()

svg = (item for item in file_list if item['title'] == file_name).next()
file = drive.CreateFile({'id': svg['id']})
print 'Downloading file %s from Google Drive' % svg['title']

file.GetContentFile(file_name) #sometimes mimetype='application/pdf'