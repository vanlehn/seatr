from django.core.management.base import BaseCommand, CommandError
from questions.models import Questions, Category, QuestionsCategoryCourseMap
from courses.models   import Courses

import pymysql.cursors


class Command(BaseCommand):
    help       = '                                                                            \
                    populate SEATR QuestionsCategoryCourseMap                                 \
                    takes in the class id as an argument                                      \
                    algorithm: takes the Questions and Category (of the class) and maps them  \
                    using the class_question table of OPE                                     \
                 '
    assumption = ''
    usage      = 'python manage.py populateQuestionsCategoryCourseMap 45'
    host       = 'ope-dev.cae0huknrosy.us-east-1.rds.amazonaws.com'
    user       = 'opemaster'
    password   = 'GHNXPIBTSKHLFIJFDLJAPBSDXMRQUJ'
    db         = 'ope_class_'
    charset    = 'utf8mb4'

    def add_arguments(self, parser):
        # positional argument
        parser.add_argument('class_id', type=int)

    def handle(self, *args, **options):
        class_id = options["class_id"]
        connectionClass = pymysql.connect(host=self.host,
                                    user=self.user,
                                    password=self.password,
                                    db=self.db + str(class_id),
                                    charset=self.charset,
                                    cursorclass=pymysql.cursors.DictCursor)
        with connectionClass.cursor() as cursor:
            # get all the class_question
            sqlStatement = "select question_id, category_id from class_question"
            cursor.execute(sqlStatement)
            categories               = Category.objects.all()
            questions                = Questions.objects.all()
            opeQuestionCategoryMap   = cursor.fetchall()
            questionIdquestionMap    = {}
            categoryIdCategoryMap    = {}
            seatrQuestionCategoryMap = {}

            # create a map between questionId -> question
            for question in questions:
                questionIdquestionMap[question.external_id] = question

            # craete a map between categoryId -> category
            for category in categories:
                categoryIdCategoryMap[category.external_id] = category

            # create a map berween category_id -> question_id
            for item in opeQuestionCategoryMap:
                seatrQuestionCategoryMap[item["category_id"]] = item["question_id"]
            
            course = Courses.objects.get(external_id=class_id)

            # final merge to create the QuestionsCategoryMap
            questionsCategoryCourseMap = []
            for x in opeQuestionCategoryMap:
                category_id = x["category_id"]
                question_id = x["question_id"]
                questionsCategoryCourseMap.append(QuestionsCategoryCourseMap(category=categoryIdCategoryMap[category_id], 
                                                                        question=questionIdquestionMap[question_id],
                                                                        course=course))
            QuestionsCategoryCourseMap.objects.bulk_create(questionsCategoryCourseMap)