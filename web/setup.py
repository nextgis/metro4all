from setuptools import setup, find_packages

requires = [
    'geojson',
    'bottle',
    'pyparsing==1.5.7',
    'networkx',
    'waitress',
    'flup'
]

setup(name='metro4all',
      version='0.0',
      description='metro4all',
      classifiers=[
        "Programming Language :: Python",
        "Framework :: Pylons",
        "Topic :: Internet :: WWW/HTTP",
        "Topic :: Internet :: WWW/HTTP :: WSGI :: Application",
        ],
      author='',
      author_email='',
      url='',
      keywords='web wsgi bfg pylons',
      packages=find_packages(),
      include_package_data=True,
      zip_safe=False,
      test_suite='metro4all',
      install_requires = requires,
      entry_points = """"""
)