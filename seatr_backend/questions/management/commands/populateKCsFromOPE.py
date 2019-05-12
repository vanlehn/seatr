from django.core.management.base import BaseCommand, CommandError
from questions.models import *

import pymysql.cursors


class Command(BaseCommand):
    help     = '                                                                                    \
                    populate SEATR KCs from OPE knowledge_component                                 \
                    algorithm: takes ope_global.knowledge_component and fills the seatr.KCs table   \
                '
    host     = 'ope-dev.cae0huknrosy.us-east-1.rds.amazonaws.com'
    user     = 'opemaster'
    password = 'GHNXPIBTSKHLFIJFDLJAPBSDXMRQUJ'
    db       = 'ope_global'
    charset  = 'utf8mb4'

    def handle(self, *args, **options):
        connectionGlobal = pymysql.connect(host=self.host,
                                    user=self.user,
                                    password=self.password,
                                    db=self.db,
                                    charset=self.charset,
                                    cursorclass=pymysql.cursors.DictCursor)
        with connectionGlobal.cursor() as cursor:
            sqlStatement = "select id, importance from knowledge_component"
            cursor.execute(sqlStatement)
            kcs = [KCs(external_id=x["id"], importance=x["importance"]) for x in cursor.fetchall()]            
            KCs.objects.bulk_create(kcs)

