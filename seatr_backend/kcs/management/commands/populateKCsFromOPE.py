from django.core.management.base import BaseCommand, CommandError
from kcs.models import KCs

import pymysql.cursors
import csv
from collections import defaultdict


class Command(BaseCommand):
    help     = 'populate SEATR KCs from OPE KCs'
    host     = 'ope-dev.cae0huknrosy.us-east-1.rds.amazonaws.com'
    user     = 'opemaster'
    password = 'GHNXPIBTSKHLFIJFDLJAPBSDXMRQUJ'
    db       = 'ope_global'
    charset  = 'utf8mb4'

    def handle(self, *args, **options):
        connection = pymysql.connect(host=self.host,
                                     user=self.user,
                                     password=self.password,
                                     db=self.db,
                                     charset=self.charset,
                                     cursorclass=pymysql.cursors.DictCursor)
        with connection.cursor() as cursor:
            sqlStatement = "select id from knowledge_component"
            cursor.execute(sqlStatement)
            kcs = [KCs(id=int(x["id"])) for x in cursor.fetchall()]
            KCs.objects.bulk_create(kcs)
