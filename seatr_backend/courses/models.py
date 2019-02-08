from django.db import models

from analyzers import models as analyzersModel

from kcs       import models as kcsModel
from students  import models as studentsModel
from questions import models as questionsModel


class Courses(models.Model):
    external_id = models.IntegerField(unique=True, null=False)
    description = models.CharField(null=True, max_length=2000)


class CoursesKcsMap(models.Model):
    course = models.ForeignKey(Courses, on_delete=models.PROTECT)
    kc     = models.ForeignKey(kcsModel.KCs, on_delete=models.PROTECT)


class CourseQuestionMap(models.Model):
    course   = models.ForeignKey(Courses, on_delete=models.PROTECT)
    question = models.ForeignKey(questionsModel.Questions, on_delete=models.PROTECT)


class CoursesStudentsMap(models.Model):
    course  = models.ForeignKey(Courses, on_delete=models.PROTECT)
    student = models.ForeignKey(studentsModel.Students, on_delete=models.PROTECT)





