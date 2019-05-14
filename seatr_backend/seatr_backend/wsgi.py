"""
WSGI config for seatr_backend project.

It exposes the WSGI callable as a module-level variable named ``application``.

For more information on this file, see
https://docs.djangoproject.com/en/2.1/howto/deployment/wsgi/
"""

import os

if "PROD" in os.environ:
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'seatr_backend.settings.production')
else:
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'seatr_backend.settings.dev')


from django.core.wsgi import get_wsgi_application

application = get_wsgi_application()
