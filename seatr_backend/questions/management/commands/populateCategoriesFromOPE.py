from django.core.management.base import BaseCommand, CommandError
from questions.models import Questions, Category, CategoryCourseMap
from courses.models   import Courses

import pymysql.cursors


class Command(BaseCommand):
    help     = '                                            \
                    populate SEATR Category from OPE Category \
                    takes in the class id as an argument \
                    algorithm: takes ope_class_45.category and populates the seatr.Category \
                '
    usage    = 'python manage.py populateCategoriesFromOPE 45'
    host     = 'ope-dev.cae0huknrosy.us-east-1.rds.amazonaws.com'
    user     = 'opemaster'
    password = 'GHNXPIBTSKHLFIJFDLJAPBSDXMRQUJ'
    db       = 'ope_class_'
    charset  = 'utf8mb4'

    def add_arguments(self, parser):
        # ppsitional argument
        parser.add_argument('class_id', type=int)

        # Named (optional) arguments
        # parser.add_argument(
        #     '--delete',
        #     action='store_true',
        #     help='Delete poll instead of closing it',
        # )

    def handle(self, *args, **options):
        class_id = options["class_id"]
        connectionClass = pymysql.connect(host=self.host,
                                    user=self.user,
                                    password=self.password,
                                    db=self.db + str(class_id),
                                    charset=self.charset,
                                    cursorclass=pymysql.cursors.DictCursor)
        with connectionClass.cursor() as cursor:
            sqlStatement = "select * from category"
            cursor.execute(sqlStatement)
            categories = [Category(external_id=int(x["id"]), parent_id=x["parent_id"], order=x["order"]) for x in cursor.fetchall()]
            Category.objects.bulk_create(categories)

            # extract the external_id from categories and create the CategoryCourseMap
            course = Courses.objects.get(external_id=class_id)
            categoryCourseMaps = [CategoryCourseMap(category=category, course=course) for category in categories]
            CategoryCourseMap.objects.bulk_create(categoryCourseMaps)