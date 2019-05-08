from .base import *

DEBUG = False

ALLOWED_HOSTS = ["*"]

DATABASES = {
    # default db is the SEATR database
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'OPTIONS': {
            # convert the warnings to errors to solve integrity issues
            'sql_mode': 'STRICT_TRANS_TABLES',
            'read_default_file': '/etc/mysql/seatr.conf',
            'init_command': 'SET foreign_key_checks = 0;',
        }

    },
    'ope': {
        'ENGINE': 'django.db.backends.mysql',
        'OPTIONS': {
            'sql_mode': 'STRICT_TRANS_TABLES',
            'read_default_file': '/etc/mysql/ope.conf',
        }
    }
}
