#Deployment

For deploying web-app you will need Python 2.7.x on your machine. First of all install `virtualenv`:
```Bash
sudo apt-get install python-virtualenv
```
Then create virtual enviroment for our web-app:
```Bash
virtualenv metro4allEnv --no-site-packages
```
Move to created directory and clone there this repo:
```Bash
cd metro4allEnv
git clone https://github.com/nextgis/metro4all.git
```
In cloned repo go to web subdirectory and install all dependencies via `setup.py` script:
```Bash
cd metro4all/web
../../bin/python setup.py develop
```

#Running server
Go to `metro4all/web` and start server:
```Bash
../../bin/python backend.py
```
Now you can reach server on `http://0.0.0.0:8088/msk`
You can change `/msk` to another city.
