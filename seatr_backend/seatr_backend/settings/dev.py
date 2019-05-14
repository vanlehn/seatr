from .base import *

DEBUG = True

ALLOWED_HOSTS = ['*']
print("\n\n****** running in development environment ******\n\n")
DATABASES = {
     'default': {
         'ENGINE': 'django.db.backends.sqlite3',
         'NAME': os.path.join(BASE_DIR, 'db.sqlite3'),
     }
 }
