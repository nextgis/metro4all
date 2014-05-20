#!/home/karavanjow/projects/metro4all/bin/python
from backend import get_app

app = get_app()

if __name__ == 'main':
    from flup.server.fcgi import WSGIServer
    WSGIServer(app).run()