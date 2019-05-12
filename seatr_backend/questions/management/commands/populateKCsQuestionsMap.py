from django.core.management.base import BaseCommand, CommandError
from questions.models import *

import pymysql.cursors


class Command(BaseCommand):
    help     = '                                                                                                       \
                    populate SEATR KCsQuestionsMap from SEATR Questions, KCs and OPE question_knowledge_component      \
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
            sqlStatement = "select * from question_knowledge_component"
            cursor.execute(sqlStatement)
            
            opeKcQuestionMap = cursor.fetchall()
            kCsQuestionsMap  = []
            for x in opeKcQuestionMap:
                kcId       = x["knowledge_component_id"]
                questionId = x["question_id"]
                kCsQuestionsMap.append(KCsQuestionsMap(kc_id=kcId, question_id=questionId))

            KCsQuestionsMap.objects.bulk_create(kCsQuestionsMap)