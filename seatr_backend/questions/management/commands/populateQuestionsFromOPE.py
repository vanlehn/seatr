from django.core.management.base import BaseCommand, CommandError
from questions.models import Questions

import pymysql.cursors


class Command(BaseCommand):
    help     = '                                            \
                    populate SEATR Questions from OPE Questions \
                    algorithm: takes ope_global.questions and fills the seatr.Questions table \
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
            sqlStatement = "select id from question"
            cursor.execute(sqlStatement)
            questions = [Questions(external_id=int(x["id"])) for x in cursor.fetchall()]
            Questions.objects.bulk_create(questions)