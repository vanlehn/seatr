from django.core.management.base import BaseCommand, CommandError

from kcs.models       import KCs, KCsQuestionsMap
from questions.models import Questions

import pymysql.cursors
from collections import defaultdict


class Command(BaseCommand):
    help     = 'populate SEATR KCsQuestionsMap from OPE KCs'
    host     = 'ope-dev.cae0huknrosy.us-east-1.rds.amazonaws.com'
    user     = 'opemaster'
    password = 'GHNXPIBTSKHLFIJFDLJAPBSDXMRQUJ'
    db       = 'ope_global'
    charset  = 'utf8mb4'

    # problem:
    # need to find all the questions and their corresponding kcs which are needed to be put into the KCsQuestionsMap table
    # algorithm
    # 1. fetch all the entries of question_knowledge_component table in OPE
    # 2. map all kcs that belong to a question
    # 3. fetch all the Questions and KCs from SEATR that have entry in question_knowledge_component
    #    the question to kc mapping in step 2 are done based on question.external
    # 4.

    def handle(self, *args, **options):
        connection = pymysql.connect(host=self.host,
                                     user=self.user,
                                     password=self.password,
                                     db=self.db,
                                     charset=self.charset,
                                     cursorclass=pymysql.cursors.DictCursor)
        with connection.cursor() as cursor:
            sqlStatement = "select * from question_knowledge_component"
            cursor.execute(sqlStatement)
            questionKnowledgeComponentMapsList = cursor.fetchall()

        questionExternalIds   = [point["question_id"]  for point in questionKnowledgeComponentMapsList]
        knowledgeComponentIds = [point["knowledge_component_id"] for point in questionKnowledgeComponentMapsList]
        questionKnowledgeComponentMap = defaultdict(list)
        for point in questionKnowledgeComponentMapsList:
            # key is OPE's question.id which is same as SEATR's questions.external_id
            # SEATR's kc.id is same as OPE's kc.id
            questionKnowledgeComponentMap[point["question_id"]].append(point["knowledge_component_id"])

        questions = Questions.objects.filter(external_id__in=questionExternalIds)
        kcs       = KCs.objects.filter(id__in=knowledgeComponentIds)
        kcIdToKcMap = {}
        for kc in kcs:
            kcIdToKcMap[kc.id] = kc

        temp = []
        # for all questions in SEATR
        for question in questions:
            questionExternalId = question.external_id
            # for all the knowledgeComponents of that question, SEATR's external_id = OPE's id
            for kc in questionKnowledgeComponentMap[questionExternalId]:
                temp.append(KCsQuestionsMap(question=question, kc=kcIdToKcMap[kc]))
        KCsQuestionsMap.objects.bulk_create(temp)





