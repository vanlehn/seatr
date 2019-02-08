from django.db import models

from students  import models as studentsModel
from questions import models as questionsModel


class KCs(models.Model):
    pass


class KCsStudentsMap(models.Model):
    student = models.ForeignKey(studentsModel.Students, on_delete=models.PROTECT)
    kc      = models.ForeignKey(KCs, on_delete=models.PROTECT)


class KCsQuestionsMap(models.Model):
    kc       = models.ForeignKey(KCs, on_delete=models.PROTECT)
    question = models.ForeignKey(questionsModel.Questions, on_delete=models.PROTECT)





